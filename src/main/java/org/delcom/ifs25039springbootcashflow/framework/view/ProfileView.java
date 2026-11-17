package org.delcom.ifs25039springbootcashflow.framework.view;

import org.delcom.ifs25039springbootcashflow.adapter.storage.FileStorage;
import org.delcom.ifs25039springbootcashflow.domain.entity.User;
import org.delcom.ifs25039springbootcashflow.domain.exception.DomainException;
import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;
import org.delcom.ifs25039springbootcashflow.framework.auth.AuthSession;
import org.delcom.ifs25039springbootcashflow.usecase.UserUseCase;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.io.IOException;
import java.util.Optional;

/** Pengaturan profil: nama, password, dan foto. */
@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profil | Cashflow")
public class ProfileView extends Main {

    private final UserUseCase userUseCase;
    private final IUserRepository userRepository;
    private final FileStorage fileStorage;

    private final Div avatarHolder = new Div();
    private final Paragraph usernameInfo = new Paragraph();
    private final TextField fullName = new TextField("Nama lengkap");
    private final PasswordField password = new PasswordField("Password baru (kosongkan jika tidak diganti)");
    private final Button save = new Button("Simpan perubahan");
    private final ProgressBar progress = new ProgressBar();

    public ProfileView(UserUseCase userUseCase, IUserRepository userRepository, FileStorage fileStorage) {
        this.userUseCase = userUseCase;
        this.userRepository = userRepository;
        this.fileStorage = fileStorage;
        addClassName("cf-page");

        fullName.setWidthFull();
        fullName.setId("profile-fullname");
        password.setWidthFull();
        password.setId("profile-password");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e -> saveProfile());
        Button changePhoto = new Button("Ubah foto", e -> openPhotoDialog());
        progress.setIndeterminate(true);
        progress.setVisible(false);
        progress.getElement().setAttribute("aria-label", "Menyimpan profil");

        Div form = new Div(fullName, password, save, progress);
        form.addClassName("cf-form");
        form.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "0.75rem");
        Div identity = new Div(avatarHolder, usernameInfo, changePhoto);
        identity.getStyle().set("display", "flex").set("align-items", "center").set("gap", "1rem")
                .set("flex-wrap", "wrap").set("margin-bottom", "1rem");

        add(new H1("Profil"), new PageCard("Data akun", identity, form));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        AuthSession.currentUser(userRepository).ifPresent(this::show);
    }

    private void show(User user) {
        Avatar avatar = new Avatar(user.fullName());
        if (user.photoPath() != null) {
            avatar.setImage(FileStorage.urlOf(user.photoPath()));
        }
        avatar.getStyle().set("--vaadin-avatar-size", "4rem");
        avatarHolder.removeAll();
        avatarHolder.add(avatar);
        usernameInfo.setText("Username: " + user.username() + " (" + user.role().name() + ")");
        fullName.setValue(user.fullName());
        password.clear();
    }

    private void saveProfile() {
        Optional<User> current = AuthSession.currentUser(userRepository);
        if (current.isEmpty()) {
            return;
        }
        save.setEnabled(false);
        progress.setVisible(true);
        try {
            User updated = userUseCase.updateProfile(current.get().id(), fullName.getValue(), password.getValue());
            show(updated);
            refreshLayout();
            Notices.success("Profil berhasil diperbarui");
        } catch (DomainException e) {
            Notices.error("Gagal: " + e.getMessage());
        } finally {
            save.setEnabled(true);
            progress.setVisible(false);
        }
    }

    private void openPhotoDialog() {
        Optional<User> current = AuthSession.currentUser(userRepository);
        if (current.isEmpty()) {
            return;
        }
        AppDialog dialog = new AppDialog("Ubah foto profil", "Unggah");
        ImageUploader uploader = new ImageUploader();
        dialog.add(new Paragraph("Pilih gambar (png, jpg, gif, webp) maksimal 5 MB."), uploader);
        Button cancel = new Button("Batal", e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button upload = new Button("Simpan foto", e -> {
            if (!uploader.hasFile()) {
                Notices.error("Pilih gambar terlebih dahulu");
                return;
            }
            try {
                String name = fileStorage.store(uploader.fileName(), uploader.content());
                User updated = userUseCase.updatePhoto(current.get().id(), name);
                show(updated);
                refreshLayout();
                Notices.success("Foto profil berhasil diperbarui");
                dialog.close();
            } catch (DomainException | IOException ex) {
                Notices.error("Gagal: " + ex.getMessage());
            }
        });
        upload.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(cancel, upload);
        dialog.open();
    }

    private void refreshLayout() {
        Optional<Component> parent = getParent();
        while (parent.isPresent() && !(parent.get() instanceof MainLayout)) {
            parent = parent.get().getParent();
        }
        parent.ifPresent(layout -> ((MainLayout) layout).refreshUser());
    }
}
