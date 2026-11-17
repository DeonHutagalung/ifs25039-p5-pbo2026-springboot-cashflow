package org.delcom.ifs25039springbootcashflow.framework.view;

import org.delcom.ifs25039springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25039springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25039springbootcashflow.domain.entity.User;
import org.delcom.ifs25039springbootcashflow.domain.exception.DomainException;
import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;
import org.delcom.ifs25039springbootcashflow.usecase.FinanceUseCase;
import org.delcom.ifs25039springbootcashflow.usecase.UserUseCase;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

/** Transaksi pengguna lain, read-only (khusus ADMIN). Parameter query: userId. */
@Route(value = "user-transactions", layout = MainLayout.class)
@PageTitle("Transaksi Pengguna | Cashflow")
public class UserTransactionsView extends Main implements BeforeEnterObserver {

    private final FinanceUseCase finance;
    private final UserUseCase userUseCase;
    private final FinancePresenter presenter;
    private final IUserRepository userRepository;

    public UserTransactionsView(FinanceUseCase finance, UserUseCase userUseCase, FinancePresenter presenter,
            IUserRepository userRepository) {
        this.finance = finance;
        this.userUseCase = userUseCase;
        this.presenter = presenter;
        this.userRepository = userRepository;
        addClassName("cf-page");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (AdminGuard.require(event, userRepository).isEmpty()) {
            return;
        }
        try {
            int userId = Integer.parseInt(event.getLocation().getQueryParameters()
                    .getSingleParameter("userId").orElse(""));
            User target = userUseCase.getUser(userId);
            removeAll();
            SummaryCards summary = new SummaryCards(presenter);
            summary.update(finance.list(userId, null, null).size(), finance.getTotalIncome(userId),
                    finance.getTotalExpense(userId), finance.getBalance(userId));
            TransactionGrid grid = new TransactionGrid(presenter, null);
            grid.setItems(finance.list(userId, null, SortOption.NEWEST_FIRST));
            add(new H1("Transaksi " + target.fullName()),
                    new Paragraph("Tampilan baca-saja untuk akun " + target.username() + "."),
                    summary, grid, new RouterLink("Kembali ke daftar pengguna", UsersView.class));
        } catch (NumberFormatException | DomainException e) {
            Notices.error("Pengguna tidak valid atau tidak ditemukan");
            event.forwardTo(UsersView.class);
        }
    }
}
