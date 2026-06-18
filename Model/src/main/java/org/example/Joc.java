package org.example;


import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Table(name = "jocuri")
public class Joc extends Entity<Long> implements Serializable {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_jucator", nullable = false)
    private Jucator jucator;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_configuratie", nullable = false)
    private Configuratie configuratie;

    @Column(name = "puncte_jucator", nullable = false)
    private Integer puncteJucator;

    @Column(name = "nr_incercare", nullable = false)
    private Integer nrIncercare;

    @Column(name = "data", nullable = false)
    private LocalDateTime data;

    public Joc(Jucator jucator, Configuratie configuratie, Integer puncteJucator, Integer nrIncercare, LocalDateTime data) {
        this.jucator = jucator;
        this.configuratie = configuratie;
        this.puncteJucator = puncteJucator;
        this.nrIncercare = nrIncercare;
        this.data = data;
    }

    public Joc() {}

    public Jucator getJucator() {
        return jucator;
    }

    public void setJucator(Jucator jucator) {
        this.jucator = jucator;
    }

    public Configuratie getConfiguratie() {
        return configuratie;
    }

    public void setConfiguratie(Configuratie configuratie) {
        this.configuratie = configuratie;
    }

    public Integer getPuncteJucator() {
        return puncteJucator;
    }

    public void setPuncteJucator(Integer puncteJucator) {
        this.puncteJucator = puncteJucator;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Integer getNrIncercare() {
        return nrIncercare;
    }

    public void setNrIncercare(Integer nrIncercare) {
        this.nrIncercare = nrIncercare;
    }
}
