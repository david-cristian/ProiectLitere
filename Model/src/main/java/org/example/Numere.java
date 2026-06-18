package org.example;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "numere")
public class Numere implements Serializable {
    @Id
    @Column(name = "numar", nullable = false)
    private Integer numar;

    public Numere(Integer numar) {
        this.numar = numar;
    }

    public Numere() {}

    public Integer getNumar() {
        return numar;
    }

    public void setNumar(Integer numar) {
        this.numar = numar;
    }
}
