package org.tyler.husher.core.network.message;

import org.tyler.husher.core.crypto.xsalsa20poly1305.Keys;
import org.tyler.husher.core.crypto.xsalsa20poly1305.SimpleBox;
import org.tyler.husher.core.util.HexUtils;
import org.tyler.husher.core.util.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;

public class EncryptedObject implements Serializable {

    private final byte[] secretKeyCipher;
    private final byte[] cipherBytes;

    public EncryptedObject(Object obj, byte[] destPublicKey, byte[] ownPrivateKey) throws IOException {
        if (!(obj instanceof Serializable))
            throw new IllegalArgumentException("Class \"" + obj.getClass().getName() + "\" is not serializable");

        byte[] data = SerializationUtils.serialize(obj);
        SimpleBox box = new SimpleBox(destPublicKey, ownPrivateKey);
        byte[] secretKey = Keys.generateSecretKey();
        this.secretKeyCipher = box.seal(secretKey);
        box = new SimpleBox(secretKey);
        this.cipherBytes = box.seal(data);
    }

    public Object decrypt(byte[] ownPrivateKey, byte[] destPublicKey) throws IOException, ClassNotFoundException {
        System.out.println("decrypting data... pub=" + HexUtils.bytesToHex(destPublicKey));
        SimpleBox box = new SimpleBox(destPublicKey, ownPrivateKey);
        byte[] secretKey = box.open(secretKeyCipher).orElseThrow(() -> new RuntimeException("Failed to decrypt secret key"));
        box = new SimpleBox(secretKey);
        byte[] objBytes = box.open(cipherBytes).orElseThrow(() -> new RuntimeException("Failed to decrypt cipher data"));
        return SerializationUtils.deserialize(objBytes);
    }

    public byte[] getSecretKeyCipher() {
        return secretKeyCipher;
    }

    public byte[] getCipherBytes() {
        return cipherBytes;
    }
}
