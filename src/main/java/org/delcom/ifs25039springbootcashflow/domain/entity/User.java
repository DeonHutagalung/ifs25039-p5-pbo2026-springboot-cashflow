package org.delcom.ifs25039springbootcashflow.domain.entity;

/** Entity pengguna. */
public record User(
        int id,
        String username,
        String passwordHash,
        String fullName,
        Role role,
        String photoPath) {

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
