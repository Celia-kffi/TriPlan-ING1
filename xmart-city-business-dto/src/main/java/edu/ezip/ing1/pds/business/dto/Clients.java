package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.LinkedHashSet;
import java.util.Set;

@JsonRootName("Clients")
public class Clients {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("clients")
    private Set<Client> clients;


    public Clients() {
        this.clients = new LinkedHashSet<>();
    }


    public Set<Client> getClients() {
        return clients;
    }


    public void setClients(Set<Client> clients) {
        if (clients != null) {
            this.clients = clients;
        } else {
            this.clients = new LinkedHashSet<>();
        }
    }


    public Clients add(Client client) {
        if (client != null) {
            clients.add(client);
        }
        return this;
    }

    @Override
    public String toString() {
        return "Clients{" +
                "clients=" + clients +
                '}';
    }
}
