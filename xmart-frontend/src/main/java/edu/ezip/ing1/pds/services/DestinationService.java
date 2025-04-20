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

    final String selectRequestOrder = "SELECT_ALL_DESTINATIONS";

    private final NetworkConfig networkConfig;

    public DestinationService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public Destinations selectDestinations() throws InterruptedException, IOException {
        final Deque<ClientRequest> destinationRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(selectRequestOrder);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);
        LoggingUtils.logDataMultiLine(logger, Level.TRACE, requestBytes);

        final SelectAllDestinationsClientRequest destinationRequest = new SelectAllDestinationsClientRequest(
                networkConfig, 0, request, null, requestBytes);
        destinationRequests.push(destinationRequest);

        if (!destinationRequests.isEmpty()) {
            final ClientRequest joinedDestinationRequest = destinationRequests.pop();
            joinedDestinationRequest.join();
            logger.debug("Thread {} complete.", joinedDestinationRequest.getThreadName());
            return (Destinations) joinedDestinationRequest.getResult();
        } else {
            logger.error("No destinations found");
            return null;
        }
    }
}
