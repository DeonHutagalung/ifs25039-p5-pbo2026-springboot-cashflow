package org.delcom.ifs25039springbootcashflow.domain.entity;

import java.time.LocalDateTime;

/** Entity transaksi keuangan milik satu pengguna. */
public record Transaction(
        int id,
        int userId,
        String description,
        long amount,
        TransactionType type,
        String proofPath,
        LocalDateTime createdAt) {

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }
}
