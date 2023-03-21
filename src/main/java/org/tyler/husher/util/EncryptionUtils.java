package org.tyler.husher.util;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class EncryptionUtils {

    public static final int DEFAULT_ITERATIONS = 65536;

    public static SecretKey restoreAESKey(char[] password, byte[] salt, int size, int iterations) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, size);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }
    public static SecretKey restoreAESKey(char[] password, byte[] salt, int size) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return restoreAESKey(password, salt, size, DEFAULT_ITERATIONS);
    }

    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256);
        return generator.generateKey();
    }

    public static byte[] asymmetricEncrypt(PublicKey publicKey, byte[] bytes, String algo) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(bytes);
    }

    public static byte[] asymmetricDecrypt(PrivateKey privateKey, byte[] cipherBytes, String algo) throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(cipherBytes);
    }

    public static byte[] encryptAES(SecretKey secretKey, byte[] bytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(bytes);
    }

    public static byte[] decryptAES(SecretKey secretKey, byte[] cipherBytes) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(cipherBytes);
    }
}
