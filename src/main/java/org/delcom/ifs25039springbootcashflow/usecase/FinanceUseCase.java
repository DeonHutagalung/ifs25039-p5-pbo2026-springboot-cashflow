package org.delcom.ifs25039springbootcashflow.usecase;

import org.delcom.ifs25039springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25039springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25039springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25039springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25039springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25039springbootcashflow.domain.repository.ITransactionRepository;
import java.util.List;
import java.util.Locale;

/** Proses bisnis catatan keuangan; semua operasi dibatasi per pengguna. */
public class FinanceUseCase {

    private final ITransactionRepository repository;

    public FinanceUseCase(ITransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction addIncome(int userId, String description, String amountText, String proofPath)
            throws ValidationException {
        return add(userId, description, amountText, TransactionType.INCOME, proofPath);
    }

    public Transaction addExpense(int userId, String description, String amountText, String proofPath)
            throws ValidationException {
        return add(userId, description, amountText, TransactionType.EXPENSE, proofPath);
    }

    private Transaction add(int userId, String description, String amountText, TransactionType type,
            String proofPath) throws ValidationException {
        requireId(userId, "ID pengguna tidak valid");
        String cleanDescription = requireDescription(description);
        long amount = parseAmount(amountText);
        return repository.save(userId, cleanDescription, amount, type, proofPath);
    }

    public Transaction update(int userId, int transactionId, String description, String amountText,
            TransactionType type, String proofPath) throws ValidationException, EntityNotFoundException {
        Transaction existing = findOwned(userId, transactionId);
        String cleanDescription = requireDescription(description);
        long amount = parseAmount(amountText);
        return repository.save(new Transaction(existing.id(), existing.userId(), cleanDescription, amount, type,
                proofPath, existing.createdAt()));
    }

    public void delete(int userId, int transactionId) throws ValidationException, EntityNotFoundException {
        Transaction existing = findOwned(userId, transactionId);
        repository.deleteById(existing.id());
    }

    /** Daftar transaksi milik pengguna, difilter keterangan (case-insensitive) lalu diurutkan. */
    public List<Transaction> list(int userId, String keyword, SortOption sortOption) throws ValidationException {
        requireId(userId, "ID pengguna tidak valid");
        String needle = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        SortOption option = sortOption == null ? SortOption.NEWEST_FIRST : sortOption;
        return repository.findByUserId(userId).stream()
                .filter(t -> t.description().toLowerCase(Locale.ROOT).contains(needle))
                .sorted(option.comparator())
                .toList();
    }

    public long getTotalIncome(int userId) throws ValidationException {
        return total(userId, TransactionType.INCOME);
    }

    public long getTotalExpense(int userId) throws ValidationException {
        return total(userId, TransactionType.EXPENSE);
    }

    /** Saldo = total pemasukan dikurangi total pengeluaran. */
    public long getBalance(int userId) throws ValidationException {
        return getTotalIncome(userId) - getTotalExpense(userId);
    }

    private long total(int userId, TransactionType type) throws ValidationException {
        requireId(userId, "ID pengguna tidak valid");
        return repository.findByUserId(userId).stream()
                .filter(t -> t.type() == type)
                .mapToLong(Transaction::amount)
                .sum();
    }

    private Transaction findOwned(int userId, int transactionId) throws ValidationException, EntityNotFoundException {
        requireId(userId, "ID pengguna tidak valid");
        requireId(transactionId, "ID transaksi tidak valid");
        Transaction transaction = repository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaksi", transactionId));
        if (transaction.userId() != userId) {
            throw new ValidationException("Transaksi bukan milik Anda");
        }
        return transaction;
    }

    private static void requireId(int id, String message) throws ValidationException {
        if (id <= 0) {
            throw new ValidationException(message);
        }
    }

    private static String requireDescription(String description) throws ValidationException {
        String clean = description == null ? "" : description.trim();
        if (clean.isEmpty()) {
            throw new ValidationException("Keterangan tidak boleh kosong");
        }
        return clean;
    }

    private static long parseAmount(String amountText) throws ValidationException {
        long amount;
        try {
            amount = Long.parseLong(amountText == null ? "" : amountText.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Jumlah harus berupa angka bulat");
        }
        if (amount <= 0) {
            throw new ValidationException("Jumlah harus lebih dari 0");
        }
        return amount;
    }
}
