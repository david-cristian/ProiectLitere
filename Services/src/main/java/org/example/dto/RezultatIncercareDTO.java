package org.example.dto;

import org.example.Status;

import java.io.Serializable;

public class RezultatIncercareDTO implements Serializable {
    private Status status;
    private String pereche;

    public RezultatIncercareDTO(Status status, String pereche) {
        this.status = status;
        this.pereche = pereche;
    }

    public RezultatIncercareDTO() {}

    public Status getStatus() {
        return status;
    }

    public String getPereche() {
        return pereche;
    }
}
