package edu.ezip.ing1.pds.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.Destinations;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;

public class SelectAllDestinationsClientRequest extends ClientRequest<Object, Destinations> {

    public SelectAllDestinationsClientRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, Object info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public Destinations readResult(String body) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Destinations destinations = mapper.readValue(body, Destinations.class);
        return destinations;
    }
}
