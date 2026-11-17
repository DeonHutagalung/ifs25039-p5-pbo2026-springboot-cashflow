package org.delcom.ifs25039springbootcashflow.framework.view;

import org.delcom.ifs25039springbootcashflow.adapter.storage.FileStorage;
import org.delcom.ifs25039springbootcashflow.domain.entity.User;
import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;
import org.delcom.ifs25039springbootcashflow.framework.auth.AuthSession;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import java.util.Optional;

/** Layout aplikasi: sidebar, menu avatar (Profil / Keluar), dan guard login global. */
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private final IUserRepository userRepository;
    private final Nav nav = new Nav();
    private final Div userMenuHolder = new Div();

    public MainLayout(IUserRepository userRepository) {
        this.userRepository = userRepository;
        setPrimarySection(Section.DRAWER);

        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Buka atau tutup menu navigasi");
        Span brand = new Span("Cashflow");
        brand.addClassName("cf-brand");
        Div spacer = new Div();
        spacer.getStyle().set("flex", "1");
        Div bar = new Div(toggle, brand, spacer, userMenuHolder);
        bar.getStyle().set("display", "flex").set("align-items", "center").set("width", "100%")
                .set("padding", "0 0.75rem").set("box-sizing", "border-box");
        addToNavbar(bar);

        Span drawerBrand = new Span("Menu");
        drawerBrand.addClassName("cf-brand");
        nav.addClassName("cf-nav");
        nav.getElement().setAttribute("aria-label", "Navigasi utama");
        addToDrawer(drawerBrand, nav);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<User> user = AuthSession.currentUser(userRepository);
        if (user.isEmpty()) {
            event.forwardTo(LoginView.class);
            return;
        }
        render(user.get());
    }

    /** Memperbarui avatar dan menu setelah profil berubah. */
    public void refreshUser() {
        AuthSession.currentUser(userRepository).ifPresent(this::render);
    }

    private void render(User user) {
        nav.removeAll();
        nav.add(link("Transaksi", FinanceView.class));
        nav.add(link("Profil", ProfileView.class));
        if (user.isAdmin()) {
            nav.add(link("Pengguna", UsersView.class));
        }

        Avatar avatar = new Avatar(user.fullName());
        if (user.photoPath() != null) {
            avatar.setImage(FileStorage.urlOf(user.photoPath()));
        }
        MenuBar menu = new MenuBar();
        menu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        var account = menu.addItem(avatar);
        account.getElement().setAttribute("aria-label", "Menu akun " + user.fullName());
        account.getSubMenu().addItem("Profil", e -> UI.getCurrent().navigate(ProfileView.class));
        account.getSubMenu().addItem("Keluar", e -> logout());
        userMenuHolder.removeAll();
        userMenuHolder.add(menu);
    }

    private static RouterLink link(String text, Class<? extends com.vaadin.flow.component.Component> target) {
        RouterLink link = new RouterLink(text, target);
        link.setHighlightCondition(HighlightConditions.sameLocation());
        return link;
    }

    private void logout() {
        AuthSession.logout();
        UI.getCurrent().navigate(LoginView.class);
    }
}
