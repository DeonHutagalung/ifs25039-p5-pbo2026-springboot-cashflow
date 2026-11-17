package org.delcom.ifs25039springbootcashflow.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Hash dan verifikasi password dengan SHA-256 (hex). */
public final class PasswordHasher {

    private static final String ALGORITHM = "SHA-256";

    private PasswordHasher() {
    }

    public static String hash(String rawPassword) {
        return hash(rawPassword, ALGORITHM);
    }

    static String hash(String rawPassword, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] bytes = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritma hash tidak tersedia: " + algorithm, e);
        }
    }

    public static boolean matches(String rawPassword, String passwordHash) {
        return hash(rawPassword).equals(passwordHash);
    }
}
