package com.bmazurkiewicz01.server.database;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class Codec {
    private final byte[] key;
    public static final String ALGORITHM = "AES";

    public Codec(String key) {
        this.key = key.getBytes();
    }

    public String encrypt(String password)  {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, generateKey());
            byte[] encryptedPassword = cipher.doFinal(password.getBytes());
            return Base64.getEncoder().encodeToString(encryptedPassword);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String decrypt(String password) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, generateKey());
            byte[] decryptedPassword = cipher.doFinal(Base64.getDecoder().decode(password));
            return new String(decryptedPassword);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException | InvalidKeyException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }
}
