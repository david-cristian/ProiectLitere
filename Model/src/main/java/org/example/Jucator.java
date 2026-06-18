package org.example;

import jakarta.persistence.*;

import java.io.Serializable;

@jakarta.persistence.Entity
@Table(name = "jucatori")
public class Jucator extends Entity<Long> implements Serializable {

    @Column(name = "alias", nullable = false, unique = true)
    private String alias;

    public Jucator(String alias) {
        this.alias = alias;
    }

    public Jucator() {}

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }
}
