package org.example;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "litere")
public class Litere implements Serializable {
    @Id
    @Column(name = "litera", nullable = false, unique = true)
    private String litera;

    public Litere(String litera) {
        this.litera = litera;
    }

    public Litere() {}

    public String getLitera() {
        return litera;
    }

    public void setLitera(String litera) {
        this.litera = litera;
    }
}
