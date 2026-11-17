package org.delcom.ifs25039springbootcashflow.adapter.repository;

import org.delcom.ifs25039springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25039springbootcashflow.domain.repository.IRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Basis repository in-memory generik (untuk unit test). */
public abstract class InMemoryRepository<T> implements IRepository<T, Integer> {

    protected final Map<Integer, T> store = new LinkedHashMap<>();
    private int sequence = 0;

    protected abstract int idOf(T entity);

    protected abstract String entityName();

    protected int nextId() {
        sequence++;
        return sequence;
    }

    @Override
    public T save(T entity) throws EntityNotFoundException {
        int id = idOf(entity);
        if (!store.containsKey(id)) {
            throw new EntityNotFoundException(entityName(), id);
        }
        store.put(id, entity);
        return entity;
    }

    @Override
    public Optional<T> findById(Integer id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean deleteById(Integer id) {
        return store.remove(id) != null;
    }
}
