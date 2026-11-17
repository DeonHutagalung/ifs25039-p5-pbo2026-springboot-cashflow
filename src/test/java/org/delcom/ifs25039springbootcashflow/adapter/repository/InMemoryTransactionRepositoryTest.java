package org.delcom.ifs25039springbootcashflow.adapter.repository;

import org.delcom.ifs25039springbootcashflow.domain.repository.ITransactionRepository;

class InMemoryTransactionRepositoryTest extends TransactionRepositoryContract {
    @Override
    ITransactionRepository create() {
        return new InMemoryTransactionRepository();
    }

    @Override
    int createUser(String username) {
        return username.hashCode() & 0xffff | 1;
    }
}
