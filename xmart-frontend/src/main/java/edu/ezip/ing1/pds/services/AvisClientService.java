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

public class AvisClientService {

    private static final String LoggingLabel = "FrontEnd - AvisClientService";
    private static final Logger logger = LoggerFactory.getLogger(LoggingLabel);

    private static final String INSERT_REQUEST_ORDER = "INSERT_AVIS";
    private static final String SELECT_REQUEST_ORDER = "SELECT_ALL_AVIS";

    private final NetworkConfig networkConfig;

    public AvisClientService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public void insertAvis(AvisClient avisClient) throws InterruptedException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonifiedAvis = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(avisClient);
        logger.trace("Avis with its JSON face : {}", jsonifiedAvis);

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(INSERT_REQUEST_ORDER);
        request.setRequestContent(jsonifiedAvis);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);

        final InsertAvisClientRequest avisRequest = new InsertAvisClientRequest(
                networkConfig, 0, request, avisClient, requestBytes);

        avisRequest.join();
        logger.debug("Insertion complete : {} --> {}",
                avisRequest.getInfo(),
                avisRequest.getResult());
    }

    public AvisClients selectAvisClients() throws InterruptedException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(SELECT_REQUEST_ORDER);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);
        LoggingUtils.logDataMultiLine(logger, Level.TRACE, requestBytes);

        final SelectAllAvisClientsRequest avisRequest = new SelectAllAvisClientsRequest(
                networkConfig, 0, request, null, requestBytes);

        avisRequest.join();
        logger.debug("Selection complete : {}",
                avisRequest.getThreadName());

        return (AvisClients) avisRequest.getResult();
    }
}
