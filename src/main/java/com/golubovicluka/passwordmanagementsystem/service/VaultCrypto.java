package com.golubovicluka.passwordmanagementsystem.service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * Encrypts and decrypts vault passwords at rest using AES-256-GCM.
 * The encryption key is derived from the user's login password via PBKDF2.
 */
public final class VaultCrypto {
    private static final String PREFIX = "v1:";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int KEY_LENGTH = 256;
    private static final int ITERATIONS = 120_000;

    private static SecretKey vaultKey;

    private VaultCrypto() {
    }

    public static void setMasterPassword(String masterPassword, String username) {
        if (masterPassword == null || masterPassword.isBlank()) {
            clearMasterPassword();
            return;
        }
        vaultKey = deriveKey(masterPassword, username);
    }

    public static void clearMasterPassword() {
        vaultKey = null;
    }

    public static boolean isUnlocked() {
        return vaultKey != null;
    }

    public static String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isBlank()) {
            return plaintext;
        }
        if (vaultKey == null) {
            throw new IllegalStateException("Vault is locked");
        }
        if (plaintext.startsWith(PREFIX)) {
            return plaintext;
        }

        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, vaultKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] payload = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(ciphertext, 0, payload, iv.length, ciphertext.length);
            return PREFIX + Base64.getEncoder().encodeToString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt vault password", e);
        }
    }

    public static String decrypt(String storedValue) {
        if (storedValue == null || storedValue.isBlank()) {
            return storedValue;
        }
        if (!storedValue.startsWith(PREFIX)) {
            return storedValue;
        }
        if (vaultKey == null) {
            throw new IllegalStateException("Vault is locked");
        }

        try {
            byte[] payload = Base64.getDecoder().decode(storedValue.substring(PREFIX.length()));
            byte[] iv = Arrays.copyOfRange(payload, 0, GCM_IV_LENGTH);
            byte[] ciphertext = Arrays.copyOfRange(payload, GCM_IV_LENGTH, payload.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, vaultKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrypt vault password", e);
        }
    }

    private static SecretKey deriveKey(String masterPassword, String username) {
        try {
            byte[] salt = username.getBytes(StandardCharsets.UTF_8);
            KeySpec spec = new PBEKeySpec(
                    masterPassword.toCharArray(),
                    salt,
                    ITERATIONS,
                    KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to derive vault key", e);
        }
    }
}
