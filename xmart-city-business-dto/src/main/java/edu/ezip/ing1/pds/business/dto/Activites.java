package edu.ezip.ing1.pds.business.dto;

import java.util.ArrayList;
import java.util.List;

public class Activites {
    private List<Activite> activites;

    // Constructeur par défaut qui initialise la liste
    public Activites() {
        this.activites = new ArrayList<>();
    }

    // Constructeur utile si on a déjà la liste
    public Activites(List<Activite> activites) {
        this.activites = activites;
    }

    public List<Activite> getActivites() {
        return activites;
    }

    public void setActivites(List<Activite> activites) {
        this.activites = activites;
    }
}
