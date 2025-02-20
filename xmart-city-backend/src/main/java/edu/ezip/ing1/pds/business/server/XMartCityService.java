package edu.ezip.ing1.pds.business.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ezip.ing1.pds.business.dto.Client;
import edu.ezip.ing1.pds.business.dto.Clients;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;

public class XMartCityService {

    private final static String LoggingLabel = "Business - Server";
    private final Logger logger = LoggerFactory.getLogger(LoggingLabel);

    private enum Queries {
        SELECT_ALL_CLIENTS("SELECT nom, prenom, age, nationalite, budget FROM clients "),
        INSERT_CLIENT("INSERT INTO clients (nom, prenom, age, nationalite, budget) VALUES (?, ?, ?, ?, ?)"),
        DELETE_CLIENT("DELETE FROM clients WHERE nom = ? AND prenom = ?"),
        UPDATE_CLIENT("UPDATE clients SET age = ?, nationalite = ?, budget = ? WHERE nom = ? AND prenom = ?");



        private final String query;

        private Queries(final String query) {
            this.query = query;
        }
    }

    public static XMartCityService inst = null;
    public static final XMartCityService getInstance() {
        if (inst == null) {
            inst = new XMartCityService();
        }
        return inst;
    }

    private XMartCityService() {}

    public final Response dispatch(final Request request, final Connection connection)
            throws InvocationTargetException, IllegalAccessException, SQLException, IOException {
        Response response = null;
        final Queries queryEnum = Enum.valueOf(Queries.class, request.getRequestOrder());
        switch (queryEnum) {
            case SELECT_ALL_CLIENTS:
                response = selectAllClients(request, connection);
                break;
            case INSERT_CLIENT:
                response = insertClient(request, connection);
                break;
            case DELETE_CLIENT:
                response = deleteClient(request, connection);
                break;
            case UPDATE_CLIENT:
                response = updateClient(request, connection);
                break;
            default:
                break;
        }
        return response;
    }

    private Response insertClient(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Client client = objectMapper.readValue(request.getRequestBody(), Client.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_CLIENT.query);
        stmt.setString(1, client.getNom());
        stmt.setString(2, client.getPrenom());
        stmt.setInt(3, client.getAge());
        stmt.setString(4, client.getNationalite());
        stmt.setDouble(5, client.getBudget());
        stmt.executeUpdate();

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(client));
    }

    private Response selectAllClients(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(Queries.SELECT_ALL_CLIENTS.query);
        Clients clients = new Clients();
        while (res.next()) {
            Client client = new Client();
            client.setNom(res.getString(1));
            client.setPrenom(res.getString(2));
            client.setAge(res.getInt(3));
            client.setNationalite(res.getString(4));
            client.setBudget(res.getDouble(5));
            clients.add(client);
        }
        return new Response(request.getRequestId(), objectMapper.writeValueAsString(clients));
    }

    private Response deleteClient(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Client client = objectMapper.readValue(request.getRequestBody(), Client.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.DELETE_CLIENT.query);
        stmt.setString(1, client.getNom());
        stmt.setString(2, client.getPrenom());
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            return new Response(request.getRequestId(), "Client supprimé avec succès.");
        } else {
            return new Response(request.getRequestId(), "Client introuvable.");
        }
    }

    private Response updateClient(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Client client = objectMapper.readValue(request.getRequestBody(), Client.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.UPDATE_CLIENT.query);
        stmt.setInt(1, client.getAge());
        stmt.setString(2, client.getNationalite());
        stmt.setDouble(3, client.getBudget());
        stmt.setString(4, client.getNom());
        stmt.setString(5, client.getPrenom());
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            return new Response(request.getRequestId(), "Client mis à jour avec succès.");
        } else {
            return new Response(request.getRequestId(), "Client introuvable ou aucune modification effectuée.");
        }
    }
}
