package org.delcom.ifs25039springbootcashflow.domain.repository;

import org.delcom.ifs25039springbootcashflow.domain.entity.Role;
import org.delcom.ifs25039springbootcashflow.domain.entity.User;
import java.util.Optional;

/** Port penyimpanan pengguna. */
public interface IUserRepository extends IRepository<User, Integer> {

    /** Membuat pengguna baru dan mengembalikan entity dengan ID hasil generate. */
    User save(String username, String passwordHash, String fullName, Role role);

    Optional<User> findByUsername(String username);
}
