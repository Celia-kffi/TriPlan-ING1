package edu.ezip.ing1.pds.services;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.ezip.commons.LoggingUtils;
import edu.ezip.ing1.pds.business.dto.Client;
import edu.ezip.ing1.pds.business.dto.EmpreinteCarbone;
import edu.ezip.ing1.pds.business.dto.EmpreintesCarbone;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.requests.InsertEmpreinteCarboneClientRequest;
import edu.ezip.ing1.pds.requests.SelectAllEmpreinteCarboneClientRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class EmpreinteCarboneService {
    private final static String LoggingLabel = "FrontEnd - EmpreinteCarboneService";
    private final static Logger logger = LoggerFactory.getLogger(LoggingLabel);

    final String insertRequestOrder = "INSERT_EMPREINTE";
    final String selectRequestOrder = "SELECT_ALL_EMPREINTES";
    final String deleteRequestOrder = "DELETE_EMPREINTE";
    final String updateRequestOrder = "UPDATE_EMPREINTE";

    private final NetworkConfig networkConfig;

    public EmpreinteCarboneService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public void insertEmpreinte(EmpreinteCarbone empreinteCarbone) throws InterruptedException, IOException, SQLException {
        processEmpreinteCarbone(empreinteCarbone, insertRequestOrder);
    }
    public void deleteEmpreinte(EmpreinteCarbone empreinteCarbone) throws InterruptedException, IOException, SQLException  {
        processEmpreinteCarbone( empreinteCarbone, deleteRequestOrder);
    }

    public void updateEmpreinte(EmpreinteCarbone empreinteCarbone) throws InterruptedException, IOException, SQLException  {
        processEmpreinteCarbone(empreinteCarbone, updateRequestOrder);
    }
    private void processEmpreinteCarbone(EmpreinteCarbone empreinteCarbone, String requestOrder)
            throws InterruptedException, IOException, SQLException {

        final Deque<ClientRequest> empreinteRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonifiedEmpreinteCarbone = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(empreinteCarbone);
        logger.trace("EmpreinteCarbone with its JSON face : {}", jsonifiedEmpreinteCarbone);

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(requestOrder);
        request.setRequestContent(jsonifiedEmpreinteCarbone);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);

        final InsertEmpreinteCarboneClientRequest empreinteRequest = new InsertEmpreinteCarboneClientRequest(
                networkConfig, 0, request, empreinteCarbone, requestBytes);
        empreinteRequests.push(empreinteRequest);

        while (!empreinteRequests.isEmpty()) {
            final ClientRequest processedRequest = empreinteRequests.pop();
            processedRequest.join();
            final EmpreinteCarbone processedEmpreinteCarbone = (EmpreinteCarbone) processedRequest.getInfo();
            logger.debug("Thread {} complete : {} --> {}",
                    processedRequest.getThreadName(),
                    processedEmpreinteCarbone.getEmpreinteKgCO2(),
                    processedRequest.getResult());
        }
    }


    public EmpreintesCarbone selectEmpreinte() throws InterruptedException, IOException {
        final Deque<ClientRequest> empreinteRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(selectRequestOrder);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);
        LoggingUtils.logDataMultiLine(logger, Level.TRACE, requestBytes);

        final SelectAllEmpreinteCarboneClientRequest empreinteRequest = new SelectAllEmpreinteCarboneClientRequest(
                networkConfig, 0, request, null, requestBytes);
        empreinteRequests.push(empreinteRequest);

        if (!empreinteRequests.isEmpty()) {
            final ClientRequest joinedEmpreinteRequest = empreinteRequests.pop();
            joinedEmpreinteRequest.join();
            logger.debug("Thread {} complete.", joinedEmpreinteRequest.getThreadName());
            return (EmpreintesCarbone) joinedEmpreinteRequest.getResult();
        } else {
            logger.error("No empreintes found");
            return null;
        }
    }
}
