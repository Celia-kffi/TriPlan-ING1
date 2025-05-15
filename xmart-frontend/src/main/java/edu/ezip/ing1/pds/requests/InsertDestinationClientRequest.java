package edu.ezip.ing1.pds.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.Destination;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;
import java.util.Map;

public class InsertDestinationClientRequest extends ClientRequest<Destination, String> {

    public InsertDestinationClientRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, Destination info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public String readResult(String body) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Map<String, Object> map = mapper.readValue(body, Map.class);
        return map.getOrDefault("id_destination", "ok").toString();
    }
}