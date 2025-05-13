package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.LinkedHashSet;
import java.util.Set;

@JsonRootName("MoyenTransports")
public class MoyenTransports {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("moyenTransports")
    private Set<MoyenTransport> moyenTransports;

    public MoyenTransports() {
        this.moyenTransports = new LinkedHashSet<>();
    }

    public Set<MoyenTransport> getMoyenTransports() {
        return moyenTransports;
    }

    public void setMoyenTransports(Set<MoyenTransport> moyenTransports) {
        if (moyenTransports != null) {
            this.moyenTransports = moyenTransports;
        } else {
            this.moyenTransports = new LinkedHashSet<>(); // Initialisation par défaut si null
        }
    }

    public MoyenTransports add(MoyenTransport moyenTransport) {
        if (moyenTransport != null) {
            moyenTransports.add(moyenTransport);
        }
        return this;
    }

    @Override
    public String toString() {
        return "MoyenTransports{" +
                "moyenTransports=" + moyenTransports +
                '}';
    }
}
