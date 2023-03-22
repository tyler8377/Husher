package org.tyler.husher.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tyler.husher.protocol.security.xsalsa20poly1305.Keys;
import org.tyler.husher.util.EncryptionUtils;
import org.tyler.husher.util.HashUtils;
import org.tyler.husher.util.HexUtils;
import org.tyler.husher.util.SerializationUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class UserData implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserData.class);
    private static final String DEFAULT_HASH_ALGO = "SHA-1";

    private final byte[] privateKey;
    private final byte[] publicKey;
    private final String username;
    private String publicId;
    private byte[] idSalt;

    public UserData(byte[] privateKey, String username) {
        this.privateKey = privateKey;
        this.publicKey = Keys.generatePublicKey(privateKey);
        this.username = username;
        this.publicId = regeneratePublicId(DEFAULT_HASH_ALGO);
    }

    public UserData(String username) {
        this(Keys.generatePrivateKey(), username);
    }

    public static UserData fromFile(Path path, char[] password) throws IOException {
        if (!Files.exists(path))
            throw new IllegalArgumentException("File \"" + path.toAbsolutePath() + "\" does not exist");

        if (!Files.isRegularFile(path))
            throw new IllegalArgumentException("\"" + path.toAbsolutePath() + "\" is not a regular file");

        byte[] encryptedBytes = Files.readAllBytes(path);
        byte[] objBytes;

        try {
            SecretKey secretKey = EncryptionUtils.restoreAESKey(password, HashUtils.sha256(new String(password).getBytes(StandardCharsets.UTF_8)), 256);
            objBytes = EncryptionUtils.decryptAES(secretKey, encryptedBytes);
        } catch (BadPaddingException e) {
            throw new RuntimeException("Invalid decryption password");
        } catch (Exception e) {
            throw new RuntimeException("Unable to decrypt data file", e);
        }

        try {
            return (UserData) SerializationUtils.deserialize(objBytes);
        } catch (Exception e) {
            throw new RuntimeException("Data file is invalid/damaged", e);
        }
    }

    private MessageDigest newMessageDigest(String algo) {
        try {
            return MessageDigest.getInstance(algo);
        } catch (NoSuchAlgorithmException e1) {
            LOGGER.warn("Digest algorithm {} not available; trying fallback algorithm", algo);
            try {
                return MessageDigest.getInstance(DEFAULT_HASH_ALGO);
            } catch (NoSuchAlgorithmException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    public String regeneratePublicId(String algo) {
        SecureRandom random = new SecureRandom();
        idSalt = new byte[16];
        random.nextBytes(idSalt);
        byte[] hash = getUserHash(publicKey, idSalt, username, algo);
        return publicId = HexUtils.bytesToHex(hash);
    }

    public byte[] getUserHash(byte[] publicKey, byte[] salt, String username, String algo) {
        MessageDigest digest = newMessageDigest(algo);
        digest.update(publicKey);
        digest.update(salt);
        digest.update(username.getBytes(StandardCharsets.UTF_8));
        return digest.digest();
    }

    public void saveToFile(Path path, char[] password) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }

        if (!Files.isRegularFile(path))
            throw new IllegalArgumentException("\"" + path.toAbsolutePath() + "\" is not a regular file");

        byte[] encryptedBytes;

        try {
            byte[] objBytes = SerializationUtils.serialize(this);
            SecretKey secretKey = EncryptionUtils.restoreAESKey(password, HashUtils.sha256(new String(password).getBytes(StandardCharsets.UTF_8)), 256);
            encryptedBytes = EncryptionUtils.encryptAES(secretKey, objBytes);
        } catch (Exception e) {
            throw new RuntimeException("Unable to encrypt data", e);
        }

        Files.write(path, encryptedBytes);
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public String getUsername() {
        return username;
    }

    public String getPublicId() {
        return publicId;
    }

    public byte[] getIdSalt() {
        return idSalt;
    }
}
