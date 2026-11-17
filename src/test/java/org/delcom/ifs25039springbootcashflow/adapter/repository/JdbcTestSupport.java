package org.delcom.ifs25039springbootcashflow.adapter.repository;

import java.util.UUID;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

final class JdbcTestSupport {

    private JdbcTestSupport() {
    }

    /** DataSource H2 in-memory (mode PostgreSQL) baru dengan skema terinisialisasi. */
    static DataSource newDataSource() {
        DataSource ds = new DriverManagerDataSource(
                "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", "");
        SchemaInitializer.initialize(ds);
        return ds;
    }
}
