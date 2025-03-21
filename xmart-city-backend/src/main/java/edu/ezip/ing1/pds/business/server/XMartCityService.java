package edu.ezip.ing1.pds.business.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.ezip.ing1.pds.business.dto.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;

public class XMartCityService {

    private final static String LoggingLabel = "Business - Server";
    private final Logger logger = LoggerFactory.getLogger(LoggingLabel);

    private enum Queries {
        SELECT_ALL_AVIS("SELECT id_avis, note, date_avis, commentaires, id_client FROM avis_clients"),
        INSERT_AVIS("INSERT INTO avis_clients (note, date_avis, commentaires, id_client) VALUES (?, ?, ?, ?)"),
        SELECT_ALL_CLIENTS("SELECT id_client ,nom, prenom, age, nationalite, budget, id_paiement FROM clients "),
        INSERT_CLIENT("INSERT INTO clients (nom, prenom, age, nationalite, budget, id_paiement) VALUES (?, ?, ?, ?, ?, ?)"),
        DELETE_CLIENT("DELETE FROM clients WHERE id_client = ? "),
        UPDATE_CLIENT("UPDATE clients SET nom = ?, prenom = ?, age = ?, nationalite = ?, budget = ?, id_paiement = ? WHERE id_client = ?"),
        SELECT_ALL_EMPREINTES("SELECT id_empreinte, empreinte_kgCO2, type_de_transport, facteur_emission, distance FROM empreinte_carbone"),
        INSERT_EMPREINTE("INSERT INTO empreinte_carbone (empreinte_kgCO2, type_de_transport, facteur_emission, distance) VALUES (?, ?, ?, ?)"),
        DELETE_EMPREINTE("DELETE FROM empreinte_carbone WHERE id_empreinte = ?"),
        UPDATE_EMPREINTE("UPDATE empreinte_carbone SET empreinte_kgCO2 = ?, type_de_transport = ?, facteur_emission = ?, distance = ? WHERE id_empreinte = ?");

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
            case SELECT_ALL_AVIS:
                response = selectAllAvis(request, connection);
                break;
            case INSERT_AVIS:
                response = insertAvis(request, connection);
                break;
            case SELECT_ALL_CLIENTS:
                response=selectAllClients(request, connection);
                break;
            case INSERT_CLIENT:
                response=insertClient(request,connection);
                break;
            case DELETE_CLIENT:
                response=deleteClient(request,connection);
                break;
            case UPDATE_CLIENT:
                response=updateClient(request,connection);
                break;
            case SELECT_ALL_EMPREINTES:
                response = selectAllEmpreintes(request, connection);
                break;
            case INSERT_EMPREINTE:
                response = insertEmpreinte(request, connection);
                break;
            case DELETE_EMPREINTE:
                response = deleteEmpreinte(request, connection);
                break;
            case UPDATE_EMPREINTE:
                response = updateEmpreinte(request, connection);
                break;
            default:
                break;
        }
        return response;
    }

    private Response insertAvis(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final AvisClient avisClient = objectMapper.readValue(request.getRequestBody(), AvisClient.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_AVIS.query);
        stmt.setInt(1, avisClient.getNote());
        stmt.setString(2, avisClient.getDateAvis());
        stmt.setString(3, avisClient.getCommentaires());
        stmt.setInt(4, avisClient.getIdClient());
        stmt.executeUpdate();

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(avisClient));
    }

    private Response selectAllAvis(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(Queries.SELECT_ALL_AVIS.query);
        AvisClients avisClients = new AvisClients();
        while (res.next()) {
            AvisClient avisClient = new AvisClient();
            avisClient.setIdAvis(res.getInt("id_avis"));
            avisClient.setNote(res.getInt("note"));
            avisClient.setDateAvis(res.getString("date_avis"));
            avisClient.setCommentaires(res.getString("commentaires"));
            avisClient.setIdClient(res.getInt("id_client"));
            avisClients.add(avisClient);
        }
        return new Response(request.getRequestId(), objectMapper.writeValueAsString(avisClients));
    }

    private Response selectAllClients(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(Queries.SELECT_ALL_CLIENTS.query);
        Clients clients = new Clients();
        while (res.next()) {
            Client client = new Client();
            client.setIdClient(res.getInt(1));
            client.setNom(res.getString(2));
            client.setPrenom(res.getString(3));
            client.setAge(res.getInt(4));
            client.setNationalite(res.getString(5));
            client.setBudget(res.getDouble(6));
            client.setIdPaiement(res.getString(7));
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
        stmt.setDouble(5, client.getBudget());
        stmt.setString(6, client.getIdPaiement());
        stmt.executeUpdate();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            client.setIdClient(generatedKeys.getInt(1));
        }

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(client));
    }
    private Response deleteClient(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Client client = objectMapper.readValue(request.getRequestBody(), Client.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.DELETE_CLIENT.query);
        stmt.setInt(1, client.getIdClient());
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
        stmt.setString(1, client.getNom());
        stmt.setString(2, client.getPrenom());
        stmt.setInt(3, client.getAge());
        stmt.setString(4, client.getNationalite());
        stmt.setDouble(5, client.getBudget());
        stmt.setString(6, client.getIdPaiement());
        stmt.setInt(7, client.getIdClient());
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            return new Response(request.getRequestId(), "Client mis à jour avec succès.");
        } else {
            return new Response(request.getRequestId(), "Client introuvable ou aucune modification effectuée.");
        }
    }

    private Response insertEmpreinte(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final EmpreinteCarbone empreinte = objectMapper.readValue(request.getRequestBody(), EmpreinteCarbone.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_EMPREINTE.query);
        stmt.setDouble(1, empreinte.getEmpreinteKgCO2());
        stmt.setString(2, empreinte.getTypeDeTransport());
        stmt.setDouble(3, empreinte.getFacteurEmission());
        stmt.setDouble(4, empreinte.getDistance());
        stmt.executeUpdate();

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(empreinte));
    }

    private Response selectAllEmpreintes(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(Queries.SELECT_ALL_EMPREINTES.query);
        EmpreintesCarbone empreintes = new EmpreintesCarbone();
        while (res.next()) {
            EmpreinteCarbone empreinte = new EmpreinteCarbone();
            empreinte.setIdEmpreinte(res.getInt(1));
            empreinte.setEmpreinteKgCO2(res.getInt(2));
            empreinte.setTypeDeTransport(res.getString(3));
            empreinte.setFacteurEmission(res.getDouble(4));
            empreinte.setDistance(res.getDouble(5));
            empreintes.add(empreinte);
        }
        return new Response(request.getRequestId(), objectMapper.writeValueAsString(empreintes));
    }
    private Response deleteEmpreinte(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final EmpreinteCarbone empreinte = objectMapper.readValue(request.getRequestBody(), EmpreinteCarbone.class);

        try (PreparedStatement stmt = connection.prepareStatement(Queries.DELETE_EMPREINTE.query)) {
            stmt.setInt(1, empreinte.getIdEmpreinte());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return new Response(request.getRequestId(), "Aucune empreinte trouvée avec cet ID.");
            }
        }

        return new Response(request.getRequestId(), "Empreinte supprimée avec succès");
    }

    private Response updateEmpreinte(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final EmpreinteCarbone empreinte = objectMapper.readValue(request.getRequestBody(), EmpreinteCarbone.class);

        try (PreparedStatement stmt = connection.prepareStatement(Queries.UPDATE_EMPREINTE.query)) {
            stmt.setDouble(1, empreinte.getEmpreinteKgCO2());
            stmt.setString(2, empreinte.getTypeDeTransport());
            stmt.setDouble(3, empreinte.getFacteurEmission());
            stmt.setDouble(4, empreinte.getDistance());
            stmt.setInt(5, empreinte.getIdEmpreinte());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return new Response(request.getRequestId(), "Aucune empreinte mise à jour, ID introuvable.");
            }
        }

        return new Response(request.getRequestId(), "Empreinte mise à jour avec succès");
    }

}
