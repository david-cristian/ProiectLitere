package org.example;

import jakarta.persistence.*;

import java.io.Serializable;

@jakarta.persistence.Entity
@Table(name = "incercari")
public class Incercare extends Entity<Long> implements Serializable {
    @ManyToOne
    @JoinColumn(name = "id_joc", nullable = false)
    private Joc joc;

    @Column(name = "cine")
    private String cine;

    @Column(name = "incercare")
    private String incercare;

    public Incercare() {}

    public Incercare(Joc joc, String cine, String incercare) {
        this.joc = joc;
        this.cine = cine;
        this.incercare = incercare;
    }

    public Joc getJoc() {
        return joc;
    }

    public void setJoc(Joc joc) {
        this.joc = joc;
    }

    public String getIncercare() {
        return incercare;
    }

    public void setIncercare(String incercare) {
        this.incercare = incercare;
    }

    public String getCine() {
        return cine;
    }

    public void setCine(String cine) {
        this.cine = cine;
    }
}
