package org.example;

import java.util.List;

public interface IRepositoryIncercare extends IRepository<Long, Incercare>{

    List<Incercare> getAllIncercariByJoc(Long idJoc);
}
