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

@Route("register")
@PageTitle("Daftar | Cashflow")
public class RegisterView extends Main implements BeforeEnterObserver {

    private final AuthUseCase authUseCase;
    private final IUserRepository userRepository;
    private final TextField fullName = new TextField("Nama lengkap");
    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final PasswordField confirm = new PasswordField("Ulangi password");
    private final Button registerButton = new Button("Daftar");
    private final ProgressBar progress = new ProgressBar();

    public RegisterView(AuthUseCase authUseCase, IUserRepository userRepository) {
        this.authUseCase = authUseCase;
        this.userRepository = userRepository;
        addClassName("cf-auth");

        fullName.setId("register-fullname");
        username.setId("register-username");
        password.setId("register-password");
        confirm.setId("register-confirm");
        fullName.setWidthFull();
        username.setWidthFull();
        password.setWidthFull();
        confirm.setWidthFull();
        registerButton.setId("register-submit");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.addClickShortcut(Key.ENTER);
        registerButton.addClickListener(e -> submit());
        progress.setIndeterminate(true);
        progress.setVisible(false);
        progress.getElement().setAttribute("aria-label", "Memproses pendaftaran");

        RouterLink login = new RouterLink("Sudah punya akun? Masuk di sini", LoginView.class);
        login.addClassName("cf-link");

        Div form = new Div(fullName, username, password, confirm, registerButton, progress);
        form.addClassName("cf-form");
        Div card = new Div(new H1("Buat akun Cashflow"),
                new Paragraph("Daftar untuk mulai mencatat keuangan pribadi."), form, login);
        card.addClassName("cf-card");
        add(card);
    }

    private void submit() {
        setBusy(true);
        try {
            if (!password.getValue().equals(confirm.getValue())) {
                throw new ValidationException("Konfirmasi password tidak sama");
            }
            User user = authUseCase.register(username.getValue(), password.getValue(), fullName.getValue());
            AuthSession.login(user);
            Notices.success("Registrasi berhasil, selamat datang " + user.fullName());
            UI.getCurrent().navigate(FinanceView.class);
        } catch (ValidationException e) {
            Notices.error(e.getMessage());
        } finally {
            setBusy(false);
        }
    }

    private void setBusy(boolean busy) {
        progress.setVisible(busy);
        registerButton.setEnabled(!busy);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (AuthSession.currentUser(userRepository).isPresent()) {
            event.forwardTo(FinanceView.class);
        }
    }
}
