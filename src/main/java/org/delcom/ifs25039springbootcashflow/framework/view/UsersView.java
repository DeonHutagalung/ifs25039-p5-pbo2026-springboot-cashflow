package org.delcom.ifs25039springbootcashflow.framework.view;

import org.delcom.ifs25039springbootcashflow.domain.entity.User;
import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;
import org.delcom.ifs25039springbootcashflow.usecase.UserUseCase;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import java.util.Comparator;
import java.util.Map;

/** Daftar seluruh pengguna (khusus ADMIN). */
@Route(value = "users", layout = MainLayout.class)
@PageTitle("Pengguna | Cashflow")
public class UsersView extends Main implements BeforeEnterObserver {

    private final UserUseCase userUseCase;
    private final IUserRepository userRepository;
    private final Grid<User> grid = new Grid<>(User.class, false);

    public UsersView(UserUseCase userUseCase, IUserRepository userRepository) {
        this.userUseCase = userUseCase;
        this.userRepository = userRepository;
        addClassName("cf-page");

        GridStyles.apply(grid, "Daftar pengguna");
        grid.addColumn(User::id).setHeader("ID").setAutoWidth(true)
                .setComparator(Comparator.comparingInt(User::id)).setSortable(true);
        grid.addColumn(User::username).setHeader("Username").setComparator(Comparator.comparing(User::username))
                .setSortable(true);
        grid.addColumn(User::fullName).setHeader("Nama lengkap").setFlexGrow(2)
                .setComparator(Comparator.comparing(User::fullName)).setSortable(true);
        grid.addColumn(u -> u.role().name()).setHeader("Peran").setAutoWidth(true);
        grid.addComponentColumn(u -> {
            RouterLink link = new RouterLink("Lihat transaksi " + u.fullName(), UserTransactionsView.class);
            link.setQueryParameters(QueryParameters.simple(Map.of("userId", String.valueOf(u.id()))));
            return link;
        }).setHeader("Transaksi").setFlexGrow(2);

        add(new H1("Daftar Pengguna"), grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        AdminGuard.require(event, userRepository);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        grid.setItems(userUseCase.listUsers());
    }
}
