package org.example;

import java.util.List;
import java.util.Optional;

public interface IRepository<ID extends Number, T extends Entity<ID>> {
    T save(T entity);
    T update(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
}
