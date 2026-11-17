package org.delcom.ifs25039springbootcashflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Memuat file .env ke system properties sebelum Spring Boot start. */
public final class DotEnv {

    private DotEnv() {
    }

    public static int load() throws IOException {
        return load(Path.of(".env"));
    }

    /** Mengembalikan jumlah properti yang diset. Env/property yang sudah ada tidak ditimpa. */
    static int load(Path file) throws IOException {
        if (!Files.exists(file)) {
            return 0;
        }
        int count = 0;
        for (String raw : Files.readAllLines(file)) {
            String line = raw.trim();
            int eq = line.indexOf('=');
            if (line.isEmpty() || line.startsWith("#") || eq <= 0) {
                continue;
            }
            String key = line.substring(0, eq).trim();
            String value = unquote(line.substring(eq + 1).trim());
            if (System.getenv(key) == null && System.getProperty(key) == null) {
                System.setProperty(key, value);
                count++;
            }
        }
        return count;
    }

    private static String unquote(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' || first == '\'') && first == last) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
