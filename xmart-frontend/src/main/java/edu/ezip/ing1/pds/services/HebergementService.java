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
import edu.ezip.ing1.pds.business.dto.Hebergement;
import edu.ezip.ing1.pds.business.dto.Hebergements;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.requests.InsertHebergementClientRequest;
import edu.ezip.ing1.pds.requests.SelectAllHebergementClientRequest;

public class HebergementService {

    private final static String LoggingLabel = "FrontEnd - HebergementService";
    private final static Logger logger = LoggerFactory.getLogger(LoggingLabel);

    final String insertRequestOrder = "INSERT_HEBERGEMENT";
    final String selectRequestOrder = "SELECT_ALL_HEBERGEMENTS";
    final String deleteRequestOrder = "DELETE_HEBERGEMENT";
    final String updateRequestOrder = "UPDATE_HEBERGEMENT";

    private final NetworkConfig networkConfig;

    public HebergementService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public void insertHebergement(Hebergement hebergement) throws InterruptedException, IOException {
        processHebergement(hebergement, insertRequestOrder);
    }

    public void deleteHebergement(Hebergement hebergement) throws InterruptedException, IOException {
        processHebergement(hebergement, deleteRequestOrder);
    }

    public void updateHebergement(Hebergement hebergement) throws InterruptedException, IOException {
        processHebergement(hebergement, updateRequestOrder);
    }

    private void processHebergement(Hebergement hebergement, String requestOrder) throws InterruptedException, IOException {
        final Deque<ClientRequest> hebergementRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonifiedVoyage = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(hebergement);
        logger.trace("Voyage with its JSON face : {}", jsonifiedVoyage);

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(requestOrder);
        request.setRequestContent(jsonifiedVoyage);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);

        final InsertHebergementClientRequest voyageRequest = new InsertHebergementClientRequest(
                networkConfig, 0, request, hebergement, requestBytes);
        hebergementRequests.push(voyageRequest);

        while (!hebergementRequests.isEmpty()) {
            final ClientRequest processedRequest = hebergementRequests.pop();
            processedRequest.join();
            final Hebergement processedHebergement = (Hebergement) processedRequest.getInfo();
            logger.debug("Thread {} complete : {} {} --> {}",
                    processedRequest.getThreadName(),
                    processedHebergement.getType(), processedHebergement.getNomH(),processedHebergement.getIdHebergement(),processedHebergement.getPrixNuit(),
                    processedRequest.getResult());
        }
    }

    public Hebergements selectHebergement() throws InterruptedException, IOException {
        final Deque<ClientRequest> voyageRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(selectRequestOrder);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);
        LoggingUtils.logDataMultiLine(logger, Level.TRACE, requestBytes);

        final SelectAllHebergementClientRequest voyageRequest = new SelectAllHebergementClientRequest(
                networkConfig, 0, request, null, requestBytes);
        voyageRequests.push(voyageRequest);

        if (!voyageRequests.isEmpty()) {
            final ClientRequest joinedHebergementRequest = voyageRequests.pop();
            joinedHebergementRequest.join();
            logger.debug("Thread {} complete.", joinedHebergementRequest.getThreadName());
            return (Hebergements) joinedHebergementRequest.getResult();
        } else {
            logger.error("No hebergement found");
            return null;
        }
    }
}