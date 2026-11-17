package org.delcom.ifs25039springbootcashflow.framework.view;

import org.delcom.ifs25039springbootcashflow.domain.entity.User;
import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;
import org.delcom.ifs25039springbootcashflow.framework.auth.AuthSession;
import com.vaadin.flow.router.BeforeEnterEvent;
import java.util.Optional;

/** Guard halaman khusus ADMIN: role USER dialihkan ke halaman utama. */
final class AdminGuard {

    private AdminGuard() {
    }

    /** Mengembalikan admin yang login, atau mengalihkan navigasi dan mengembalikan kosong. */
    static Optional<User> require(BeforeEnterEvent event, IUserRepository userRepository) {
        Optional<User> admin = AuthSession.currentUser(userRepository).filter(User::isAdmin);
        if (admin.isEmpty()) {
            event.forwardTo(FinanceView.class);
        }
        return admin;
    }
}
