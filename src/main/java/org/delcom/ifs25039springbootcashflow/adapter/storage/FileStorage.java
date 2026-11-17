package org.delcom.ifs25039springbootcashflow.adapter.storage;

import org.delcom.ifs25039springbootcashflow.domain.exception.ValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/** Menyimpan file upload (foto profil dan bukti transaksi) ke folder UPLOAD_DIR. */
public class FileStorage {

    private static final Set<String> ALLOWED = Set.of("png", "jpg", "jpeg", "gif", "webp");

    private final Path root;

    public FileStorage(String uploadDir) {
        this.root = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public Path root() {
        return root;
    }

    /** URI folder upload (selalu diakhiri "/") untuk resource location Spring MVC. */
    public String locationUri() {
        String uri = root.toUri().toString();
        return uri.endsWith("/") ? uri : uri + "/";
    }

    /** Menyimpan stream sebagai file baru (UUID + ekstensi) dan mengembalikan nama file relatif. */
    public String store(String originalFilename, InputStream content) throws IOException, ValidationException {
        String extension = extensionOf(originalFilename);
        if (!ALLOWED.contains(extension)) {
            throw new ValidationException("Format file harus berupa gambar (png, jpg, jpeg, gif, webp)");
        }
        Files.createDirectories(root);
        String name = UUID.randomUUID() + "." + extension;
        Files.copy(content, root.resolve(name));
        return name;
    }

    public boolean delete(String name) throws IOException {
        return Files.deleteIfExists(root.resolve(Path.of(name).getFileName()));
    }

    /** URL publik yang dilayani oleh mapping /uploads/**. */
    public static String urlOf(String name) {
        return "/uploads/" + name;
    }

    private static String extensionOf(String filename) {
        String safe = filename == null ? "" : filename;
        int dot = safe.lastIndexOf('.');
        return dot < 0 ? "" : safe.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
