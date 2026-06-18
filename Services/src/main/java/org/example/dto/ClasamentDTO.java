package org.example.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ClasamentDTO implements Serializable {
    public String alias;
    public Integer nrPuncte;
    public LocalDateTime data;

    public ClasamentDTO(String alias, Integer nrPuncte, LocalDateTime data) {
        this.alias = alias;
        this.nrPuncte = nrPuncte;
        this.data = data;
    }

    public ClasamentDTO() {}

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getNrPuncte() {
        return nrPuncte;
    }

    public void setNrPuncte(Integer nrPuncte) {
        this.nrPuncte = nrPuncte;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}
