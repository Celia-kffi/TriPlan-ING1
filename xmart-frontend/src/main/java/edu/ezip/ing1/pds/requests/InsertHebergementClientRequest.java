package edu.ezip.ing1.pds.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.Hebergement;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;
import java.util.Map;

public class InsertHebergementClientRequest extends ClientRequest<Hebergement, String> {

    public InsertHebergementClientRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, Hebergement info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public String readResult(String body) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Map<String, Integer> HebergementIdMap = mapper.readValue(body, Map.class);
        return HebergementIdMap.get("hebergement_id").toString();
    }
}