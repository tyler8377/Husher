package org.tyler.husher.thread;

import org.tyler.husher.protocol.cipher.xsalsa20poly1305.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tyler.husher.Husher;
import org.tyler.husher.io.tcp.TCPPunchServer;
import org.tyler.husher.util.EncryptionUtils;
import org.tyler.husher.util.HashUtils;
import org.tyler.husher.util.HexUtils;
import org.tyler.husher.util.ThreadUtils;

import javax.crypto.SecretKey;
import java.io.Console;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Scanner;

public class DHTNodeThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DHTNodeThread.class);

    private final Duration restartDelay;
    private final byte[] privateKey;
    private boolean running;

    public DHTNodeThread() {
        this.restartDelay = Duration.ofSeconds(3);
        this.privateKey = loadPrivateKey();
        LOGGER.info("Successfully loaded private key for DHT node");
    }

    private byte[] loadPrivateKey() {
        char[] password = Husher.INSTANCE.getNodePassword();
        byte[] result;

        while (true) {
            if (Husher.INSTANCE.getNodePassword().length == 0)
                password = promptPassword();

            try {
                result = decryptSecretKeyStore(password);
                Husher.INSTANCE.setNodePassword(password);
                return result;
            } catch (Exception e) {
                System.err.println("Wrong password (failed to decrypt secret key)");
                ThreadUtils.sleep(3000);
                Husher.INSTANCE.setNodePassword(new char[0]);
            }
        }
    }

    private byte[] decryptSecretKeyStore(char[] password) throws Exception {
        SecretKey secretKey = EncryptionUtils.restoreAESKey(password, HashUtils.sha256(new String(password).getBytes(StandardCharsets.UTF_8)), 256);
        Path nodeDir = Husher.INSTANCE.getDataDir().resolve("node");
        Path privateKeyFile = nodeDir.resolve("private.key");
        Path plainPublicKeyFile = nodeDir.resolve("public_key.txt");

        if (!Files.isDirectory(nodeDir))
            Files.createDirectory(nodeDir);

        byte[] privateKey;

        if (!Files.isRegularFile(privateKeyFile)) {
            privateKey = Keys.generatePrivateKey();
            byte[] encryptedPrivateKey = EncryptionUtils.encryptAES(secretKey, privateKey);
            byte[] publicKey = Keys.generatePublicKey(privateKey);
            Files.write(privateKeyFile, encryptedPrivateKey);
            Files.writeString(plainPublicKeyFile, HexUtils.bytesToHex(publicKey));
            LOGGER.info("Written private key to {}", privateKeyFile.getFileName());
            LOGGER.info("Written public key (hex) to {}", plainPublicKeyFile.getFileName());
        } else {
            privateKey = EncryptionUtils.decryptAES(secretKey, Files.readAllBytes(privateKeyFile));
        }
        return privateKey;
    }

    private char[] promptPassword() {
        Console console = System.console();
        Scanner userIn = new Scanner(System.in);
        System.out.print("Enter your DHT node password: ");
        if (console != null) return console.readPassword();
        else return userIn.nextLine().toCharArray();
    }

    @Override
    public void run() {
        try {
            TCPPunchServer server = new TCPPunchServer(InetAddress.getByName("0.0.0.0"), Husher.INSTANCE.getNodePort(), Duration.ofSeconds(10), privateKey);
            setRunning(true);

            while (isRunning()) {
                try {
                    server.run();
                } catch (RuntimeException e) {
                    LOGGER.error("An error has occurred. Restarting service in {}s", restartDelay.toSeconds(), e);
                    Thread.sleep(restartDelay.toMillis());
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }
}
