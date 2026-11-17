package org.delcom.ifs25039springbootcashflow.adapter.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;
import org.junit.jupiter.api.Test;

class JdbcUserRepositoryTest extends UserRepositoryContract {
    @Override
    IUserRepository create() {
        return new JdbcUserRepository(JdbcTestSupport.newDataSource());
    }

    @Test
    void schemaInitializerIsIdempotent() {
        javax.sql.DataSource ds = JdbcTestSupport.newDataSource();
        SchemaInitializer.initialize(ds);
        assertEquals(0, new JdbcUserRepository(ds).findAll().size());
    }
}
