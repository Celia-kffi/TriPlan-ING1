package edu.ezip.ing1.pds.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.Client;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;
import java.util.Map;

public class DeleteClientsClientRequest extends ClientRequest<Client, String> {

    public DeleteClientsClientRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, Client info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public String readResult(String body) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Map<String, String> statusMap = mapper.readValue(body, Map.class);
        return statusMap.getOrDefault("status", "Statut inconnu");
    }
}