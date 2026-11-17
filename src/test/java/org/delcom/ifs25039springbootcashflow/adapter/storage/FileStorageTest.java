package org.delcom.ifs25039springbootcashflow.adapter.storage;

import static org.junit.jupiter.api.Assertions.*;

import org.delcom.ifs25039springbootcashflow.domain.exception.ValidationException;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileStorageTest {

    @TempDir
    Path tmp;

    private static ByteArrayInputStream data() {
        return new ByteArrayInputStream(new byte[] {1, 2, 3});
    }

    @Test
    void storesImageWithUuidName() throws Exception {
        FileStorage storage = new FileStorage(tmp.resolve("up").toString());
        String name = storage.store("Foto.PNG", data());
        assertTrue(name.endsWith(".png"));
        assertTrue(Files.exists(storage.root().resolve(name)));
        assertEquals("/uploads/" + name, FileStorage.urlOf(name));
    }

    @Test
    void rejectsNonImage() {
        FileStorage storage = new FileStorage(tmp.toString());
        assertThrows(ValidationException.class, () -> storage.store("virus.exe", data()));
        assertThrows(ValidationException.class, () -> storage.store("tanpaekstensi", data()));
        assertThrows(ValidationException.class, () -> storage.store(null, data()));
    }

    @Test
    void deleteReportsExistence() throws Exception {
        FileStorage storage = new FileStorage(tmp.toString());
        String name = storage.store("a.jpg", data());
        assertTrue(storage.delete(name));
        assertFalse(storage.delete(name));
    }

    @Test
    void locationUriAlwaysEndsWithSlash() {
        assertTrue(new FileStorage(tmp.toString()).locationUri().endsWith("/"));
        assertTrue(new FileStorage(tmp.resolve("belum-ada").toString()).locationUri().endsWith("/"));
    }
}
