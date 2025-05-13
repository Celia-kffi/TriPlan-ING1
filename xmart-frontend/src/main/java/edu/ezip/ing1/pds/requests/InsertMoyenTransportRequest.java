package edu.ezip.ing1.pds.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.MoyenTransport;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;
import java.util.Map;

public class InsertMoyenTransportRequest extends ClientRequest<MoyenTransport, String> {

    public InsertMoyenTransportRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, MoyenTransport info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public String readResult(String body) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Map<String, Integer> TransportIdMap = mapper.readValue(body, Map.class);
        return TransportIdMap.get("transport_id").toString();
    }
}
