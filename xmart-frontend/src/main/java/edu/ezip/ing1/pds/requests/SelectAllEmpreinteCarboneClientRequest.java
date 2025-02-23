package edu.ezip.ing1.pds.requests;
import edu.ezip.ing1.pds.business.dto.EmpreinteCarbone;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.EmpreintesCarbone;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectAllEmpreinteCarboneClientRequest extends ClientRequest<Object, EmpreintesCarbone> {

    public SelectAllEmpreinteCarboneClientRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, Object info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public EmpreintesCarbone readResult(String body) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final EmpreintesCarbone empreintesCarbone = mapper.readValue(body, EmpreintesCarbone.class);
        return empreintesCarbone;
    }

    // Méthode ajoutée pour récupérer les types de transport distincts
    public static List<String> getTypesDeTransport(EmpreintesCarbone empreintesCarbone) {
        List<String> typesDeTransport = new ArrayList<>();
        for (EmpreinteCarbone empreinte : empreintesCarbone.getEmpreintesCarbone()) {
            if (!typesDeTransport.contains(empreinte.getTypeDeTransport())) {
                typesDeTransport.add(empreinte.getTypeDeTransport());
            }
        }
        return typesDeTransport;
    }
}