package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.LinkedHashSet;
import java.util.Set;

@JsonRootName("destinations") // Nom racine JSON
public class Destinations {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("destinations") // Nom de la propriété JSON
    private Set<Destination> destinations = new LinkedHashSet<>();

    public Set<Destination> getDestinations() {
        return destinations;
    }

    public void setDestinations(Set<Destination> destinations) {
        this.destinations = destinations;
    }

    public final Destinations add(final Destination destination) {
        destinations.add(destination);
        return this;
    }

    @Override
    public String toString() {
        return "Destinations{" +
                "destinations=" + destinations +
                '}';
    }
}
