package org.delcom.ifs25039springbootcashflow.domain.repository;

import org.delcom.ifs25039springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25039springbootcashflow.domain.entity.TransactionType;
import java.util.List;

/** Port penyimpanan transaksi. */
public interface ITransactionRepository extends IRepository<Transaction, Integer> {

    /** Membuat transaksi baru dan mengembalikan entity dengan ID hasil generate. */
    Transaction save(int userId, String description, long amount, TransactionType type, String proofPath);

    List<Transaction> findByUserId(int userId);
}
