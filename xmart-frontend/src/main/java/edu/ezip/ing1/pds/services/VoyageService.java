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
import edu.ezip.ing1.pds.business.dto.Voyage;
import edu.ezip.ing1.pds.business.dto.Voyages;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.requests.InsertVoyagesClientRequest;
import edu.ezip.ing1.pds.requests.SelectAllVoyagesClientRequest;

public class VoyageService {

    private final static String LoggingLabel = "FrontEnd - VoyageService";
    private final static Logger logger = LoggerFactory.getLogger(LoggingLabel);

    final String insertRequestOrder = "INSERT_VOYAGE";
    final String selectRequestOrder = "SELECT_ALL_VOYAGES";
    final String deleteRequestOrder = "DELETE_VOYAGE";
    final String updateRequestOrder = "UPDATE_VOYAGE";

    private final NetworkConfig networkConfig;

    public VoyageService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public void insertVoyage(Voyage voyage) throws InterruptedException, IOException {
        processVoyage(voyage, insertRequestOrder);
    }

    public void deleteVoyage(Voyage voyage) throws InterruptedException, IOException {
        processVoyage(voyage, deleteRequestOrder);
    }

    public void updateVoyage(Voyage voyage) throws InterruptedException, IOException {
        processVoyage(voyage, updateRequestOrder);
    }

    private void processVoyage(Voyage voyage, String requestOrder) throws InterruptedException, IOException {
        final Deque<ClientRequest> voyageRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonifiedVoyage = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(voyage);
        logger.trace("Voyage with its JSON face : {}", jsonifiedVoyage);

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(requestOrder);
        request.setRequestContent(jsonifiedVoyage);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);

        final InsertVoyagesClientRequest voyageRequest = new InsertVoyagesClientRequest(
                networkConfig, 0, request, voyage, requestBytes);
        voyageRequests.push(voyageRequest);

        while (!voyageRequests.isEmpty()) {
            final ClientRequest processedRequest = voyageRequests.pop();
            processedRequest.join();
            final Voyage processedVoyage = (Voyage) processedRequest.getInfo();
            logger.debug("Thread {} complete : {} {} --> {}",
                    processedRequest.getThreadName(),
                    processedVoyage.getTypeVoyage(), processedVoyage.getMontant(),
                    processedRequest.getResult());
        }
    }

    public Voyages selectVoyages() throws InterruptedException, IOException {
        final Deque<ClientRequest> voyageRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(selectRequestOrder);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);
        LoggingUtils.logDataMultiLine(logger, Level.TRACE, requestBytes);

        final SelectAllVoyagesClientRequest voyageRequest = new SelectAllVoyagesClientRequest(
                networkConfig, 0, request, null, requestBytes);
        voyageRequests.push(voyageRequest);

        if (!voyageRequests.isEmpty()) {
            final ClientRequest joinedVoyageRequest = voyageRequests.pop();
            joinedVoyageRequest.join();
            logger.debug("Thread {} complete.", joinedVoyageRequest.getThreadName());
            return (Voyages) joinedVoyageRequest.getResult();
        } else {
            logger.error("No voyages found");
            return null;
        }
    }

}
