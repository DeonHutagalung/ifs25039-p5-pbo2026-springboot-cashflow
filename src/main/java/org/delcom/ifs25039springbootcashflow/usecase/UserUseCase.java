package org.delcom.ifs25039springbootcashflow.usecase;

import org.delcom.ifs25039springbootcashflow.domain.PasswordHasher;
import org.delcom.ifs25039springbootcashflow.domain.entity.User;
import org.delcom.ifs25039springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25039springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;
import java.util.List;

/** Proses bisnis pengguna: daftar, detail, profil, dan foto. */
public class UserUseCase {

    private static final int MIN_PASSWORD_LENGTH = 4;

    private final IUserRepository userRepository;

    public UserUseCase(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public User getUser(int id) throws EntityNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Pengguna", id));
    }

    /** Ubah nama; password hanya diganti bila newPassword tidak kosong. */
    public User updateProfile(int id, String fullName, String newPassword)
            throws ValidationException, EntityNotFoundException {
        User user = getUser(id);
        String cleanName = fullName == null ? "" : fullName.trim();
        if (cleanName.isEmpty()) {
            throw new ValidationException("Nama lengkap tidak boleh kosong");
        }
        String hash = user.passwordHash();
        if (newPassword != null && !newPassword.isEmpty()) {
            if (newPassword.length() < MIN_PASSWORD_LENGTH) {
                throw new ValidationException("Password minimal " + MIN_PASSWORD_LENGTH + " karakter");
            }
            hash = PasswordHasher.hash(newPassword);
        }
        return userRepository.save(new User(user.id(), user.username(), hash, cleanName, user.role(),
                user.photoPath()));
    }

    public User updatePhoto(int id, String photoPath) throws EntityNotFoundException {
        User user = getUser(id);
        return userRepository.save(new User(user.id(), user.username(), user.passwordHash(), user.fullName(),
                user.role(), photoPath));
    }
}
