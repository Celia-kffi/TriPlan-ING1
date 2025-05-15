package edu.ezip.ing1.pds.services;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.ezip.commons.LoggingUtils;
import edu.ezip.ing1.pds.business.dto.AvisClient;
import edu.ezip.ing1.pds.business.dto.AvisClients;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.requests.InsertAvisClientRequest;
import edu.ezip.ing1.pds.requests.SelectAllAvisClientsRequest;
import edu.ezip.ing1.pds.requests.UpdateAvisClientRequest;
import edu.ezip.ing1.pds.requests.DeleteAvisClientRequest;
import edu.ezip.ing1.pds.requests.FindClientIdByNomPrenomRequest;

public class AvisClientService {

    private final static String LoggingLabel = "FrontEnd - AvisClientService";
    private final static Logger logger = LoggerFactory.getLogger(LoggingLabel);

    final String insertRequestOrder = "INSERT_AVIS";
    final String selectRequestOrder = "SELECT_ALL_AVIS";
    final String deleteRequestOrder = "DELETE_AVIS";
    final String updateRequestOrder = "UPDATE_AVIS";
    final String findClientIdRequestOrder = "FIND_CLIENT_ID_BY_NOM_PRENOM";

    private final NetworkConfig networkConfig;

    public AvisClientService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public void insertAvis(AvisClient avisClient) throws InterruptedException, IOException {
        processAvisClient(avisClient, insertRequestOrder);
    }

    public void deleteAvis(AvisClient avisClient) throws InterruptedException, IOException {
        processAvisClient(avisClient, deleteRequestOrder);
    }

    public void updateAvis(AvisClient avisClient) throws InterruptedException, IOException {
        processAvisClient(avisClient, updateRequestOrder);
    }

    private void processAvisClient(AvisClient avisClient, String requestOrder) throws InterruptedException, IOException {
        final Deque<ClientRequest> clientRequests = new ArrayDeque<>();

        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonifiedAvis = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(avisClient);
        logger.trace("AvisClient JSON : {}", jsonifiedAvis);

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(requestOrder);
        request.setRequestContent(jsonifiedAvis);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);

        final InsertAvisClientRequest avisRequest = new InsertAvisClientRequest(
                networkConfig, 0, request, avisClient, requestBytes);
        clientRequests.push(avisRequest);

        while (!clientRequests.isEmpty()) {
            final ClientRequest processedRequest = clientRequests.pop();
            processedRequest.join();
            final AvisClient processedAvis = (AvisClient) processedRequest.getInfo();
            logger.debug("Thread {} terminé : Avis ID {} --> {}",
                    processedRequest.getThreadName(),
                    processedAvis.getIdAvis(),
                    processedRequest.getResult());
        }
    }

    public AvisClients selectAvisClients() throws InterruptedException, IOException {
        final Deque<ClientRequest> clientRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(selectRequestOrder);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);
        LoggingUtils.logDataMultiLine(logger, Level.TRACE, requestBytes);

        final SelectAllAvisClientsRequest avisRequest = new SelectAllAvisClientsRequest(
                networkConfig, 0, request, null, requestBytes);
        clientRequests.push(avisRequest);

        if (!clientRequests.isEmpty()) {
            final ClientRequest joinedAvisRequest = clientRequests.pop();
            joinedAvisRequest.join();
            logger.debug("Thread {} terminé.", joinedAvisRequest.getThreadName());
            return (AvisClients) joinedAvisRequest.getResult();
        } else {
            logger.error("Aucun avis trouvé");
            return null;
        }
    }

    public int findClientIdByNomPrenom(String nom, String prenom) throws InterruptedException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final String[] nomPrenom = { nom, prenom };
        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(findClientIdRequestOrder);
        request.setRequestContent(objectMapper.writeValueAsString(nomPrenom));
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);

        final FindClientIdByNomPrenomRequest clientRequest = new FindClientIdByNomPrenomRequest(
                networkConfig, 0, request, null, requestBytes);
        clientRequest.join();

        return Integer.parseInt(clientRequest.getResult().toString());
    }
}
