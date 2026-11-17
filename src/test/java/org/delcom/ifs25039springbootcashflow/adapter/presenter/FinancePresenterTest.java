package org.delcom.ifs25039springbootcashflow.adapter.presenter;

import static org.junit.jupiter.api.Assertions.*;

import org.delcom.ifs25039springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25039springbootcashflow.domain.entity.TransactionType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class FinancePresenterTest {

    private final FinancePresenter presenter = new FinancePresenter();
    private final Transaction income = new Transaction(1, 1, "Gaji", 1_000_000, TransactionType.INCOME, null,
            LocalDateTime.of(2026, 11, 17, 8, 5));
    private final Transaction expense = new Transaction(2, 1, "Makan", 25_000, TransactionType.EXPENSE, null,
            LocalDateTime.of(2026, 11, 17, 8, 5));

    @Test
    void formatsMoney() {
        assertEquals("Rp 1,000,000.00", presenter.formatMoney(1_000_000));
        assertEquals("Rp 0.00", presenter.formatMoney(0));
        assertEquals("Rp -500.00", presenter.formatMoney(-500));
    }

    @Test
    void formatsRowsAndBalance() {
        assertEquals("[1] Gaji | Rp 1,000,000.00 | [Pemasukan]", presenter.formatRow(income));
        assertEquals("[2] Makan | Rp 25,000.00 | [Pengeluaran]", presenter.formatRow(expense));
        assertEquals("Saldo: Rp 4,975,000.00", presenter.formatBalance(4_975_000));
    }

    @Test
    void typeLabels() {
        assertEquals("Pemasukan", presenter.typeLabel(TransactionType.INCOME));
        assertEquals("Pengeluaran", presenter.typeLabel(TransactionType.EXPENSE));
    }

    @Test
    void formatsDateAndMessages() {
        assertEquals("17-11-2026 08:05", presenter.formatDateTime(income.createdAt()));
        assertTrue(presenter.addedMessage(income).startsWith("Berhasil menambahkan [1]"));
        assertTrue(presenter.deletedMessage(expense).startsWith("Berhasil menghapus [2]"));
        assertEquals("Gagal: boom", presenter.failureMessage(new IllegalStateException("boom")));
    }
}
