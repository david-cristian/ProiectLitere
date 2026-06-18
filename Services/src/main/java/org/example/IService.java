package org.example;

import org.example.dto.JocIncercariDTO;
import org.example.dto.RezultatIncercareDTO;

import java.util.List;

public interface IService {
    Joc incepeJoc(String alias, IObserver client);
    RezultatIncercareDTO faIncercare(Long idJoc, String pereche);
    void finalizeazaJoc();
    List<JocIncercariDTO> getJocSiIncercari(String alias);
    Configuratie modificaConfiguratie(Long idConfiguratie, String cuvinteNoi);
}
