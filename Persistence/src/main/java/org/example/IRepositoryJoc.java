package org.example;

import java.util.List;

public interface IRepositoryJoc extends IRepository<Long, Joc>{
    List<Joc> findByAlias(String alias);
}
