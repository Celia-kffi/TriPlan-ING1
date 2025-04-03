package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.LinkedHashSet;
import java.util.Set;

@JsonRootName("voyages") // Nom racine JSON
public class Voyages {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("voyages") // Nom de la propriété JSON
    private Set<Voyage> voyages = new LinkedHashSet<>();

    public Set<Voyage> getVoyages() {
        return voyages;
    }

    public void setVoyages(Set<Voyage> voyages) {
        this.voyages = voyages;
    }

    public final Voyages add(final Voyage voyage) {
        voyages.add(voyage);
        return this;
    }

    @Override
    public String toString() {
        return "Voyages{" +
                "voyages=" + voyages +
                '}';
    }
}