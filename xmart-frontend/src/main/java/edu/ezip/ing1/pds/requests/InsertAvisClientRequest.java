package edu.ezip.ing1.pds.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.AvisClient;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;
import java.util.Map;

public class InsertAvisClientRequest extends ClientRequest<AvisClient, String> {

    public InsertAvisClientRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, AvisClient info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public String readResult(String body) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Map<String, Integer> avisIdMap = mapper.readValue(body, Map.class);
        if (!avisIdMap.containsKey("avis_id") || avisIdMap.get("avis_id") == null) {
            throw new IOException("Erreur : 'avis_id' est absent ou null dans la réponse JSON !");
        }

        return avisIdMap.get("avis_id").toString();
    }
}