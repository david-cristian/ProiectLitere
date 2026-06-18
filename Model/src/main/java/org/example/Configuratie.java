package org.example;
import jakarta.persistence.*;

import java.io.Serializable;

@jakarta.persistence.Entity
@Table(name = "configuratii")
public class Configuratie extends Entity<Long> implements Serializable {
    @Column(name = "multimi", nullable = false)
    private String multimi;

    public Configuratie(String multimi) {
        this.multimi = multimi;
    }

    public Configuratie() {}

    public String getMultimi() {
        return multimi;
    }

    public void setMultimi(String multimi) {
        this.multimi = multimi;
    }
}
