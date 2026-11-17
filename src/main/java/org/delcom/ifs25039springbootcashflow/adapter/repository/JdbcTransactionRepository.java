package org.delcom.ifs25039springbootcashflow.adapter.repository;

import org.delcom.ifs25039springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25039springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25039springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25039springbootcashflow.domain.repository.ITransactionRepository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/** Implementasi JDBC (PostgreSQL) untuk penyimpanan transaksi. */
public class JdbcTransactionRepository implements ITransactionRepository {

    private static final RowMapper<Transaction> MAPPER = (rs, rowNum) -> new Transaction(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getString("description"),
            rs.getLong("amount"),
            TransactionType.valueOf(rs.getString("type")),
            rs.getString("proof_path"),
            rs.getTimestamp("created_at").toLocalDateTime());

    private final JdbcTemplate jdbc;

    public JdbcTransactionRepository(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    @Override
    public Transaction save(int userId, String description, long amount, TransactionType type, String proofPath) {
        LocalDateTime now = LocalDateTime.now();
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO transactions (user_id, description, amount, type, proof_path, created_at)"
                            + " VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setString(2, description);
            ps.setLong(3, amount);
            ps.setString(4, type.name());
            ps.setString(5, proofPath);
            ps.setTimestamp(6, Timestamp.valueOf(now));
            return ps;
        }, keys);
        int id = ((Number) keys.getKeys().get("id")).intValue();
        return new Transaction(id, userId, description, amount, type, proofPath, now);
    }

    @Override
    public Transaction save(Transaction transaction) throws EntityNotFoundException {
        int updated = jdbc.update(
                "UPDATE transactions SET description = ?, amount = ?, type = ?, proof_path = ? WHERE id = ?",
                transaction.description(), transaction.amount(), transaction.type().name(),
                transaction.proofPath(), transaction.id());
        if (updated == 0) {
            throw new EntityNotFoundException("Transaksi", transaction.id());
        }
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(Integer id) {
        return jdbc.query("SELECT * FROM transactions WHERE id = ?", MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Transaction> findByUserId(int userId) {
        return jdbc.query("SELECT * FROM transactions WHERE user_id = ? ORDER BY id", MAPPER, userId);
    }

    @Override
    public List<Transaction> findAll() {
        return jdbc.query("SELECT * FROM transactions ORDER BY id", MAPPER);
    }

    @Override
    public boolean deleteById(Integer id) {
        return jdbc.update("DELETE FROM transactions WHERE id = ?", id) > 0;
    }
}
