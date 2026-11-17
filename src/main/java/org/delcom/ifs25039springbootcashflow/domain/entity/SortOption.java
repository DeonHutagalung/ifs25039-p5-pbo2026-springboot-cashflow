package org.delcom.ifs25039springbootcashflow.domain.entity;

import java.util.Comparator;

/** Opsi pengurutan transaksi; setiap opsi membawa Comparator. */
public enum SortOption {
    NEWEST_FIRST("Terbaru dulu",
            Comparator.comparing(Transaction::createdAt).thenComparingInt(Transaction::id).reversed()),
    OLDEST_FIRST("Terlama dulu",
            Comparator.comparing(Transaction::createdAt).thenComparingInt(Transaction::id)),
    AMOUNT_ASC("Jumlah naik",
            Comparator.comparingLong(Transaction::amount).thenComparingInt(Transaction::id)),
    AMOUNT_DESC("Jumlah turun",
            Comparator.comparingLong(Transaction::amount).reversed().thenComparingInt(Transaction::id)),
    INCOME_FIRST("Pemasukan dulu",
            Comparator.comparing(Transaction::type).thenComparingInt(Transaction::id)),
    EXPENSE_FIRST("Pengeluaran dulu",
            Comparator.comparing(Transaction::type).reversed().thenComparingInt(Transaction::id));

    private final String label;
    private final Comparator<Transaction> comparator;

    SortOption(String label, Comparator<Transaction> comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    public String label() {
        return label;
    }

    public Comparator<Transaction> comparator() {
        return comparator;
    }
}
