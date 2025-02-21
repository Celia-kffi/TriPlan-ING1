package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.LinkedHashSet;
import java.util.Set;

@JsonRootName("avis_clients")
public class AvisClients {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("avis_clients")
    private Set<AvisClient> avisClients;

    public AvisClients() {
        this.avisClients = new LinkedHashSet<>();
    }

    public Set<AvisClient> getAvisClients() {
        return avisClients;
    }

    public void setAvisClients(Set<AvisClient> avisClients) {
        this.avisClients = avisClients != null ? avisClients : new LinkedHashSet<>();
    }

    public AvisClients add(AvisClient avisClient) {
        if (avisClient != null) {
            avisClients.add(avisClient);
        }
        return this;
    }

    @Override
    public String toString() {
        return "AvisClients{" +
                "avisClients=" + avisClients +
                '}';
    }
}
