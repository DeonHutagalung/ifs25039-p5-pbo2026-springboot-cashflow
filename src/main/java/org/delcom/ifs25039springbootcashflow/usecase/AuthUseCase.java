package org.delcom.ifs25039springbootcashflow.usecase;

import org.delcom.ifs25039springbootcashflow.domain.PasswordHasher;
import org.delcom.ifs25039springbootcashflow.domain.entity.Role;
import org.delcom.ifs25039springbootcashflow.domain.entity.User;
import org.delcom.ifs25039springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;

/** Proses bisnis autentikasi: registrasi, login, dan seed admin. */
public class AuthUseCase {

    private static final int MIN_PASSWORD_LENGTH = 4;

    private final IUserRepository userRepository;

    public AuthUseCase(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String password, String fullName) throws ValidationException {
        String cleanUsername = clean(username);
        String cleanName = clean(fullName);
        if (cleanUsername.isEmpty()) {
            throw new ValidationException("Username tidak boleh kosong");
        }
        if (cleanName.isEmpty()) {
            throw new ValidationException("Nama lengkap tidak boleh kosong");
        }
        requirePassword(password);
        if (userRepository.findByUsername(cleanUsername).isPresent()) {
            throw new ValidationException("Username sudah digunakan");
        }
        return userRepository.save(cleanUsername, PasswordHasher.hash(password), cleanName, Role.USER);
    }

    public User login(String username, String password) throws ValidationException {
        String cleanUsername = clean(username);
        if (cleanUsername.isEmpty() || password == null || password.isEmpty()) {
            throw new ValidationException("Username dan password wajib diisi");
        }
        return userRepository.findByUsername(cleanUsername)
                .filter(user -> PasswordHasher.matches(password, user.passwordHash()))
                .orElseThrow(() -> new ValidationException("Username atau password salah"));
    }

    /** Membuat admin dari konfigurasi ROOT_* bila belum ada. */
    public User ensureRootAdmin(String username, String password) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.save(username, PasswordHasher.hash(password), "Administrator", Role.ADMIN));
    }

    private static void requirePassword(String password) throws ValidationException {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("Password minimal " + MIN_PASSWORD_LENGTH + " karakter");
        }
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
