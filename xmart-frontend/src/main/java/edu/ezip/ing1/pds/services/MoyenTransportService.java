package edu.ezip.ing1.pds.services;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

import edu.ezip.ing1.pds.business.dto.MoyenTransport;
import edu.ezip.ing1.pds.business.dto.MoyenTransports;
import edu.ezip.ing1.pds.requests.InsertMoyenTransportRequest;
import edu.ezip.ing1.pds.requests.SelectAllMoyenTransportRequest;
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

public class MoyenTransportService {

    private final static String LoggingLabel = "FrontEnd - ClientService";
    private final static Logger logger = LoggerFactory.getLogger(LoggingLabel);

    final String insertRequestOrder = "INSERT_TRANSPORT";
    final String selectRequestOrder = "SELECT_ALL_TRANSPORTS";
    final String deleteRequestOrder = "DELETE_TRANSPORT";
    final String updateRequestOrder = "UPDATE_TRANSPORT";

    private final NetworkConfig networkConfig;

    public MoyenTransportService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public void insertTransport(MoyenTransport transport) throws InterruptedException, IOException {
        processTransport(transport, insertRequestOrder);
    }

    public void deleteTransport(MoyenTransport transport) throws InterruptedException, IOException {
        processTransport(transport, deleteRequestOrder);
    }

    public void updateTransport(MoyenTransport transport) throws InterruptedException, IOException {
        processTransport(transport, updateRequestOrder);
    }


    private void processTransport(MoyenTransport transport, String requestOrder) throws InterruptedException, IOException {
        final Deque<ClientRequest> clientRequests = new ArrayDeque<>();

        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonifiedClient = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(transport);
        logger.trace("Client with its JSON face : {}", jsonifiedClient);

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(requestOrder);
        request.setRequestContent(jsonifiedClient);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);

        final InsertMoyenTransportRequest transportRequest = new InsertMoyenTransportRequest(
                networkConfig, 0, request, transport, requestBytes);
        clientRequests.push(transportRequest);

        while (!clientRequests.isEmpty()) {
            final ClientRequest processedRequest = clientRequests.pop();
            processedRequest.join();
            final MoyenTransport processedClient = (MoyenTransport) processedRequest.getInfo();
            logger.debug("Thread {} complete : {} {} --> {}",
                    processedRequest.getThreadName(),
                    processedClient.getIdMoyenDestination(), processedClient.getTypeTransports(),
                    processedRequest.getResult());
        }
    }/*
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
    }*/

    public MoyenTransports selectTransport() throws InterruptedException, IOException {
        final Deque<ClientRequest> transportRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(selectRequestOrder);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);
        LoggingUtils.logDataMultiLine(logger, Level.TRACE, requestBytes);

        final SelectAllMoyenTransportRequest transportRequest = new SelectAllMoyenTransportRequest(
                networkConfig, 0, request, null, requestBytes);
        transportRequests.push(transportRequest);

        if (!transportRequests.isEmpty()) {
            final ClientRequest joinedtransportRequest = transportRequests.pop();
            joinedtransportRequest.join();
            logger.debug("Thread {} complete.", joinedtransportRequest.getThreadName());
            return (MoyenTransports) joinedtransportRequest.getResult();
        } else {
            logger.error("No clients found");
            return null;
        }
    }

}