package edu.ezip.ing1.pds.services;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

import edu.ezip.ing1.pds.business.dto.Destinations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.ezip.commons.LoggingUtils;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.requests.SelectAllDestinationsClientRequest;

public class DestinationService {

    private final static String LoggingLabel = "FrontEnd - DestinationService";
    private final static Logger logger = LoggerFactory.getLogger(LoggingLabel);

    private static final String SELECT_REQUEST_ORDER = "SELECT_ALL_DESTINATIONS";

    private final NetworkConfig networkConfig;

    public DestinationService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public Destinations selectDestinations() throws InterruptedException, IOException {
        final Deque<ClientRequest> requests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(SELECT_REQUEST_ORDER);

        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);

        logger.info("📤 Envoi de la requête {}", SELECT_REQUEST_ORDER);
        LoggingUtils.logDataMultiLine(logger, Level.DEBUG, requestBytes);

        final SelectAllDestinationsClientRequest clientRequest = new SelectAllDestinationsClientRequest(
                networkConfig, 0, request, null, requestBytes
        );
        requests.push(clientRequest);

        if (!requests.isEmpty()) {
            final ClientRequest joined = requests.pop();
            joined.join();

            logger.info("📥 Résultat brut reçu : {}", joined.getResult());

            Object result = joined.getResult();
            if (result instanceof Destinations) {
                return (Destinations) result;
            } else {
                logger.error("Type de réponse inattendu : {}", result != null ? result.getClass() : "null");
                return null;
            }
        } else {
            logger.error("Aucun résultat à traiter (pile vide)");
            return null;
        }
    }
}
