package org.delcom.ifs25039springbootcashflow.adapter.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.delcom.ifs25039springbootcashflow.domain.entity.Role;
import org.delcom.ifs25039springbootcashflow.domain.entity.User;
import org.delcom.ifs25039springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class UserRepositoryContract {

    protected IUserRepository repo;

    abstract IUserRepository create();

    @BeforeEach
    void setUp() {
        repo = create();
    }

    @Test
    void saveAndFind() {
        User saved = repo.save("budi", "hash", "Budi", Role.USER);
        assertTrue(saved.id() > 0);
        assertNull(saved.photoPath());
        assertEquals(saved, repo.findById(saved.id()).orElseThrow());
        assertEquals(saved, repo.findByUsername("budi").orElseThrow());
    }

    @Test
    void findAbsent() {
        assertTrue(repo.findById(999).isEmpty());
        assertTrue(repo.findByUsername("none").isEmpty());
    }

    @Test
    void findAllReturnsEverything() {
        repo.save("a", "h", "A", Role.USER);
        repo.save("b", "h", "B", Role.ADMIN);
        assertEquals(2, repo.findAll().size());
    }

    @Test
    void updateExisting() throws Exception {
        User saved = repo.save("a", "h", "A", Role.USER);
        User changed = new User(saved.id(), "a", "h2", "Nama Baru", Role.USER, "foto.png");
        assertEquals(changed, repo.save(changed));
        assertEquals(changed, repo.findById(saved.id()).orElseThrow());
    }

    @Test
    void updateMissingThrows() {
        User ghost = new User(404, "x", "h", "X", Role.USER, null);
        assertThrows(EntityNotFoundException.class, () -> repo.save(ghost));
    }

    @Test
    void deleteById() {
        User saved = repo.save("a", "h", "A", Role.USER);
        assertTrue(repo.deleteById(saved.id()));
        assertFalse(repo.deleteById(saved.id()));
    }
}
