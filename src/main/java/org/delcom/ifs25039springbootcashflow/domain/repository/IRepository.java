package org.delcom.ifs25039springbootcashflow.domain.repository;

import org.delcom.ifs25039springbootcashflow.domain.exception.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

/** Port generik untuk penyimpanan entity. */
public interface IRepository<T, ID> {

    /** Memperbarui entity yang sudah ada. */
    T save(T entity) throws EntityNotFoundException;

    Optional<T> findById(ID id);

    List<T> findAll();

    boolean deleteById(ID id);
}
