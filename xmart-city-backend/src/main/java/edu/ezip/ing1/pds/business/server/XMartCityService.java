package edu.ezip.ing1.pds.business.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.Client;
import edu.ezip.ing1.pds.business.dto.Clients;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class XMartCityService {

    private final static String LoggingLabel = "B u s i n e s s - S e r v e r";
    private final Logger logger = LoggerFactory.getLogger(LoggingLabel);

    private enum Queries {
        SELECT_ALL_CLIENTS("SELECT id_client, nom, prenom, age, nationalite FROM clients"),
        INSERT_CLIENT("INSERT INTO clients (nom, prenom, age, nationalite) VALUES (?, ?, ?, ?)"),
        DELETE_CLIENT("DELETE FROM clients WHERE id_client = ?");

        private final String query;

        Queries(final String query) {
            this.query = query;
        }
    }

    public static XMartCityService inst = null;

    public static XMartCityService getInstance() {
        if (inst == null) {
            inst = new XMartCityService();
        }
        return inst;
    }

    private XMartCityService() {
    }

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
            default:
                logger.warn("Requête inconnue : {}", request.getRequestOrder());
                break;
        }

        return response;
    }

    private Response selectAllClients(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(Queries.SELECT_ALL_CLIENTS.query);
        Clients clients = new Clients();

        while (res.next()) {
            Client client = new Client();
            client.setIdClient(res.getInt("id_client"));
            client.setNom(res.getString("nom"));
            client.setPrenom(res.getString("prenom"));
            client.setAge(res.getInt("age"));
            client.setNationalite(res.getString("nationalite"));
            clients.add(client);
        }

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(clients));
    }

    private Response insertClient(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Client client = objectMapper.readValue(request.getRequestBody(), Client.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_CLIENT.query, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, client.getNom());
        stmt.setString(2, client.getPrenom());
        stmt.setInt(3, client.getAge());
        stmt.setString(4, client.getNationalite());
        stmt.executeUpdate();

        final ResultSet res = stmt.getGeneratedKeys();
        if (res.next()) {
            client.setIdClient(res.getInt(1)); // Récupère l'ID généré
        }

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(client));
    }

    private Response deleteClient(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Client client = objectMapper.readValue(request.getRequestBody(), Client.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.DELETE_CLIENT.query);
        stmt.setInt(1, client.getIdClient());
        int affectedRows = stmt.executeUpdate();

        if (affectedRows == 0) {
            logger.warn("Aucun client supprimé avec ID {}", client.getIdClient());
            return new Response(request.getRequestId(), "{\"message\": \"Aucun client supprimé.\"}");
        }

        return new Response(request.getRequestId(), "{\"message\": \"Client supprimé avec succès.\"}");
    }
}
