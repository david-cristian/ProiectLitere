package org.example;

import java.util.Optional;

public interface IRepositoryJucator extends IRepository<Long, Jucator>{
    Optional<Jucator> findByAlias(String alias);
}
