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
import java.util.ArrayList;


public class XMartCityService {

    private final static String LoggingLabel = "Business - Server";
    private final Logger logger = LoggerFactory.getLogger(LoggingLabel);

    private enum Queries {
        SELECT_ALL_AVIS("SELECT id_avis, note, date_avis, commentaires, id_client FROM avis_clients"),
        INSERT_AVIS("INSERT INTO avis_clients (note, date_avis, commentaires, id_client) VALUES (?, ?, ?, ?)"),

        DELETE_AVIS("DELETE FROM avis_clients WHERE id_avis = ?"),
        UPDATE_AVIS("UPDATE avis_clients SET note = ?, date_avis = ?, commentaires = ? WHERE id_avis = ?"),

        SELECT_ALL_CLIENTS("SELECT id_client ,nom, prenom, age, nationalite, budget, id_paiement FROM clients "),
        INSERT_CLIENT("INSERT INTO clients (nom, prenom, age, nationalite, budget, id_paiement) VALUES (?, ?, ?, ?, ?, ?)"),
        DELETE_CLIENT("DELETE FROM clients WHERE id_client = ? "),
        UPDATE_CLIENT("UPDATE clients SET nom = ?, prenom = ?, age = ?, nationalite = ?, budget = ?, id_paiement = ? WHERE id_client = ?"),

        SELECT_ALL_EMPREINTES("SELECT id_empreinte, empreinte_kgCO2 FROM empreinte_carbone"),
        INSERT_EMPREINTE("INSERT INTO empreinte_carbone (empreinte_kgCO2) VALUES (?)"),
        DELETE_EMPREINTE("DELETE FROM empreinte_carbone WHERE id_empreinte = ?"),

        SELECT_ALL_TRANSPORTS("SELECT id_moyen_destination, type_transports, facteur_emission FROM moyen_transports"),
        INSERT_TRANSPORT("INSERT INTO moyen_transports (id_moyen_destination, type_transports, facteur_emission) VALUES (?, ?, ?)"),
        DELETE_TRANSPORT("DELETE FROM moyen_transports WHERE id_moyen_destination = ?"),
        UPDATE_TRANSPORT("UPDATE moyen_transports SET type_transports = ?, facteur_emission = ? WHERE id_moyen_destination = ?");

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

    private XMartCityService() {
    }

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
            case DELETE_AVIS:
                response = deleteAvis(request, connection);
                break;
            case UPDATE_AVIS:
                response = updateAvis(request, connection);
                break;
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
            case SELECT_ALL_EMPREINTES:
                response = SelectAllEmpreintes(request, connection);
                break;
            case INSERT_EMPREINTE:
                response = InsertEmpreinte(request, connection);
                break;
            case SELECT_ALL_TRANSPORTS:
                response = SelectAllTransports(request, connection);
                break;
            case INSERT_TRANSPORT:
                response = InsertTransport(request, connection);
                break;
            case DELETE_EMPREINTE:
                response = DeleteTransport(request, connection);
                break;
            case UPDATE_TRANSPORT:
                response = UpdateTransport(request, connection);
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

    private Response deleteAvis(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final AvisClient avisClient = objectMapper.readValue(request.getRequestBody(), AvisClient.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.DELETE_AVIS.query);
        stmt.setInt(1, avisClient.getIdAvis());
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            return new Response(request.getRequestId(), "Avis supprimé avec succès.");
        } else {
            return new Response(request.getRequestId(), "Avis introuvable.");
        }
    }


    private Response updateAvis(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final AvisClient avisClient = objectMapper.readValue(request.getRequestBody(), AvisClient.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.UPDATE_AVIS.query);
        stmt.setInt(1, avisClient.getNote());
        stmt.setString(2, avisClient.getDateAvis());
        stmt.setString(3, avisClient.getCommentaires());
        stmt.setInt(4, avisClient.getIdAvis());
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            return new Response(request.getRequestId(), "Avis mis à jour avec succès.");
        } else {
            return new Response(request.getRequestId(), "Avis introuvable ou aucune modification effectuée.");
        }
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
    private Response InsertEmpreinte(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final EmpreinteCarbone empreinteCarbone = objectMapper.readValue(request.getRequestBody(), EmpreinteCarbone.class);

        if (connection.isClosed()) {
            logger.error("Connexion fermée !");
            return new Response(request.getRequestId(), "{\"error\": \"Database connection closed\"}");
        }

        logger.debug("Tentative d'insertion : {}", empreinteCarbone);

        try (PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_EMPREINTE.query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, empreinteCarbone.getEmpreinteKgCO2());
            // Autres champs (type_de_transport, facteur_emission, distance) ne sont plus nécessaires ici

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        empreinteCarbone.setIdEmpreinte(generatedKeys.getInt(1));
                    }
                }
            }

            logger.info("Empreinte carbone insérée avec succès : ID {}", empreinteCarbone.getIdEmpreinte());
            return new Response(request.getRequestId(), objectMapper.writeValueAsString(empreinteCarbone));
        } catch (SQLException e) {
            logger.error("Erreur SQL lors de l'insertion : ", e);
            return new Response(request.getRequestId(), "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response SelectAllEmpreintes(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(Queries.SELECT_ALL_EMPREINTES.query);
        EmpreintesCarbone empreintesCarbone = new EmpreintesCarbone();
        while (res.next()) {
            EmpreinteCarbone empreinteCarbone = new EmpreinteCarbone();
            empreinteCarbone.setIdEmpreinte(res.getInt("id_empreinte"));
            empreinteCarbone.setEmpreinteKgCO2(res.getDouble("empreinte_kgCO2"));
            // Le type de transport et la distance ne sont plus gérés dans cette classe

            empreintesCarbone.add(empreinteCarbone);
        }
        logger.info("Récupération de toutes les empreintes carbone : {} éléments trouvés", empreintesCarbone.getEmpreintesCarbone().size());
        return new Response(request.getRequestId(), objectMapper.writeValueAsString(empreintesCarbone));
    }

    private Response SelectAllTransports(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(Queries.SELECT_ALL_TRANSPORTS.query);
        MoyenTransports transports = new MoyenTransports();
        while (res.next()) {
            MoyenTransport transport = new MoyenTransport();
            transport.setIdMoyenDestination(res.getString(1));
            transport.setTypeTransports(res.getString(2));
            transport.setFacteurEmission(res.getDouble(3));
            transports.add(transport);
        }
        return new Response(request.getRequestId(), objectMapper.writeValueAsString(transports));
    }
    private Response InsertTransport(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final MoyenTransport transport = objectMapper.readValue(request.getRequestBody(), MoyenTransport
                .class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_TRANSPORT.query, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, transport.getIdMoyenDestination());
        stmt.setString(2, transport.getTypeTransports());
        stmt.setDouble(3, transport.getFacteurEmission());

        stmt.executeUpdate();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            transport.setIdMoyenDestination(generatedKeys.getString(1));
        }

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(transport));
    }
    private Response DeleteTransport(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final MoyenTransport transport = objectMapper.readValue(request.getRequestBody(), MoyenTransport
                .class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.DELETE_TRANSPORT.query);
        stmt.setString(1, transport.getIdMoyenDestination());
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            return new Response(request.getRequestId(), "Moyen de transport supprimé avec succès.");
        } else {
            return new Response(request.getRequestId(), "Moyen de transport introuvable.");
        }
    }
    private Response UpdateTransport(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final MoyenTransport transport = objectMapper.readValue(request.getRequestBody(), MoyenTransport.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.UPDATE_TRANSPORT.query);
        stmt.setString(1, transport.getIdMoyenDestination());
        stmt.setString(2, transport.getTypeTransports());
        stmt.setDouble(3, transport.getFacteurEmission());
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            return new Response(request.getRequestId(), "Moyen transport mis à jour avec succès.");
        } else {
            return new Response(request.getRequestId(), "Moyen transport introuvable ou aucune modification effectuée.");
        }
    }
}



