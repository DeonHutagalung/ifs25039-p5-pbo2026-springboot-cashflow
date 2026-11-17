package org.delcom.ifs25039springbootcashflow;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DotEnvTest {

    private static final List<String> KEYS = List.of("TST_PLAIN", "TST_DQ", "TST_SQ", "TST_MIX", "TST_ONE",
            "TST_EXIST");

    @TempDir
    Path tmp;

    @AfterEach
    void cleanup() {
        KEYS.forEach(System::clearProperty);
    }

    @Test
    void missingFileLoadsNothing() throws Exception {
        assertEquals(0, DotEnv.load(tmp.resolve("tidak-ada.env")));
    }

    @Test
    void parsesLinesAndKeepsExistingValues() throws Exception {
        System.setProperty("TST_EXIST", "asli");
        Path file = tmp.resolve(".env");
        Files.writeString(file, String.join("\n",
                "# komentar",
                "",
                "tanpa-sama-dengan",
                "=nilai-tanpa-kunci",
                "TST_PLAIN = polos ",
                "TST_DQ=\"dua kutip\"",
                "TST_SQ='satu kutip'",
                "TST_MIX=\"campur'",
                "TST_ONE=a",
                "TST_EXIST=baru",
                "PATH=jangan-ditimpa"));
        assertEquals(5, DotEnv.load(file));
        assertEquals("polos", System.getProperty("TST_PLAIN"));
        assertEquals("dua kutip", System.getProperty("TST_DQ"));
        assertEquals("satu kutip", System.getProperty("TST_SQ"));
        assertEquals("\"campur'", System.getProperty("TST_MIX"));
        assertEquals("a", System.getProperty("TST_ONE"));
        assertEquals("asli", System.getProperty("TST_EXIST"));
    }

    @Test
    void defaultLoadDoesNotFail() throws Exception {
        assertTrue(DotEnv.load() >= 0);
    }
}
