package edu.ezip.ing1.pds.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.Activites;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;

public class SelectActivitesByDestinationClientRequest extends ClientRequest<String, Activites> {

    public SelectActivitesByDestinationClientRequest(NetworkConfig networkConfig, int myBirthDate, Request request, String info, byte[] bytes)

            throws IOException {
                super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public Activites readResult(String body) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(body, Activites.class);
    }
}
