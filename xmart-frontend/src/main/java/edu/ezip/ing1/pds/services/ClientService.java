package edu.ezip.ing1.pds.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.ezip.commons.LoggingUtils;
import edu.ezip.ing1.pds.business.dto.Client;
import edu.ezip.ing1.pds.business.dto.Clients;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.requests.DeleteClientsClientRequest;
import edu.ezip.ing1.pds.requests.InsertClientsClientRequest;
import edu.ezip.ing1.pds.requests.SelectAllClientsClientRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

public class ClientService {

    private final static String LoggingLabel = "FrontEnd - ClientService";
    private final static Logger logger = LoggerFactory.getLogger(LoggingLabel);


    final String insertRequestOrder = "INSERT_CLIENT";
    final String selectRequestOrder = "SELECT_ALL_CLIENTS";

    private final NetworkConfig networkConfig;

    public ClientService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public void insertClients(Client client) throws InterruptedException, IOException {

            if (client == null) {
                logger.error("Le client est null !");
                return;
            }

            final ObjectMapper objectMapper = new ObjectMapper();
            final String jsonifiedClient = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(client);
            logger.trace("Client avec son JSON : {}", jsonifiedClient);

            final String requestId = UUID.randomUUID().toString();
            final Request request = new Request(requestId, insertRequestOrder);
            request.setRequestContent(jsonifiedClient);

            final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);
            final InsertClientsClientRequest clientRequest = new InsertClientsClientRequest(
                    networkConfig, 0, request, client, requestBytes);

            clientRequest.join();
            logger.debug("Client ajouté : {} {} ({} ans, {}) --> {}",
                    client.getNom(), client.getPrenom(), client.getAge(), client.getNationalite(), clientRequest.getResult());
        }


    public Clients selectClients() throws InterruptedException, IOException {
        final Deque<ClientRequest> clientRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();
        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request(requestId, "SELECT_ALL_CLIENTS");

        // Ajoutez ceci pour éviter les requêtes vides
        request.setRequestContent("{}"); // Corps de requête vide mais non null

        final byte[] requestBytes = objectMapper.writeValueAsBytes(request);
        final SelectAllClientsClientRequest clientRequest = new SelectAllClientsClientRequest(
                networkConfig, 0, request, null, requestBytes);

        clientRequests.push(clientRequest);
        clientRequest.join();

        // Retournez un objet Clients vide si null
        return clientRequest.getResult() != null ?
                (Clients) clientRequest.getResult() : new Clients();
    }

    public void deleteClient(Client client) throws IOException, InterruptedException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonifiedClient = objectMapper.writeValueAsString(client);

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request(requestId, "DELETE_CLIENT");
        request.setRequestContent(jsonifiedClient);

        final byte[] requestBytes = objectMapper.writeValueAsBytes(request);
        final DeleteClientsClientRequest clientRequest = new DeleteClientsClientRequest(
                networkConfig, 0, request, client, requestBytes);

        clientRequest.join();
        logger.info("Client ID {} supprimé : {}", client.getIdClient(), clientRequest.getResult());
    }
}