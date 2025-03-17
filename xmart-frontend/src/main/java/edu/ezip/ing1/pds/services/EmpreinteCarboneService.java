package edu.ezip.ing1.pds.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.ezip.ing1.pds.business.dto.EmpreinteCarbone;
import edu.ezip.ing1.pds.business.dto.EmpreintesCarbone;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.requests.InsertEmpreinteCarboneClientRequest;
import edu.ezip.ing1.pds.requests.SelectAllEmpreinteCarboneClientRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;
import java.util.Iterator;

public class EmpreinteCarboneService {
    private final static Logger logger = LoggerFactory.getLogger(EmpreinteCarboneService.class);
    private final NetworkConfig networkConfig;
    int myBirthDate = 2000;
    public EmpreinteCarboneService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public void insertEmpreinteCarbone(EmpreinteCarbone empreinte) throws InterruptedException, IOException {
        final Deque<ClientRequest> clientRequests = new ArrayDeque<>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

        String jsonifiedEmpreinte = objectMapper.writeValueAsString(empreinte);
        logger.trace("EmpreinteCarbone JSON : {}", jsonifiedEmpreinte);

        String requestId = UUID.randomUUID().toString();
        Request request = new Request(requestId, "INSERT_EMPREINTE_CARBONE");
        request.setRequestContent(jsonifiedEmpreinte);

        byte[] requestBytes = objectMapper.writeValueAsBytes(request);

        InsertEmpreinteCarboneClientRequest clientRequest = new InsertEmpreinteCarboneClientRequest(
                networkConfig, myBirthDate, request, empreinte, requestBytes);
        clientRequests.push(clientRequest);

        while (!clientRequests.isEmpty()) {
            ClientRequest cr = clientRequests.pop();
            cr.join();
            logger.debug("Insertion complète : {}", empreinte);
        }
    }

    public EmpreinteCarbone selectEmpreinteById(int id) throws InterruptedException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

        String requestId = UUID.randomUUID().toString();
        Request request = new Request(requestId, "SELECT_EMPREINTE_BY_ID");
        request.setRequestContent(String.valueOf(id));

        byte[] requestBytes = objectMapper.writeValueAsBytes(request);
        int myBirthDate = 2000; // Remplace avec la vraie valeur

        SelectAllEmpreinteCarboneClientRequest clientRequest = new SelectAllEmpreinteCarboneClientRequest(
                networkConfig, myBirthDate, request, null, requestBytes);

        clientRequest.join();
        EmpreintesCarbone empreintes = clientRequest.getResult();
        if (empreintes == null || empreintes.getEmpreintesCarbone().isEmpty()) {
            return null;
        }

        Iterator<EmpreinteCarbone> iterator = empreintes.getEmpreintesCarbone().iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

}
