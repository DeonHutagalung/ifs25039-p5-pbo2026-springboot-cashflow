package org.delcom.ifs25039springbootcashflow.adapter.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.delcom.ifs25039springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25039springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25039springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25039springbootcashflow.domain.repository.ITransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class TransactionRepositoryContract {

    protected ITransactionRepository repo;
    protected int userA;
    protected int userB;

    abstract ITransactionRepository create();

    /** Membuat pengguna valid (dibutuhkan oleh foreign key pada JDBC). */
    abstract int createUser(String username);

    @BeforeEach
    void setUp() {
        repo = create();
        userA = createUser("a");
        userB = createUser("b");
    }

    @Test
    void saveAndFind() {
        Transaction saved = repo.save(userA, "Gaji", 1000, TransactionType.INCOME, "bukti.png");
        assertTrue(saved.id() > 0);
        assertNotNull(saved.createdAt());
        Transaction found = repo.findById(saved.id()).orElseThrow();
        assertEquals("Gaji", found.description());
        assertEquals(1000L, found.amount());
        assertEquals(TransactionType.INCOME, found.type());
        assertEquals("bukti.png", found.proofPath());
        assertEquals(userA, found.userId());
    }

    @Test
    void findAbsent() {
        assertTrue(repo.findById(999).isEmpty());
    }

    @Test
    void findByUserIdIsScoped() {
        repo.save(userA, "A1", 1, TransactionType.INCOME, null);
        repo.save(userA, "A2", 2, TransactionType.EXPENSE, null);
        repo.save(userB, "B1", 3, TransactionType.INCOME, null);
        assertEquals(2, repo.findByUserId(userA).size());
        assertEquals(1, repo.findByUserId(userB).size());
        assertEquals(3, repo.findAll().size());
    }

    @Test
    void updateExisting() throws Exception {
        Transaction saved = repo.save(userA, "Lama", 10, TransactionType.EXPENSE, null);
        Transaction changed = new Transaction(saved.id(), userA, "Baru", 20, TransactionType.INCOME, "x.png",
                saved.createdAt());
        repo.save(changed);
        Transaction found = repo.findById(saved.id()).orElseThrow();
        assertEquals("Baru", found.description());
        assertEquals(20L, found.amount());
        assertEquals(TransactionType.INCOME, found.type());
        assertEquals("x.png", found.proofPath());
    }

    @Test
    void updateMissingThrows() {
        Transaction ghost = new Transaction(404, userA, "x", 1, TransactionType.INCOME, null,
                java.time.LocalDateTime.now());
        assertThrows(EntityNotFoundException.class, () -> repo.save(ghost));
    }

    @Test
    void deleteById() {
        Transaction saved = repo.save(userA, "x", 1, TransactionType.INCOME, null);
        assertTrue(repo.deleteById(saved.id()));
        assertFalse(repo.deleteById(saved.id()));
    }
}
