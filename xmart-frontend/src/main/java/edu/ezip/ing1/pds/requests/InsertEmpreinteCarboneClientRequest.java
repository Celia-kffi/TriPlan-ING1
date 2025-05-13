package edu.ezip.ing1.pds.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.EmpreinteCarbone;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import javax.print.DocFlavor;
import java.io.IOException;
import java.util.Map;

public class InsertEmpreinteCarboneClientRequest extends ClientRequest<EmpreinteCarbone, String> {

    public InsertEmpreinteCarboneClientRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, EmpreinteCarbone info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public String readResult(String body) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Map<String, Integer>  empreinteIdMap = mapper.readValue(body, Map.class);
        return empreinteIdMap.get("empreinte_id").toString();
    }
}