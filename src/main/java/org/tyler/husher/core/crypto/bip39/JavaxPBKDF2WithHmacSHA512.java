/*
 *  BIP39 library, a Java implementation of BIP39
 *  Copyright (C) 2017-2019 Alan Evans, NovaCrypto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  Original source: https://github.com/NovaCrypto/BIP39
 *  You can contact the authors via github issues.
 */

package org.tyler.husher.core.crypto.bip39;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Not available in all Java implementations, for example will not find the implementation before Android API 26+.
 * See <a href="https://developer.android.com/reference/javax/crypto/SecretKeyFactory.html">...</a> for more details.
 */
public enum JavaxPBKDF2WithHmacSHA512 implements PBKDF2WithHmacSHA512 {
    INSTANCE;

    private final SecretKeyFactory skf = getPbkdf2WithHmacSHA512();

    @Override
    public byte[] hash(char[] chars, byte[] salt) {
        final PBEKeySpec spec = new PBEKeySpec(chars, salt, 2048, 512);
        final byte[] encoded = generateSecretKey(spec).getEncoded();
        spec.clearPassword();
        return encoded;
    }

    private SecretKey generateSecretKey(final PBEKeySpec spec) {
        try {
            return skf.generateSecret(spec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private static SecretKeyFactory getPbkdf2WithHmacSHA512() {
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}