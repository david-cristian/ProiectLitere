package org.example.dto;

public class IncercareRequestDTO {
    private Long idJoc;
    private String pereche;

    public IncercareRequestDTO(Long idJoc, String pereche) {
        this.idJoc = idJoc;
        this.pereche = pereche;
    }

    public IncercareRequestDTO() {}

    public Long getIdJoc() {
        return idJoc;
    }

    public void setIdJoc(Long idJoc) {
        this.idJoc = idJoc;
    }

    public String getPereche() {
        return pereche;
    }

    public void setPereche(String pereche) {
        this.pereche = pereche;
    }
}
