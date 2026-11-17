package org.delcom.ifs25039springbootcashflow.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.delcom.ifs25039springbootcashflow.domain.exception.DomainException;
import org.delcom.ifs25039springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25039springbootcashflow.domain.exception.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class EntityTest {

    private static final LocalDateTime T0 = LocalDateTime.of(2026, 1, 1, 10, 0);

    @Test
    void userAccessorsAndRole() {
        User admin = new User(1, "admin", "h", "Admin", Role.ADMIN, "p.png");
        User user = new User(2, "budi", "h2", "Budi", Role.USER, null);
        assertEquals(1, admin.id());
        assertEquals("admin", admin.username());
        assertEquals("h", admin.passwordHash());
        assertEquals("Admin", admin.fullName());
        assertEquals(Role.ADMIN, admin.role());
        assertEquals("p.png", admin.photoPath());
        assertTrue(admin.isAdmin());
        assertFalse(user.isAdmin());
    }

    @Test
    void transactionAccessorsAndType() {
        Transaction income = new Transaction(1, 2, "Gaji", 100, TransactionType.INCOME, "b.png", T0);
        Transaction expense = new Transaction(2, 2, "Makan", 50, TransactionType.EXPENSE, null, T0);
        assertEquals(1, income.id());
        assertEquals(2, income.userId());
        assertEquals("Gaji", income.description());
        assertEquals(100L, income.amount());
        assertEquals(TransactionType.INCOME, income.type());
        assertEquals("b.png", income.proofPath());
        assertEquals(T0, income.createdAt());
        assertTrue(income.isIncome());
        assertFalse(expense.isIncome());
    }

    private static Transaction tx(int id, long amount, TransactionType type, int minutes) {
        return new Transaction(id, 1, "t" + id, amount, type, null, T0.plusMinutes(minutes));
    }

    private static List<Integer> order(SortOption option) {
        List<Transaction> list = new ArrayList<>(List.of(
                tx(1, 300, TransactionType.INCOME, 0),
                tx(2, 100, TransactionType.EXPENSE, 10),
                tx(3, 200, TransactionType.INCOME, 20),
                tx(4, 200, TransactionType.EXPENSE, 5)));
        list.sort(option.comparator());
        return list.stream().map(Transaction::id).toList();
    }

    @Test
    void sortOptionsOrderCorrectly() {
        assertEquals(List.of(3, 2, 4, 1), order(SortOption.NEWEST_FIRST));
        assertEquals(List.of(1, 4, 2, 3), order(SortOption.OLDEST_FIRST));
        assertEquals(List.of(2, 3, 4, 1), order(SortOption.AMOUNT_ASC));
        assertEquals(List.of(1, 3, 4, 2), order(SortOption.AMOUNT_DESC));
        assertEquals(List.of(1, 3, 2, 4), order(SortOption.INCOME_FIRST));
        assertEquals(List.of(2, 4, 1, 3), order(SortOption.EXPENSE_FIRST));
    }

    @Test
    void sortOptionsHaveLabels() {
        assertEquals(6, SortOption.values().length);
        for (SortOption option : SortOption.values()) {
            assertFalse(option.label().isBlank());
        }
    }

    @Test
    void exceptionHierarchy() {
        DomainException base = new DomainException("x");
        assertEquals("x", base.getMessage());
        assertTrue(new ValidationException("v") instanceof DomainException);
        EntityNotFoundException nf = new EntityNotFoundException("Transaksi", 7);
        assertEquals("Transaksi dengan ID 7 tidak ditemukan", nf.getMessage());
    }
}
