package edu.ezip.ing1.pds.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.AvisClients;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;

public class SelectAllAvisClientsRequest extends ClientRequest<Object, AvisClients> {

    public SelectAllAvisClientsRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, Object info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public AvisClients readResult(String body) throws IOException {
        if (body == null || body.isEmpty()) {
            throw new IOException("La réponse de SELECT_ALL_AVIS est vide ou nulle");
        }

        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(body, AvisClients.class);
    }
}
