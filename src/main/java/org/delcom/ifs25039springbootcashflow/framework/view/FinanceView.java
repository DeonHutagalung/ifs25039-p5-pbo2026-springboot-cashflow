package org.delcom.ifs25039springbootcashflow.framework.view;

import org.delcom.ifs25039springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25039springbootcashflow.adapter.storage.FileStorage;
import org.delcom.ifs25039springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25039springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25039springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25039springbootcashflow.domain.entity.User;
import org.delcom.ifs25039springbootcashflow.domain.exception.DomainException;
import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;
import org.delcom.ifs25039springbootcashflow.framework.auth.AuthSession;
import org.delcom.ifs25039springbootcashflow.usecase.FinanceUseCase;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;

/** Dashboard transaksi keuangan milik pengguna yang sedang login. */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Catatan Keuangan | Cashflow")
public class FinanceView extends Main {

    private final FinanceUseCase finance;
    private final FinancePresenter presenter;
    private final IUserRepository userRepository;
    private final FileStorage fileStorage;

    private final SummaryCards summary;
    private final TextField search = new TextField("Cari keterangan");
    private final Select<SortOption> sort = new Select<>();
    private final TransactionGrid grid;

    public FinanceView(FinanceUseCase finance, FinancePresenter presenter, IUserRepository userRepository,
            FileStorage fileStorage) {
        this.finance = finance;
        this.presenter = presenter;
        this.userRepository = userRepository;
        this.fileStorage = fileStorage;
        addClassName("cf-page");

        summary = new SummaryCards(presenter);
        grid = new TransactionGrid(presenter, this::confirmDelete);

        search.setPlaceholder("Ketik keterangan...");
        search.setClearButtonVisible(true);
        search.setValueChangeMode(ValueChangeMode.LAZY);
        search.addValueChangeListener(e -> refresh());
        search.setWidthFull();

        sort.setLabel("Urutkan");
        sort.setItems(SortOption.values());
        sort.setItemLabelGenerator(SortOption::label);
        sort.setValue(SortOption.NEWEST_FIRST);
        sort.addValueChangeListener(e -> refresh());

        Button reload = new Button("Muat ulang", e -> refresh());
        Button addIncome = new Button("Tambah pemasukan", e -> openDialog(TransactionType.INCOME));
        addIncome.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        Button addExpense = new Button("Tambah pengeluaran", e -> openDialog(TransactionType.EXPENSE));
        addExpense.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Div searchBox = new Div(search);
        searchBox.addClassName("cf-grow");
        Div toolbar = new Div(searchBox, sort, reload, addIncome, addExpense);
        toolbar.addClassName("cf-toolbar");

        add(new H1("Catatan Keuangan"), summary, toolbar, grid);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        refresh();
    }

    private User user() {
        return AuthSession.currentUser(userRepository).orElseThrow();
    }

    private void refresh() {
        if (AuthSession.currentUser(userRepository).isEmpty()) {
            return;
        }
        try {
            int id = user().id();
            List<Transaction> items = finance.list(id, search.getValue(), sort.getValue());
            grid.setItems(items);
            summary.update(finance.list(id, null, null).size(), finance.getTotalIncome(id),
                    finance.getTotalExpense(id), finance.getBalance(id));
        } catch (DomainException e) {
            Notices.error(presenter.failureMessage(e));
        }
    }

    private void openDialog(TransactionType type) {
        new AddTransactionDialog(type, user().id(), finance, presenter, fileStorage, this::refresh).open();
    }

    private void confirmDelete(Transaction transaction) {
        AppDialog dialog = new AppDialog("Hapus transaksi", "Konfirmasi");
        dialog.add(new Paragraph("Hapus transaksi berikut? " + presenter.formatRow(transaction)));
        Button delete = new Button("Hapus", e -> {
            try {
                finance.delete(user().id(), transaction.id());
                Notices.success(presenter.deletedMessage(transaction));
                dialog.close();
                refresh();
            } catch (DomainException ex) {
                Notices.error(presenter.failureMessage(ex));
            }
        });
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        Button cancel = new Button("Batal", e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancel, delete);
        dialog.open();
    }
}
