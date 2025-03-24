package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "empreinteCarbone")
public class EmpreinteCarbone {

    private int idEmpreinte;
    private double empreinteKgCO2;

    public EmpreinteCarbone() {
    }

    public EmpreinteCarbone(int idEmpreinte, double empreinteKgCO2) {
        this.idEmpreinte = idEmpreinte;
        this.empreinteKgCO2 = empreinteKgCO2;
    }

    public int getIdEmpreinte() {
        return idEmpreinte;
    }

    @JsonProperty("id_empreinte")
    public void setIdEmpreinte(int idEmpreinte) {
        this.idEmpreinte = idEmpreinte;
    }

    public double getEmpreinteKgCO2() {
        return empreinteKgCO2;
    }

    @JsonProperty("empreinte_kgCO2")
    public void setEmpreinteKgCO2(double empreinteKgCO2) {
        this.empreinteKgCO2 = empreinteKgCO2;
    }

    @Override
    public String toString() {
        return "EmpreinteCarbone{" +
                "idEmpreinte=" + idEmpreinte +
                ", empreinteKgCO2=" + empreinteKgCO2 +
                '}';
    }
}
