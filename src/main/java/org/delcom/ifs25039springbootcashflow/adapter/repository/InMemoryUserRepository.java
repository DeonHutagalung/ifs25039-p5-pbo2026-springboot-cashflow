package org.delcom.ifs25039springbootcashflow.adapter.repository;

import org.delcom.ifs25039springbootcashflow.domain.entity.Role;
import org.delcom.ifs25039springbootcashflow.domain.entity.User;
import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;
import java.util.Optional;

public class InMemoryUserRepository extends InMemoryRepository<User> implements IUserRepository {

    @Override
    protected int idOf(User entity) {
        return entity.id();
    }

    @Override
    protected String entityName() {
        return "Pengguna";
    }

    @Override
    public User save(String username, String passwordHash, String fullName, Role role) {
        User user = new User(nextId(), username, passwordHash, fullName, role, null);
        store.put(user.id(), user);
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return store.values().stream().filter(u -> u.username().equals(username)).findFirst();
    }
}
