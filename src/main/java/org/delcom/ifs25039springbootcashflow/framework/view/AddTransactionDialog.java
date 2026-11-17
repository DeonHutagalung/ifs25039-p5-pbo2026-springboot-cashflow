package org.delcom.ifs25039springbootcashflow.framework.view;

import org.delcom.ifs25039springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25039springbootcashflow.adapter.storage.FileStorage;
import org.delcom.ifs25039springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25039springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25039springbootcashflow.domain.exception.DomainException;
import org.delcom.ifs25039springbootcashflow.usecase.FinanceUseCase;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import java.io.IOException;

/** Dialog tambah pemasukan / pengeluaran dengan bukti gambar opsional. */
public class AddTransactionDialog extends AppDialog {

    private final TransactionType type;
    private final int userId;
    private final FinanceUseCase finance;
    private final FinancePresenter presenter;
    private final FileStorage fileStorage;
    private final Runnable onSaved;

    private final TextField description = new TextField("Keterangan");
    private final TextField amount = new TextField("Jumlah (Rp)");
    private final ImageUploader uploader = new ImageUploader();
    private final ProgressBar progress = new ProgressBar();
    private final Button save = new Button("Simpan");

    public AddTransactionDialog(TransactionType type, int userId, FinanceUseCase finance,
            FinancePresenter presenter, FileStorage fileStorage, Runnable onSaved) {
        super(type == TransactionType.INCOME ? "Tambah pemasukan" : "Tambah pengeluaran",
                presenter.typeLabel(type));
        this.type = type;
        this.userId = userId;
        this.finance = finance;
        this.presenter = presenter;
        this.fileStorage = fileStorage;
        this.onSaved = onSaved;

        description.setWidthFull();
        description.setId("transaction-description");
        amount.setWidthFull();
        amount.setId("transaction-amount");
        amount.setPlaceholder("contoh: 1000000");
        amount.setAllowedCharPattern("[0-9]");
        Span proofLabel = new Span("Bukti transaksi (opsional, gambar maks. 5 MB)");
        progress.setIndeterminate(true);
        progress.setVisible(false);
        progress.getElement().setAttribute("aria-label", "Menyimpan transaksi");
        body.add(description, amount, proofLabel, uploader, progress);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e -> submit());
        getFooter().add(closeButton("Batal"), save);
    }

    private void submit() {
        save.setEnabled(false);
        progress.setVisible(true);
        String stored = null;
        try {
            if (uploader.hasFile()) {
                stored = fileStorage.store(uploader.fileName(), uploader.content());
            }
            Transaction created = type == TransactionType.INCOME
                    ? finance.addIncome(userId, description.getValue(), amount.getValue(), stored)
                    : finance.addExpense(userId, description.getValue(), amount.getValue(), stored);
            Notices.success(presenter.addedMessage(created));
            close();
            onSaved.run();
        } catch (DomainException | IOException e) {
            discard(stored);
            Notices.error(presenter.failureMessage(e));
        } finally {
            save.setEnabled(true);
            progress.setVisible(false);
        }
    }

    private void discard(String storedName) {
        if (storedName != null) {
            try {
                fileStorage.delete(storedName);
            } catch (IOException ignored) {
                // berkas yatim tidak mengganggu fungsi aplikasi
            }
        }
    }
}
