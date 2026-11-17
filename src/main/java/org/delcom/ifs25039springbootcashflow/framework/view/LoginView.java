package org.delcom.ifs25039springbootcashflow.framework.view;

import org.delcom.ifs25039springbootcashflow.domain.entity.User;
import org.delcom.ifs25039springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;
import org.delcom.ifs25039springbootcashflow.framework.auth.AuthSession;
import org.delcom.ifs25039springbootcashflow.usecase.AuthUseCase;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("login")
@PageTitle("Masuk | Cashflow")
public class LoginView extends Main implements BeforeEnterObserver {

    private final AuthUseCase authUseCase;
    private final IUserRepository userRepository;
    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final Button loginButton = new Button("Masuk");
    private final ProgressBar progress = new ProgressBar();

    public LoginView(AuthUseCase authUseCase, IUserRepository userRepository) {
        this.authUseCase = authUseCase;
        this.userRepository = userRepository;
        addClassName("cf-auth");

        username.setId("login-username");
        username.setAutocomplete(com.vaadin.flow.component.textfield.Autocomplete.USERNAME);
        username.setWidthFull();
        password.setId("login-password");
        password.setAutocomplete(com.vaadin.flow.component.textfield.Autocomplete.CURRENT_PASSWORD);
        password.setWidthFull();
        loginButton.setId("login-submit");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.addClickShortcut(Key.ENTER);
        loginButton.addClickListener(e -> submit());
        progress.setIndeterminate(true);
        progress.setVisible(false);
        progress.getElement().setAttribute("aria-label", "Memproses login");

        RouterLink register = new RouterLink("Belum punya akun? Daftar di sini", RegisterView.class);
        register.addClassName("cf-link");

        Div form = new Div(username, password, loginButton, progress);
        form.addClassName("cf-form");
        Div card = new Div(new H1("Masuk ke Cashflow"),
                new Paragraph("Catat pemasukan dan pengeluaran dengan mudah."), form, register);
        card.addClassName("cf-card");
        add(card);
    }

    private void submit() {
        setBusy(true);
        try {
            User user = authUseCase.login(username.getValue(), password.getValue());
            AuthSession.login(user);
            UI.getCurrent().navigate(FinanceView.class);
        } catch (ValidationException e) {
            Notices.error(e.getMessage());
        } finally {
            setBusy(false);
        }
    }

    private void setBusy(boolean busy) {
        progress.setVisible(busy);
        loginButton.setEnabled(!busy);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (AuthSession.currentUser(userRepository).isPresent()) {
            event.forwardTo(FinanceView.class);
        }
    }
}
