package edu.ezip.ing1.pds.services;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

import edu.ezip.ing1.pds.requests.SelectClientByNameClientRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.ezip.commons.LoggingUtils;
import edu.ezip.ing1.pds.business.dto.Client;
import edu.ezip.ing1.pds.business.dto.Clients;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.requests.InsertClientsClientRequest;
import edu.ezip.ing1.pds.requests.SelectAllClientsClientRequest;

public class ClientService {

    private final static String LoggingLabel = "FrontEnd - ClientService";
    private final static Logger logger = LoggerFactory.getLogger(LoggingLabel);

    final String insertRequestOrder = "INSERT_CLIENT";
    final String selectRequestOrder = "SELECT_ALL_CLIENTS";
    final String deleteRequestOrder = "DELETE_CLIENT";
    final String updateRequestOrder = "UPDATE_CLIENT";

    final String selectbynameRequestOrder = "SELECT_CLIENT_BY_NAME";

    private final NetworkConfig networkConfig;

    public ClientService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public void insertClient(Client client) throws InterruptedException, IOException {
        processClient(client, insertRequestOrder);
    }

    public void deleteClient(Client client) throws InterruptedException, IOException {
        processClient(client, deleteRequestOrder);
    }

    public void updateClient(Client client) throws InterruptedException, IOException {
        processClient(client, updateRequestOrder);
    }

    public void selectClientByName(Client client) throws InterruptedException, IOException {
        processClient(client, selectbynameRequestOrder);
    }

    private void processClient(Client client, String requestOrder) throws InterruptedException, IOException {
        final Deque<ClientRequest> clientRequests = new ArrayDeque<>();

        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonifiedClient = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(client);
        logger.trace("Client with its JSON face : {}", jsonifiedClient);

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(requestOrder);
        request.setRequestContent(jsonifiedClient);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);

        final InsertClientsClientRequest clientRequest = new InsertClientsClientRequest(
                networkConfig, 0, request, client, requestBytes);
        clientRequests.push(clientRequest);

        while (!clientRequests.isEmpty()) {
            final ClientRequest processedRequest = clientRequests.pop();
            processedRequest.join();
            final Client processedClient = (Client) processedRequest.getInfo();
            logger.debug("Thread {} complete : {} {} --> {}",
                    processedRequest.getThreadName(),
                    processedClient.getNom(), processedClient.getPrenom(),
                    processedRequest.getResult());
        }
    }

    public Clients selectClients() throws InterruptedException, IOException {
        final Deque<ClientRequest> clientRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(selectRequestOrder);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);
        LoggingUtils.logDataMultiLine(logger, Level.TRACE, requestBytes);

        final SelectAllClientsClientRequest clientRequest = new SelectAllClientsClientRequest(
                networkConfig, 0, request, null, requestBytes);
        clientRequests.push(clientRequest);

        if (!clientRequests.isEmpty()) {
            final ClientRequest joinedClientRequest = clientRequests.pop();
            joinedClientRequest.join();
            logger.debug("Thread {} complete.", joinedClientRequest.getThreadName());
            return (Clients) joinedClientRequest.getResult();
        } else {
            logger.error("No clients found");
            return null;
        }
    }

    public Client rechercherClientParNom(String nom) throws InterruptedException, IOException {
        final Deque<ClientRequest> clientRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(selectbynameRequestOrder);
        request.setRequestContent("\""+ nom.toLowerCase() +"\"");
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);

        final SelectClientByNameClientRequest clientRequest = new SelectClientByNameClientRequest(
                networkConfig, 0, request, null, requestBytes);
        clientRequests.push(clientRequest);

        if (!clientRequests.isEmpty()) {
            final ClientRequest joinedClientRequest = clientRequests.pop();
            joinedClientRequest.join();
            logger.debug("Thread {} complete.", joinedClientRequest.getThreadName());

            
            Clients clients = (Clients) joinedClientRequest.getResult();

            if (clients.getClients().isEmpty()) {
                throw new IllegalArgumentException("Aucun client trouvé avec ce nom.");
            }

            return clients.getClients().iterator().next();
        } else {
            logger.error("Client not found");
            return null;
        }
    }

}
