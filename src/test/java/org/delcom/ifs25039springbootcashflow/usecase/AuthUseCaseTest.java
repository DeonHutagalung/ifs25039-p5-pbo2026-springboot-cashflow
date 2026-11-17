package org.delcom.ifs25039springbootcashflow.usecase;

import static org.junit.jupiter.api.Assertions.*;

import org.delcom.ifs25039springbootcashflow.adapter.repository.InMemoryUserRepository;
import org.delcom.ifs25039springbootcashflow.domain.PasswordHasher;
import org.delcom.ifs25039springbootcashflow.domain.entity.Role;
import org.delcom.ifs25039springbootcashflow.domain.entity.User;
import org.delcom.ifs25039springbootcashflow.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthUseCaseTest {

    private InMemoryUserRepository repo;
    private AuthUseCase auth;

    @BeforeEach
    void setUp() {
        repo = new InMemoryUserRepository();
        auth = new AuthUseCase(repo);
    }

    @Test
    void registerCreatesUserWithHashedPassword() throws Exception {
        User user = auth.register("  budi ", "rahasia", " Budi Santoso ");
        assertEquals("budi", user.username());
        assertEquals("Budi Santoso", user.fullName());
        assertEquals(Role.USER, user.role());
        assertEquals(PasswordHasher.hash("rahasia"), user.passwordHash());
    }

    @Test
    void registerValidatesInput() throws Exception {
        assertThrows(ValidationException.class, () -> auth.register(null, "rahasia", "Budi"));
        assertThrows(ValidationException.class, () -> auth.register("   ", "rahasia", "Budi"));
        assertThrows(ValidationException.class, () -> auth.register("budi", "rahasia", null));
        assertThrows(ValidationException.class, () -> auth.register("budi", "rahasia", "  "));
        assertThrows(ValidationException.class, () -> auth.register("budi", null, "Budi"));
        assertThrows(ValidationException.class, () -> auth.register("budi", "abc", "Budi"));
        auth.register("budi", "rahasia", "Budi");
        assertThrows(ValidationException.class, () -> auth.register("budi", "rahasia", "Budi 2"));
    }

    @Test
    void loginSucceeds() throws Exception {
        User registered = auth.register("budi", "rahasia", "Budi");
        assertEquals(registered, auth.login(" budi ", "rahasia"));
    }

    @Test
    void loginRejectsBadCredentials() throws Exception {
        auth.register("budi", "rahasia", "Budi");
        assertThrows(ValidationException.class, () -> auth.login(null, "rahasia"));
        assertThrows(ValidationException.class, () -> auth.login("", "rahasia"));
        assertThrows(ValidationException.class, () -> auth.login("budi", null));
        assertThrows(ValidationException.class, () -> auth.login("budi", ""));
        assertThrows(ValidationException.class, () -> auth.login("tidak-ada", "rahasia"));
        ValidationException wrong = assertThrows(ValidationException.class, () -> auth.login("budi", "salah"));
        assertEquals("Username atau password salah", wrong.getMessage());
    }

    @Test
    void ensureRootAdminCreatesOnlyOnce() {
        User first = auth.ensureRootAdmin("admin", "admin");
        assertEquals(Role.ADMIN, first.role());
        assertEquals(first, auth.ensureRootAdmin("admin", "lain"));
        assertEquals(1, repo.findAll().size());
    }
}
