package org.tyler.husher;

import com.formdev.flatlaf.IntelliJTheme;
import org.tyler.husher.protocol.model.User;
import org.tyler.husher.protocol.security.bip39.JavaxPBKDF2WithHmacSHA512;
import org.tyler.husher.protocol.security.bip39.MnemonicGenerator;
import org.tyler.husher.protocol.security.bip39.SeedCalculator;
import org.tyler.husher.protocol.security.bip39.Words;
import org.tyler.husher.protocol.security.bip39.wordlists.English;
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
import org.tyler.husher.client.UserData;
import org.tyler.husher.thread.DHTNodeThread;
import org.tyler.husher.ui.FxApp;
import org.tyler.husher.util.ExceptionUtils;
import org.tyler.husher.util.HashUtils;
import org.tyler.husher.util.HexUtils;
import org.tyler.husher.util.ResourceUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.List;
import java.util.Scanner;

public class Husher {

    public static final Husher INSTANCE = new Husher();
    private static final Logger LOGGER = LoggerFactory.getLogger(Husher.class);

    private Path dataDir;
    private boolean headless;
    private int nodePort;
    private char[] nodePassword;

    public Husher() {
        this.setDataDir(Paths.get(""));
    }

    private void parseArguments(String... args) {
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

        this.setDataDir(Paths.get(ns.getString("dir")));
        this.setHeadless(ns.getBoolean("headless"));
        this.setNodePort(ns.getInt("port"));
        this.setNodePassword(ns.getString("password").toCharArray());
    }

    private void initLogger() throws IOException {
        Layout patternLayout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
        RootLogger.getRootLogger().addAppender(new ConsoleAppender(patternLayout, ConsoleAppender.SYSTEM_OUT));

        Path latestLogFile = dataDir.resolve("latest.log");
        LOGGER.info("Enabling file logger on {}", latestLogFile.getFileName());

        RootLogger.getRootLogger().addAppender(new RollingFileAppender(patternLayout, latestLogFile.toAbsolutePath().toString(), false));
        LOGGER.info("Running Husher v{} on Java {}", HusherConstants.VERSION, System.getProperty("java.version"));
    }

    private void loadLookAndFeel() {
        IntelliJTheme.setup(ResourceUtils.openResource("styles/dark.theme.json"));
    }

    private void launchGUI() {
        Class<? extends Application> fxAppClass = FxApp.class;
        LOGGER.info("Launching GUI from {}", fxAppClass.getName());
        Application.launch(fxAppClass);
    }

    private void launchDHTNode() {
        new Thread(new DHTNodeThread(), DHTNodeThread.class.getName()).start();
    }

    private void start(String... args) {
        try {
            this.parseArguments(args);
            this.initLogger();

            if (false) {
                Scanner scanner = new Scanner(System.in);

                String username = "tyler";
                Path userDataFile = dataDir.resolve(username + ".hush");

                System.out.print("Enter the decryption password for " + userDataFile + ": ");
                String password = scanner.nextLine().trim();
                char[] hashedPassword = new String(HashUtils.sha256(password.getBytes(StandardCharsets.UTF_8))).toCharArray();

                StringBuilder sb = new StringBuilder();
                byte[] entropy = new byte[Words.TWELVE.byteLength()];
                new SecureRandom().nextBytes(entropy);
                new MnemonicGenerator(English.INSTANCE).createMnemonic(entropy, sb::append);

                String words = "surround effort bring chair various media brand common fit chief judge impose";
                // words = sb.toString();

                System.out.println("Recovery phrase: " + words);

                byte[] seed = new SeedCalculator(JavaxPBKDF2WithHmacSHA512.INSTANCE)
                        .withWordsFromWordList(English.INSTANCE)
                        .calculateSeed(List.of(words.split(" ")), "");

                byte[] privateKey = HashUtils.sha256(seed);

                UserData userData = new UserData(privateKey, username);

                System.out.println("priv key: " + HexUtils.bytesToHex(userData.getPrivateKey()));
                System.out.println("pub key: " + HexUtils.bytesToHex(userData.getPublicKey()));
                System.out.println("username: " + userData.getUsername());
                System.out.println("id: " + userData.getPublicId());

                byte[] userHash = userData.getUserHash(userData.getPublicKey(), userData.getIdSalt(), userData.getUsername() + "", "SHA-1");

                System.out.println("user hash: " + HexUtils.bytesToHex(userHash).toLowerCase());

                userData.saveToFile(userDataFile, hashedPassword);
                System.out.println("User data saved to file");

                return;
            }

            if (getNodePort() != 0) {
                launchDHTNode();
            }

            if (!isHeadless()) {
                this.loadLookAndFeel();
                this.launchGUI();
            }
        } catch (Throwable t) {
            LOGGER.error("Error while initializing app", t);

            if (!GraphicsEnvironment.isHeadless() && !isHeadless())
                JOptionPane.showConfirmDialog(null, "Error while initializing app:\n" + ExceptionUtils.throwableToString(t), "FATAL ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        }
    }

    public void unlockProfile(Path path, String password) throws IOException {
        char[] hashedPassword = new String(HashUtils.sha256(password.getBytes(StandardCharsets.UTF_8))).toCharArray();
        UserData userData = UserData.fromFile(path, hashedPassword);

        System.out.println("priv key: " + HexUtils.bytesToHex(userData.getPrivateKey()));
        System.out.println("pub key: " + HexUtils.bytesToHex(userData.getPublicKey()));
        System.out.println("username: " + userData.getUsername());
        System.out.println("id: " + userData.getPublicId());

        byte[] userHash = userData.getUserHash(userData.getPublicKey(), userData.getIdSalt(), userData.getUsername() + "", "SHA-1");

        System.out.println("user hash: " + HexUtils.bytesToHex(userHash).toLowerCase());
    }

    public void setDataDir(Path dataDir) {
        this.dataDir = dataDir;
    }

    public Path getDataDir() {
        return dataDir;
    }

    public void setNodePort(int nodePort) {
        this.nodePort = nodePort;
    }

    public int getNodePort() {
        return nodePort;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public boolean isHeadless() {
        return headless;
    }

    public void setNodePassword(char[] nodePassword) {
        this.nodePassword = nodePassword;
    }

    public char[] getNodePassword() {
        return nodePassword;
    }

    public static void main(String[] args) {
        INSTANCE.start(args);
    }
}
