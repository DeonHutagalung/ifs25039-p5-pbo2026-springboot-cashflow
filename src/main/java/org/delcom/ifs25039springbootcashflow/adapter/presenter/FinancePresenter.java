package org.delcom.ifs25039springbootcashflow.adapter.presenter;

import org.delcom.ifs25039springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25039springbootcashflow.domain.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Format teks dan uang untuk tampilan; bukan tanggung jawab entity. */
public class FinancePresenter {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    /** Contoh: Rp 1,000,000.00 */
    public String formatMoney(long amount) {
        return String.format(Locale.US, "Rp %,.2f", new BigDecimal(amount));
    }

    public String typeLabel(TransactionType type) {
        return type == TransactionType.INCOME ? "Pemasukan" : "Pengeluaran";
    }

    /** Contoh: [1] Gaji | Rp 1,000,000.00 | [Pemasukan] */
    public String formatRow(Transaction transaction) {
        return "[" + transaction.id() + "] " + transaction.description() + " | "
                + formatMoney(transaction.amount()) + " | [" + typeLabel(transaction.type()) + "]";
    }

    /** Contoh: Saldo: Rp 4,975,000.00 */
    public String formatBalance(long balance) {
        return "Saldo: " + formatMoney(balance);
    }

    public String formatDateTime(LocalDateTime time) {
        return DATE_TIME.format(time);
    }

    public String addedMessage(Transaction transaction) {
        return "Berhasil menambahkan " + formatRow(transaction);
    }

    public String deletedMessage(Transaction transaction) {
        return "Berhasil menghapus " + formatRow(transaction);
    }

    public String failureMessage(Exception error) {
        return "Gagal: " + error.getMessage();
    }
}
