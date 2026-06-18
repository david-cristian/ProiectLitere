package org.example.dto;

import org.example.Incercare;
import org.example.Joc;

import java.io.Serializable;
import java.util.List;

public class JocIncercariDTO implements Serializable {
    private String alias;
    private Joc joc;
    private List<Incercare> incercari;

    public JocIncercariDTO(String alias, Joc joc, List<Incercare> incercari) {
        this.alias = alias;
        this.joc = joc;
        this.incercari = incercari;
    }

    public JocIncercariDTO() {}

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Joc getJoc() {
        return joc;
    }

    public void setJoc(Joc joc) {
        this.joc = joc;
    }

    public List<Incercare> getIncercari() {
        return incercari;
    }

    public void setIncercari(List<Incercare> incercari) {
        this.incercari = incercari;
    }
}
