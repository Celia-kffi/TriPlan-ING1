package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.LinkedHashSet;
import java.util.Set;

@JsonRootName("Hebergements")
public class Hebergements {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("hebergements")
    private Set<Hebergement> hebergements;

    public Hebergements() {
        this.hebergements = new LinkedHashSet<>();
    }

    public Set<Hebergement> getHebergements() {
        return hebergements;
    }

    public void setHebergements(Set<Hebergement> hebergements) {
        if (hebergements != null) {
            this.hebergements = hebergements;
        } else {
            this.hebergements = new LinkedHashSet<>();
        }
    }

    public Hebergements add(Hebergement hebergement) {
        if (hebergement != null) {
            hebergements.add(hebergement);
        }
        return this;
    }

    @Override
    public String toString() {
        return "Hebergements{" +
                "hebergements=" + hebergements +
                '}';
    }
}
