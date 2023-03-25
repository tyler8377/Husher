package org.tyler.husher.client.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tyler.husher.core.crypto.xsalsa20poly1305.Keys;
import org.tyler.husher.core.crypto.EncryptionUtils;
import org.tyler.husher.core.exceptions.WrongPasswordException;
import org.tyler.husher.core.util.HashUtils;
import org.tyler.husher.core.util.HexUtils;
import org.tyler.husher.core.util.SerializationUtils;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserData implements Serializable {

    private static final long serialVersionUID = -2764315589891121475L;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserData.class);
    private static final String DEFAULT_HASH_ALGO = "SHA-1";

    private String path;
    private String username;
    private char[] password;
    private byte[] privateKey;
    private byte[] publicKey;
    private String publicId;
    private byte[] idSalt;
    private List<String> channels;

    public UserData(byte[] privateKey, String username, String path, char[] password) {
        this.privateKey = privateKey;
        this.publicKey = Keys.generatePublicKey(privateKey);
        this.username = username;
        this.path = path;
        this.password = password;
        this.publicId = regeneratePublicId(DEFAULT_HASH_ALGO);
        this.channels = new ArrayList<>();
    }

    public UserData load(String path, String username, char[] password) {
        this.path = path;
        this.username = username;
        this.password = password;
        this.privateKey = Optional.ofNullable(privateKey).orElseGet(Keys::generatePrivateKey);
        this.publicKey = Optional.ofNullable(publicKey).orElseGet(() -> Keys.generatePublicKey(privateKey));
        // this.publicId = Optional.

        return this;
    }

    public static UserData fromFile(Path path, char[] password) throws IOException, IllegalArgumentException {
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
            throw new WrongPasswordException();
        } catch (Exception e) {
            throw new RuntimeException("Unable to decrypt data file", e);
        }

        try {
            UserData userData = (UserData) SerializationUtils.deserialize(objBytes);
            userData.setStoragePath(path.toAbsolutePath().toString(), password);
            return userData;
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

    public void save() throws IOException {
        Path storagePath = Path.of(path);

        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath.getParent());
            Files.createFile(storagePath);
        }

        if (!Files.isRegularFile(storagePath))
            throw new IllegalArgumentException("\"" + storagePath.toAbsolutePath() + "\" is not a regular file");

        byte[] encryptedBytes;

        try {
            byte[] objBytes = SerializationUtils.serialize(this);
            SecretKey secretKey = EncryptionUtils.restoreAESKey(password, HashUtils.sha256(new String(password).getBytes(StandardCharsets.UTF_8)), 256);
            encryptedBytes = EncryptionUtils.encryptAES(secretKey, objBytes);
        } catch (Exception e) {
            throw new RuntimeException("Unable to encrypt data", e);
        }

        Files.write(storagePath, encryptedBytes);
    }

    public void setStoragePath(String path, char[] password) {
        this.path = path;
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public char[] getPassword() {
        return password;
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

    public List<String> getChannels() {
        if (channels == null)
            channels = new ArrayList<>();
        // TODO type safe object
        return channels;
    }
}
