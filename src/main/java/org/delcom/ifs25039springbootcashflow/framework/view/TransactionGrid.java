package org.delcom.ifs25039springbootcashflow.framework.view;

import org.delcom.ifs25039springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25039springbootcashflow.adapter.storage.FileStorage;
import org.delcom.ifs25039springbootcashflow.domain.entity.Transaction;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import java.util.Comparator;
import java.util.function.Consumer;

/** Grid transaksi; kolom aksi hapus hanya muncul bila onDelete diberikan (mode baca-saja bila null). */
public class TransactionGrid extends Grid<Transaction> {

    public TransactionGrid(FinancePresenter presenter, Consumer<Transaction> onDelete) {
        super(Transaction.class, false);
        GridStyles.apply(this, "Daftar transaksi");

        addColumn(Transaction::id).setHeader("ID").setAutoWidth(true)
                .setComparator(Comparator.comparingInt(Transaction::id)).setSortable(true);
        addColumn(Transaction::description).setHeader("Keterangan").setFlexGrow(2)
                .setComparator(Comparator.comparing((Transaction t) -> t.description().toLowerCase())).setSortable(true);
        addComponentColumn(t -> {
            Span amount = new Span(presenter.formatMoney(t.amount()));
            amount.addClassName(t.isIncome() ? "cf-income" : "cf-expense");
            return amount;
        }).setHeader("Jumlah").setAutoWidth(true).setComparator(Comparator.comparingLong(Transaction::amount)).setSortable(true);
        addColumn(t -> presenter.typeLabel(t.type())).setHeader("Tipe").setAutoWidth(true)
                .setComparator(Comparator.comparing(Transaction::type)).setSortable(true);
        addComponentColumn(t -> proof(t)).setHeader("Bukti").setAutoWidth(true);
        addColumn(t -> presenter.formatDateTime(t.createdAt())).setHeader("Waktu").setAutoWidth(true)
                .setComparator(Comparator.comparing(Transaction::createdAt)).setSortable(true);
        if (onDelete != null) {
            addComponentColumn(t -> {
                Button delete = new Button("Hapus", e -> onDelete.accept(t));
                delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
                delete.getElement().setAttribute("aria-label", "Hapus transaksi " + t.id());
                return delete;
            }).setHeader("Aksi").setAutoWidth(true);
        }
    }

    private static com.vaadin.flow.component.Component proof(Transaction t) {
        if (t.proofPath() == null) {
            return new Span("-");
        }
        String url = FileStorage.urlOf(t.proofPath());
        Image thumb = new Image(url, "Bukti transaksi " + t.description());
        thumb.addClassName("cf-thumb");
        Button zoom = new Button(thumb, e -> new ImagePreviewDialog(url, "Bukti transaksi " + t.description()).open());
        zoom.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        zoom.getElement().setAttribute("aria-label", "Perbesar bukti transaksi " + t.id());
        return zoom;
    }
}
