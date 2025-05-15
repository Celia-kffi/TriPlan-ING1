package edu.ezip.ing1.pds.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.Activites;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;

public class SelectAllActivitesClientRequest extends ClientRequest<Object, Activites> {

    public SelectAllActivitesClientRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, Object info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public Activites readResult(String body) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Activites activites = mapper.readValue(body, Activites.class);
        return activites;
    }
}
