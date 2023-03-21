package org.tyler.husher.object.message;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

import static org.tyler.husher.util.EncryptionUtils.*;

public class RSAEncryptedMessage {

    private final byte[] encryptedSecretKey;
    private final byte[] encryptedData;

    public RSAEncryptedMessage(PublicKey publicKey, byte[] data) throws Exception {
        SecretKey secretKey = generateAESKey();
        this.encryptedSecretKey = asymmetricEncrypt(publicKey, secretKey.getEncoded(), "EC");
        this.encryptedData = encryptAES(secretKey, data);
    }

    public byte[] decryptData(PrivateKey privateKey) throws Exception {
        byte[] encodedKey = asymmetricDecrypt(privateKey, encryptedSecretKey, "EC");
        SecretKey secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return decryptAES(secretKey, encryptedData);
    }

    public byte[] getEncryptedSecretKey() {
        return encryptedSecretKey;
    }

    public byte[] getEncryptedData() {
        return encryptedData;
    }
}
