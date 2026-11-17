package org.delcom.ifs25039springbootcashflow.usecase;

import static org.junit.jupiter.api.Assertions.*;

import org.delcom.ifs25039springbootcashflow.adapter.repository.InMemoryUserRepository;
import org.delcom.ifs25039springbootcashflow.domain.PasswordHasher;
import org.delcom.ifs25039springbootcashflow.domain.entity.Role;
import org.delcom.ifs25039springbootcashflow.domain.entity.User;
import org.delcom.ifs25039springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25039springbootcashflow.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserUseCaseTest {

    private UserUseCase users;
    private User budi;

    @BeforeEach
    void setUp() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        users = new UserUseCase(repo);
        budi = repo.save("budi", PasswordHasher.hash("lama"), "Budi", Role.USER);
        repo.save("admin", "h", "Admin", Role.ADMIN);
    }

    @Test
    void listAndGet() throws Exception {
        assertEquals(2, users.listUsers().size());
        assertEquals(budi, users.getUser(budi.id()));
        assertThrows(EntityNotFoundException.class, () -> users.getUser(99));
    }

    @Test
    void updateProfileKeepsPasswordWhenBlank() throws Exception {
        User a = users.updateProfile(budi.id(), " Budi Baru ", null);
        assertEquals("Budi Baru", a.fullName());
        assertEquals(budi.passwordHash(), a.passwordHash());
        assertEquals(budi.passwordHash(), users.updateProfile(budi.id(), "Budi", "").passwordHash());
    }

    @Test
    void updateProfileChangesPassword() throws Exception {
        User updated = users.updateProfile(budi.id(), "Budi", "barusekali");
        assertEquals(PasswordHasher.hash("barusekali"), updated.passwordHash());
    }

    @Test
    void updateProfileValidates() {
        assertThrows(ValidationException.class, () -> users.updateProfile(budi.id(), null, null));
        assertThrows(ValidationException.class, () -> users.updateProfile(budi.id(), "  ", null));
        assertThrows(ValidationException.class, () -> users.updateProfile(budi.id(), "Budi", "abc"));
        assertThrows(EntityNotFoundException.class, () -> users.updateProfile(99, "Budi", null));
    }

    @Test
    void updatePhoto() throws Exception {
        assertEquals("foto.png", users.updatePhoto(budi.id(), "foto.png").photoPath());
        assertEquals("foto.png", users.getUser(budi.id()).photoPath());
        assertThrows(EntityNotFoundException.class, () -> users.updatePhoto(99, "x.png"));
    }
}
