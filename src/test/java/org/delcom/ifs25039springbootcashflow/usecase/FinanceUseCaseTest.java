package org.delcom.ifs25039springbootcashflow.usecase;

import static org.junit.jupiter.api.Assertions.*;

import org.delcom.ifs25039springbootcashflow.adapter.repository.InMemoryTransactionRepository;
import org.delcom.ifs25039springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25039springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25039springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25039springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25039springbootcashflow.domain.exception.ValidationException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FinanceUseCaseTest {

    private FinanceUseCase finance;

    @BeforeEach
    void setUp() {
        finance = new FinanceUseCase(new InMemoryTransactionRepository());
    }

    @Test
    void addIncomeAndExpense() throws Exception {
        Transaction in = finance.addIncome(1, "  Gaji ", " 1000 ", "bukti.png");
        Transaction out = finance.addExpense(1, "Makan", "250", null);
        assertEquals("Gaji", in.description());
        assertEquals(1000L, in.amount());
        assertEquals(TransactionType.INCOME, in.type());
        assertEquals("bukti.png", in.proofPath());
        assertEquals(TransactionType.EXPENSE, out.type());
    }

    @Test
    void addValidatesInput() {
        assertThrows(ValidationException.class, () -> finance.addIncome(0, "x", "1", null));
        assertThrows(ValidationException.class, () -> finance.addIncome(1, null, "1", null));
        assertThrows(ValidationException.class, () -> finance.addIncome(1, "   ", "1", null));
        assertThrows(ValidationException.class, () -> finance.addIncome(1, "x", null, null));
        assertThrows(ValidationException.class, () -> finance.addIncome(1, "x", "abc", null));
        assertThrows(ValidationException.class, () -> finance.addIncome(1, "x", "12.5", null));
        assertThrows(ValidationException.class, () -> finance.addIncome(1, "x", "0", null));
        assertThrows(ValidationException.class, () -> finance.addExpense(1, "x", "-5", null));
    }

    @Test
    void balanceIsIncomeMinusExpense() throws Exception {
        finance.addIncome(1, "Gaji", "5000000", null);
        finance.addExpense(1, "Sewa", "25000", null);
        finance.addIncome(2, "Orang lain", "999", null);
        assertEquals(5_000_000L, finance.getTotalIncome(1));
        assertEquals(25_000L, finance.getTotalExpense(1));
        assertEquals(4_975_000L, finance.getBalance(1));
        assertEquals(0L, finance.getBalance(3));
        assertThrows(ValidationException.class, () -> finance.getBalance(0));
    }

    @Test
    void listFiltersAndSorts() throws Exception {
        finance.addIncome(1, "Gaji Bulanan", "300", null);
        finance.addExpense(1, "makan siang", "50", null);
        finance.addExpense(1, "Makan malam", "70", null);
        finance.addIncome(2, "Makan gratis", "1", null);

        assertEquals(3, finance.list(1, null, null).size());
        assertEquals(3, finance.list(1, "  ", SortOption.AMOUNT_ASC).size());
        List<Transaction> makan = finance.list(1, "MAKAN", SortOption.AMOUNT_DESC);
        assertEquals(List.of(70L, 50L), makan.stream().map(Transaction::amount).toList());
        assertEquals(0, finance.list(1, "tidak ada", null).size());
        assertThrows(ValidationException.class, () -> finance.list(0, null, null));
    }

    @Test
    void updateChangesOwnedTransaction() throws Exception {
        Transaction t = finance.addExpense(1, "Lama", "10", null);
        Transaction updated = finance.update(1, t.id(), " Baru ", "20", TransactionType.INCOME, "b.png");
        assertEquals("Baru", updated.description());
        assertEquals(20L, updated.amount());
        assertEquals(TransactionType.INCOME, updated.type());
        assertEquals("b.png", updated.proofPath());
        assertEquals(t.createdAt(), updated.createdAt());
    }

    @Test
    void updateValidates() throws Exception {
        Transaction t = finance.addExpense(1, "x", "10", null);
        assertThrows(ValidationException.class, () -> finance.update(1, t.id(), " ", "5", TransactionType.INCOME, null));
        assertThrows(ValidationException.class, () -> finance.update(1, t.id(), "ok", "abc", TransactionType.INCOME, null));
        assertThrows(ValidationException.class, () -> finance.update(2, t.id(), "ok", "5", TransactionType.INCOME, null));
        assertThrows(ValidationException.class, () -> finance.update(0, t.id(), "ok", "5", TransactionType.INCOME, null));
        assertThrows(ValidationException.class, () -> finance.update(1, 0, "ok", "5", TransactionType.INCOME, null));
        assertThrows(EntityNotFoundException.class, () -> finance.update(1, 99, "ok", "5", TransactionType.INCOME, null));
    }

    @Test
    void deleteOnlyOwnData() throws Exception {
        Transaction t = finance.addIncome(1, "x", "10", null);
        assertThrows(ValidationException.class, () -> finance.delete(2, t.id()));
        assertThrows(ValidationException.class, () -> finance.delete(1, -1));
        assertThrows(EntityNotFoundException.class, () -> finance.delete(1, 99));
        finance.delete(1, t.id());
        assertEquals(0, finance.list(1, null, null).size());
        assertThrows(EntityNotFoundException.class, () -> finance.delete(1, t.id()));
    }
}
