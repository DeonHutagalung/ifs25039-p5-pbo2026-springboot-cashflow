package org.delcom.ifs25039springbootcashflow.adapter.repository;

import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;

class InMemoryUserRepositoryTest extends UserRepositoryContract {
    @Override
    IUserRepository create() {
        return new InMemoryUserRepository();
    }
}
