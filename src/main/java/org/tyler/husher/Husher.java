package org.tyler.husher;

import com.formdev.flatlaf.IntelliJTheme;
import javafx.application.Application;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.RootLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tyler.husher.thread.DHTNodeThread;
import org.tyler.husher.ui.FxApp;
import org.tyler.husher.ui.ResourceManager;
import org.tyler.husher.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        IntelliJTheme.setup(ResourceManager.openResource("/style/dark.theme.json"));
    }

    private void launchGUI() {
        Class<? extends Application> fxAppClass = FxApp.class;
        LOGGER.info("Launching GUI from {}", fxAppClass.getName());
        Application.launch(fxAppClass);
    }

    private void launchDHTNode() {
        new Thread(new DHTNodeThread(), DHTNodeThread.class.getName()).start();
    }

    public void start(String... args) {
        try {
            // this.parseArguments(args);
            this.initLogger();

            if (getNodePort() != 0) {
                launchDHTNode();
            }

            if (!isHeadless()) {
                this.loadLookAndFeel();
                this.launchGUI();
            }
        } catch (Throwable t) {
            LOGGER.error("Error while initializing app", t);
            System.err.println("Error while initializing app");
            t.printStackTrace();

            if (!GraphicsEnvironment.isHeadless() && !isHeadless())
                JOptionPane.showConfirmDialog(null, "Error while initializing app:\n" + Utils.throwableToString(t), "FATAL ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        }
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
