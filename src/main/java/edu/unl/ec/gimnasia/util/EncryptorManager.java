package edu.unl.ec.gimnasia.util;

import edu.unl.ec.gimnasia.exception.EncryptorException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptorManager {

    private static final String ALGORITHM = "AES";
    private static final String DEFAULT_PRIVATE_KEY = "_tuClaveEnBase64";

    private EncryptorManager() {
    }

    private static SecretKey getSecretKey() throws NoSuchAlgorithmException {
        return new SecretKeySpec(DEFAULT_PRIVATE_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    public static String generateKeySecretToStr() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
        generator.init(256);
        SecretKey secretKey = generator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static String encrypt(String text) throws EncryptorException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                 | BadPaddingException | InvalidKeyException e) {
            throw new EncryptorException(e.getMessage(), e);
        }
    }

    public static String decrypt(String textEncrypted) throws EncryptorException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            byte[] decoded = Base64.getDecoder().decode(textEncrypted);
            byte[] original = cipher.doFinal(decoded);
            return new String(original, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                 | BadPaddingException | InvalidKeyException e) {
            throw new EncryptorException(e.getMessage(), e);
        }
    }
}


