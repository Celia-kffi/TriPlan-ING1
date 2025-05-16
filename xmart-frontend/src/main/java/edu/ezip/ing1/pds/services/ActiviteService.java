package edu.ezip.ing1.pds.services;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.ezip.commons.LoggingUtils;
import edu.ezip.ing1.pds.business.dto.Activites;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.requests.SelectAllActivitesClientRequest;
import edu.ezip.ing1.pds.requests.SelectActivitesByDestinationClientRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class ActiviteService {

    private static final String LoggingLabel = "FrontEnd - ActiviteService";
    private static final Logger logger = LoggerFactory.getLogger(LoggingLabel);

    private static final String SELECT_ALL = "SELECT_ALL_ACTIVITES";
    private static final String SELECT_BY_DESTINATION = "SELECT_ACTIVITES_BY_DESTINATION";

    private final NetworkConfig networkConfig;

    public ActiviteService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public Activites selectActivites() throws InterruptedException, IOException {
        return sendActiviteRequest(SELECT_ALL, null);
    }

    public Activites getActivitesByDestination(String idDestination) throws InterruptedException, IOException {
        return sendActiviteRequest(SELECT_BY_DESTINATION, idDestination);
    }

    private Activites sendActiviteRequest(String requestOrder, String requestBody) throws InterruptedException, IOException {
        final Deque<ClientRequest> activiteRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(requestOrder);

        // Convertir String en JsonNode si nécessaire
        if (requestBody != null) {
            request.setRequestBody(objectMapper.readTree("\"" + requestBody + "\"")); // ex: "D004"
        }

        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);
        LoggingUtils.logDataMultiLine(logger, Level.TRACE, requestBytes);

        ClientRequest activiteRequest;
        if (SELECT_ALL.equals(requestOrder)) {
            activiteRequest = new SelectAllActivitesClientRequest(networkConfig, 0, request, null, requestBytes);
        } else {
            activiteRequest = new SelectActivitesByDestinationClientRequest(networkConfig, 0, request, null, requestBytes);
        }

        activiteRequests.push(activiteRequest);
        activiteRequest.join();

        logger.debug("Thread {} complete.", activiteRequest.getThreadName());

        Object result = activiteRequest.getResult();
        if (result instanceof Activites) {
            return (Activites) result;
        } else {
            logger.error("Erreur lors de la récupération des activités. Résultat inattendu : {}", result);
            return null;
        }
    }

}
