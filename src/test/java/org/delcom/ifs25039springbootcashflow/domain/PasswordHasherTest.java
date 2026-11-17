package org.delcom.ifs25039springbootcashflow.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PasswordHasherTest {

    @Test
    void hashesWithSha256Hex() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                PasswordHasher.hash("abc"));
    }

    @Test
    void matchesChecksPassword() {
        String hash = PasswordHasher.hash("rahasia");
        assertTrue(PasswordHasher.matches("rahasia", hash));
        assertFalse(PasswordHasher.matches("salah", hash));
    }

    @Test
    void unknownAlgorithmThrows() {
        assertThrows(IllegalStateException.class, () -> PasswordHasher.hash("x", "NOPE-999"));
    }
}
