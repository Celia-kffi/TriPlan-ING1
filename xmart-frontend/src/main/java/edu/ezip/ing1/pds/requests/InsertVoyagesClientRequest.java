package edu.ezip.ing1.pds.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.Voyage;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;
import java.util.Map;

public class InsertVoyagesClientRequest extends ClientRequest<Voyage, String> {

    public InsertVoyagesClientRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, Voyage info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public String readResult(String body) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Map<String, Integer> voyageIdMap = mapper.readValue(body, Map.class);
        return voyageIdMap.get("voyage_id").toString();
    }
}
