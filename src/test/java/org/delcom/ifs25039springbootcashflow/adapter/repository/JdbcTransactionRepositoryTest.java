package org.delcom.ifs25039springbootcashflow.adapter.repository;

import org.delcom.ifs25039springbootcashflow.domain.entity.Role;
import org.delcom.ifs25039springbootcashflow.domain.repository.ITransactionRepository;
import javax.sql.DataSource;

class JdbcTransactionRepositoryTest extends TransactionRepositoryContract {

    private DataSource dataSource;

    @Override
    ITransactionRepository create() {
        dataSource = JdbcTestSupport.newDataSource();
        return new JdbcTransactionRepository(dataSource);
    }

    @Override
    int createUser(String username) {
        return new JdbcUserRepository(dataSource).save(username, "h", username, Role.USER).id();
    }
}
