package org.tyler.husher.client;

import com.formdev.flatlaf.IntelliJTheme;
import javafx.application.Application;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.RootLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tyler.husher.client.user.Profile;
import org.tyler.husher.client.user.UserConfig;
import org.tyler.husher.client.user.UserData;
import org.tyler.husher.core.util.RandomIdProvider;
import org.tyler.husher.core.node.RelayNodeService;
import org.tyler.husher.client.ui.FxApp;
import org.tyler.husher.core.util.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.StreamCorruptedException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Husher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Husher.class);

    private static Path workingDir;
    private static boolean headless;
    private static int nodePort;
    private static char[] nodePassword;
    private static UserConfig userConfig;
    private static UserData userData;

    static {
        workingDir = Paths.get("");
    }

    private static void parseArguments(String... args) {
        ArgumentParser parser = ArgumentParsers.newFor("Husher").build();
        parser.addArgument("--dir").dest("dir").help("Specify user directory").type(Arguments.fileType().verifyIsDirectory().verifyCanWrite().verifyCanCreate().verifyCanRead()).setDefault("./");
        parser.addArgument("--headless").action(Arguments.storeTrue());
        parser.addArgument("--node-port").dest("port").help("Specify DHT node port").type(Integer.class).setDefault(0);
        parser.addArgument("--node-password").dest("password").help("Specify DHT node password").type(String.class).setDefault("");

        Namespace ns;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
            return;
        }

        setWorkingDir(Paths.get(ns.getString("dir")));
        setHeadless(ns.getBoolean("headless"));
        setNodePort(ns.getInt("port"));
        setNodePassword(ns.getString("password").toCharArray());
    }

    private static void initLogger() throws IOException {
        Layout patternLayout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
        RootLogger.getRootLogger().addAppender(new ConsoleAppender(patternLayout, ConsoleAppender.SYSTEM_OUT));

        Path latestLogFile = workingDir.resolve("latest.log");
        LOGGER.info("Enabling file logger on {}", latestLogFile.getFileName());

        RootLogger.getRootLogger().addAppender(new RollingFileAppender(patternLayout, latestLogFile.toAbsolutePath().toString(), false));
        LOGGER.info("Running Husher v{} on Java {}", HusherConstants.VERSION, System.getProperty("java.version"));
    }

    private static void loadConfig() {
        Path configPath = workingDir.resolve("config.dat");

        if (!Files.exists(configPath)) {
            try {
                Files.createFile(configPath);
                userConfig = new UserConfig(configPath);
                Files.write(configPath, SerializationUtils.serialize(userConfig));
            } catch (IOException e) {
                throw new RuntimeException("Failed to generate config file at \"" + configPath.toAbsolutePath() + "\"", e);
            }
        }

        if (userConfig == null) {
            try {
                byte[] objBytes = Files.readAllBytes(configPath);
                userConfig = (UserConfig) SerializationUtils.deserialize(objBytes);
                userConfig.init();
            } catch (InvalidClassException | StreamCorruptedException e) {
                userConfig = new UserConfig(configPath);
                LOGGER.warn("InvalidClassException: config has file has been reset");
            } catch (Exception e) {
                // TODO Improve exception readability
                /**
                 * java.lang.RuntimeException: Failed to read config file
                 * 	at org.tyler.husher.client.Husher.loadConfig(Husher.java:104)
                 * 	at org.tyler.husher.client.Husher.main(Husher.java:186)
                 * Caused by: java.io.StreamCorruptedException: invalid stream header: EFBFBDEF
                 * 	at java.base/java.io.ObjectInputStream.readStreamHeader(ObjectInputStream.java:936)
                 * 	at java.base/java.io.ObjectInputStream.<init>(ObjectInputStream.java:375)
                 * 	at org.tyler.husher.core.util.SerializationUtils.deserialize(SerializationUtils.java:15)
                 * 	at org.tyler.husher.client.Husher.loadConfig(Husher.java:98)
                 * 	... 1 more
                 */

                throw new RuntimeException("Failed to read config file ", e);
            }
        }
    }

    private static void loadLookAndFeel() {
        IntelliJTheme.setup(ResourceUtils.openResource("styles/dark.theme.json"));
    }

    private static void launchGUI() {
        Class<? extends Application> fxAppClass = FxApp.class;
        LOGGER.info("Launching GUI from {}", fxAppClass.getName());
        Application.launch(fxAppClass);
    }

    private static void launchDHTNode() {
        new Thread(new RelayNodeService(), RelayNodeService.class.getName()).start();
    }

    public static UserData unlockProfile(Profile profile, String password) throws IOException, IllegalArgumentException {
        char[] hashedPassword = new String(HashUtils.sha256(password.getBytes(StandardCharsets.UTF_8))).toCharArray();
        UserData userData = UserData.fromFile(Path.of(profile.getPath()), hashedPassword);

        System.out.println("priv key: " + HexUtils.bytesToHex(userData.getPrivateKey()));
        System.out.println("pub key: " + HexUtils.bytesToHex(userData.getPublicKey()));
        System.out.println("username: " + userData.getUsername());
        System.out.println("id: " + userData.getPublicId());

        byte[] userHash = userData.getUserHash(userData.getPublicKey(), userData.getIdSalt(), userData.getUsername() + "", "SHA-1");

        System.out.println("user hash: " + HexUtils.bytesToHex(userHash).toLowerCase());
        return userData;
    }

    public static void setWorkingDir(Path workingDir) {
        Husher.workingDir = workingDir;
    }

    public static Path getWorkingDir() {
        return workingDir;
    }

    public static void setNodePort(int nodePort) {
        Husher.nodePort = nodePort;
    }

    public static int getNodePort() {
        return nodePort;
    }

    public static void setHeadless(boolean headless) {
        Husher.headless = headless;
    }

    public static boolean isHeadless() {
        return headless;
    }

    public static void setNodePassword(char[] nodePassword) {
        Husher.nodePassword = nodePassword;
    }

    public static char[] getNodePassword() {
        return nodePassword;
    }

    public static void setUserData(UserData userData) {
        Husher.userData = userData;
    }

    public static UserConfig getUserConfig() {
        return userConfig;
    }

    public static UserData getUserData() {
        return userData;
    }

    public static void main(String[] args) {
        try {
            parseArguments(args);
            initLogger();
            loadConfig();

            if (false) {
                userData = UserData.fromFile(workingDir.resolve("tyler2.hush"), new String(HashUtils.sha256("caca1".getBytes(StandardCharsets.UTF_8))).toCharArray());
                userData.getChannels().add(RandomIdProvider.generateBase62());
                userData.save();
                userData.getChannels().add(RandomIdProvider.generateBase62());
                userData.save();
                System.out.println(userData.getChannels().toString());
            }

            if (getNodePort() != 0) {
                launchDHTNode();
            }

            if (!isHeadless()) {
                loadLookAndFeel();
                launchGUI();
            }
        } catch (Throwable t) {
            LOGGER.error("Error while initializing app", t);

            if (!GraphicsEnvironment.isHeadless() && !isHeadless())
                JOptionPane.showConfirmDialog(null, "Error while initializing app:\n" + ExceptionUtils.throwableToString(t), "FATAL ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        }
    }
}
