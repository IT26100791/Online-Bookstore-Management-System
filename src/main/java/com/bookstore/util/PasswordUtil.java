package com.bookstore.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {
    private static final int ITERATIONS = 65536, KEY_BITS = 256;

    private PasswordUtil() {}

    /** returns "salt:hash" (Base64, no commas, safe for the CSV line) */
    public static String hash(String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(pbkdf2(password, salt));
    }

    public static boolean matches(String password, String stored) {
        if (password == null || stored == null) return false;
        int i = stored.indexOf(':');
        if (i < 0) return stored.equals(password);          // old plain-text accounts still work
        try {
            byte[] salt = Base64.getDecoder().decode(stored.substring(0, i));
            byte[] expected = Base64.getDecoder().decode(stored.substring(i + 1));
            return MessageDigest.isEqual(expected, pbkdf2(password, salt));
        } catch (IllegalArgumentException e) {
            return stored.equals(password);
        }
    }

    private static byte[] pbkdf2(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
