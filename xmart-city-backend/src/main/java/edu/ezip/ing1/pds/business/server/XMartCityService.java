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
import java.util.List;


public class XMartCityService {

    private final static String LoggingLabel = "Business - Server";
    private final Logger logger = LoggerFactory.getLogger(LoggingLabel);

    private enum Queries {
        SELECT_ALL_AVIS("SELECT a.id_avis, a.note, a.date_avis, a.commentaires,  a.id_client,  c.nom AS nom_client,  c.prenom AS prenom_cl FROM  avis_clien JOIN  clients c ON a.id_client = c.id_client"),
        INSERT_AVIS("INSERT INTO avis_clients (note, date_avis, commentaires, id_client) VALUES (?, ?, ?, ?)"),
        DELETE_AVIS("DELETE FROM avis_clients WHERE id_avis = ?"),
        UPDATE_AVIS("UPDATE avis_clients SET note = ?, date_avis = ?, commentaires = ? WHERE id_avis = ?"),

        SELECT_ALL_CLIENTS("SELECT id_client ,nom, prenom, age, nationalite, budget, id_paiement FROM clients "),
        INSERT_CLIENT("INSERT INTO clients (nom, prenom, age, nationalite, budget, id_paiement) VALUES (?, ?, ?, ?, ?, ?)"),
        DELETE_CLIENT("DELETE FROM clients WHERE id_client = ? "),
        UPDATE_CLIENT("UPDATE clients SET nom = ?, prenom = ?, age = ?, nationalite = ?, budget = ?, id_paiement = ? WHERE id_client = ?"),

        SELECT_CLIENT_BY_NAME("SELECT id_client, nom, prenom, age, nationalite, budget, id_paiement FROM clients WHERE LOWER(nom) = LOWER(?)"),

        SELECT_ALL_DESTINATIONS("SELECT id_destination, pays, ville, climat, prix_par_jour FROM destinations "),


        SELECT_ALL_VOYAGES("SELECT id_voyage, montant, type_voyage, date_depart, date_retour, id_client FROM voyages"),
        INSERT_VOYAGE("INSERT INTO voyages (montant, type_voyage, date_depart, date_retour, id_client) VALUES (?, ?, ?, ?, ?)"),
        DELETE_VOYAGE("DELETE FROM voyages WHERE id_voyage = ?"),
        UPDATE_VOYAGE("UPDATE voyages SET montant= ?,type_voyage=?, date_depart=?, date_retour=?, id_client=? WHERE id_voyage = ? "),

        SELECT_ALL_EMPREINTES("SELECT id_empreinte, empreinte_kgCO2 FROM empreinte_carbone"),
        INSERT_EMPREINTE("INSERT INTO empreinte_carbone (empreinte_kgCO2) VALUES (?)"),
        DELETE_EMPREINTE("DELETE FROM empreinte_carbone WHERE id_empreinte = ?"),
        UPDATE_EMPREINTE("UPDATE empreinte_carbone SET id_empreinte= ?,empreinte_kgC02=? WHERE id_empreinte = ? "),

        INSERT_DESTINATION("INSERT INTO destinations (id_destination, pays, ville, climat, prix_par_jour) VALUES (?, ?, ?, ?, ?)"),
        UPDATE_DESTINATION("UPDATE destinations SET pays = ?, ville = ?, climat = ?, prix_par_jour = ? WHERE id_destination = ?"),
        DELETE_DESTINATION("DELETE FROM destinations WHERE id_destination = ?"),

        SELECT_ALL_ACTIVITES("SELECT id_activite, nom, description, prix, image_path, id_destination FROM activite"),
        SELECT_ACTIVITES_BY_DESTINATION("SELECT id_activite, nom, description, prix, image_path, id_destination FROM activite WHERE id_destination = ?"),
        INSERT_ACTIVITE("INSERT INTO activite (nom, description, prix, image_path, id_destination) VALUES (?, ?, ?, ?, ?)"),
        UPDATE_ACTIVITE("UPDATE activite SET nom = ?, description = ?, prix = ?, image_path = ?, id_destination = ? WHERE id_activite = ?"),
        DELETE_ACTIVITE("DELETE FROM activite WHERE id_activite = ?"),

        FIND_CLIENT_ID_BY_NOM_PRENOM("SELECT id_client FROM clients WHERE LOWER(nom) = LOWER(?) AND LOWER(prenom) = LOWER(?)"),



        SELECT_ALL_TRANSPORTS("SELECT id_moyen_destination, type_transports, facteur_emission FROM moyen_transports"),
        INSERT_TRANSPORT("INSERT INTO moyen_transports (id_moyen_destination, type_transports, facteur_emission) VALUES (?, ?, ?)"),
        DELETE_TRANSPORT("DELETE FROM moyen_transports WHERE id_moyen_destination = ?"),
        UPDATE_TRANSPORT("UPDATE moyen_transports SET type_transports = ?, facteur_emission = ? WHERE id_moyen_destination = ?"),


        SELECT_ALL_HEBERGEMENTS("SELECT id_hebergement, prix_nuit, nom_h, type , image, emission_par_nuit FROM hebergement"),
        INSERT_HEBERGEMENT("INSERT INTO hebergement (id_hebergement, prix_nuit, nom_h, type, image ,emission_par_nuit) VALUES (?, ?, ?, ?,?)"),
        DELETE_HEBERGEMENT("DELETE FROM hebergement WHERE id_hebergement = ?"),
        UPDATE_HEBERGEMENT("UPDATE hebergement SET prix_nuit = ?, nom_h = ?, type = ?, image=? , emission_par_nuit=? WHERE id_hebergement = ?");

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
            case SELECT_CLIENT_BY_NAME:
                response = selectClientByName(request, connection);
                break;

            case SELECT_ALL_VOYAGES:
                response=selectAllVoyages(request,connection);
                break;
            case INSERT_VOYAGE:
                response=insertVoyage(request,connection);
                break;
            case DELETE_VOYAGE:
                response=deleteVoyage(request,connection);
                break;
            case UPDATE_VOYAGE:
                response=updateVoyage(request,connection);
                break;

            case SELECT_ALL_DESTINATIONS:
                response=selectAllDestinations(request,connection);
                break;

            case SELECT_ALL_EMPREINTES:
                response = SelectAllEmpreintes(request, connection);
                break;
            case INSERT_EMPREINTE:
                response = InsertEmpreinte(request, connection);
                break;

            case UPDATE_DESTINATION:
                response = updateDestination(request, connection);
                break;
            case DELETE_DESTINATION:
                response = deleteDestination(request, connection);
                break;
            case SELECT_ALL_ACTIVITES:
                response = selectAllActivites(request, connection);
                break;
            case SELECT_ACTIVITES_BY_DESTINATION:
                response = selectActivitesByDestination(request, connection);
                break;
            case INSERT_ACTIVITE:
                response = insertActivite(request, connection);
                break;
            case UPDATE_ACTIVITE:
                response = updateActivite(request, connection);
                break;
            case DELETE_ACTIVITE:
                response = deleteActivite(request, connection);
                break;

            case SELECT_ALL_TRANSPORTS:
                response = SelectAllTransports(request, connection);
                break;
            case INSERT_TRANSPORT:
                response = InsertTransport(request, connection);
                break;
            case UPDATE_TRANSPORT:
                response = UpdateTransport(request, connection);
                break;
            case DELETE_EMPREINTE:
                response = DeleteTransport(request, connection);
                break;
            case SELECT_ALL_HEBERGEMENTS:
                response = SelectAllHebergements(request, connection);
                break;
            case INSERT_HEBERGEMENT:
                response = InsertHebergement(request, connection);
                break;
            case DELETE_HEBERGEMENT:
                response = DeleteHebergement(request, connection);
                break;
            case UPDATE_HEBERGEMENT:
                response = UpdateHebergement(request, connection);
                break;
            case FIND_CLIENT_ID_BY_NOM_PRENOM:
                response =findClientIdByNomPrenom(request, connection);
            default:
                break;
        }
        return response;
    }
    private Response findClientIdByNomPrenom(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();

        // On désérialise le body JSON ["Nom", "Prenom"]
        final String[] nomPrenom = objectMapper.readValue(request.getRequestBody(), String[].class);
        final String nom = nomPrenom[0];
        final String prenom = nomPrenom[1];

        final PreparedStatement stmt = connection.prepareStatement(
                "SELECT id_client FROM clients WHERE LOWER(nom) = LOWER(?) AND LOWER(prenom) = LOWER(?)"
        );
        stmt.setString(1, nom);
        stmt.setString(2, prenom);

        final ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int idClient = rs.getInt("id_client");
            return new Response(request.getRequestId(), String.valueOf(idClient));
        } else {
            throw new SQLException("Client non trouvé avec nom: " + nom + " et prénom: " + prenom);
        }
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

        // Optionnel : récupérer le nom/prénom du client correspondant si nécessaire
        final PreparedStatement getClientInfoStmt = connection.prepareStatement(
                "SELECT nom, prenom FROM clients WHERE id_client = ?"
        );
        getClientInfoStmt.setInt(1, avisClient.getIdClient());

        try (ResultSet rs = getClientInfoStmt.executeQuery()) {
            if (rs.next()) {
                avisClient.setNomClient(rs.getString("nom"));
                avisClient.setPrenomClient(rs.getString("prenom"));
            }
        }

        // Retourne l'avis inséré, avec nom/prénom ajoutés
        return new Response(request.getRequestId(), objectMapper.writeValueAsString(avisClient));
    }

    private Response selectAllAvis(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Statement stmt = connection.createStatement();

        final ResultSet res = stmt.executeQuery(
                "SELECT a.id_avis, a.note, a.date_avis, a.commentaires, a.id_client, c.nom AS nom_client, c.prenom AS prenom_client " +
                        "FROM avis_clients a JOIN clients c ON a.id_client = c.id_client"
        );

        AvisClients avisClients = new AvisClients();
        while (res.next()) {
            AvisClient avisClient = new AvisClient();
            avisClient.setIdAvis(res.getInt("id_avis"));
            avisClient.setNote(res.getInt("note"));
            avisClient.setDateAvis(res.getString("date_avis"));
            avisClient.setCommentaires(res.getString("commentaires"));
            avisClient.setIdClient(res.getInt("id_client"));
            avisClient.setNomClient(res.getString("nom_client"));       // 💡 alias SQL !
            avisClient.setPrenomClient(res.getString("prenom_client")); // 💡 alias SQL !

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


        int idClient = -1;

        try {
            final PreparedStatement getClientStmt = connection.prepareStatement(
                    "SELECT id_client FROM clients WHERE LOWER(nom) = LOWER(?) AND LOWER(prenom) = LOWER(?)"
            );
            getClientStmt.setString(1, avisClient.getNomClient());
            getClientStmt.setString(2, avisClient.getPrenomClient());

            ResultSet rs = getClientStmt.executeQuery();
            if (rs.next()) {
                idClient = rs.getInt("id_client");
            } else {
                return new Response(request.getRequestId(), "Client introuvable : " + avisClient.getNomClient() + " " + avisClient.getPrenomClient());
            }
        } catch (SQLException e) {
            return new Response(request.getRequestId(), "Erreur lors de la récupération du client : " + e.getMessage());
        }


        final PreparedStatement stmt = connection.prepareStatement(Queries.UPDATE_AVIS.query);
        stmt.setInt(1, avisClient.getNote());
        stmt.setString(2, avisClient.getDateAvis());
        stmt.setString(3, avisClient.getCommentaires());
        stmt.setInt(4, idClient);
        stmt.setInt(5, avisClient.getIdAvis());

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

    private Response selectClientByName(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        //Client client = objectMapper.readValue(request.getRequestBody(), Client.class);

        String name = objectMapper.readValue(request.getRequestBody(), String.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.SELECT_CLIENT_BY_NAME.query);
        stmt.setString(1, name);

        final ResultSet res = stmt.executeQuery();
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

    private Response selectAllDestinations(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(Queries.SELECT_ALL_DESTINATIONS.query);
        Destinations destinations = new Destinations();

        while (res.next()) {
            Destination destination = new Destination();
            destination.setIdDestination(res.getString(1));
            destination.setPays(res.getString(2));
            destination.setVille(res.getString(3));
            destination.setClimat(res.getString(4));
            destination.setPrixParJour(res.getDouble(5));
            destinations.add(destination);
        }

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(destinations));
    }





    private Response selectAllVoyages(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(Queries.SELECT_ALL_VOYAGES.query);
        Voyages voyages = new Voyages();
        while (res.next()) {
            Voyage voyage = new Voyage();
            voyage.setIdVoyage(res.getInt(1));
            voyage.setMontant(res.getDouble(2));
            voyage.setTypeVoyage(res.getString(3));
            voyage.setDateDepart(res.getString(4));
            voyage.setDateRetour(res.getString(5));
            voyage.setIdClient(res.getInt(6));
            voyages.add(voyage);
        }
        return new Response(request.getRequestId(), objectMapper.writeValueAsString(voyages));
    }

    private Response insertVoyage(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Voyage voyage = objectMapper.readValue(request.getRequestBody(), Voyage.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_VOYAGE.query, Statement.RETURN_GENERATED_KEYS);
        stmt.setDouble(1, voyage.getMontant());
        stmt.setString(2, voyage.getTypeVoyage());
        stmt.setString(3, voyage.getDateDepart());
        stmt.setString(4, voyage.getDateRetour());
        stmt.setInt(5, voyage.getIdClient());
        stmt.executeUpdate();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            voyage.setIdVoyage(generatedKeys.getInt(1));
        }

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(voyage));
    }

    private Response deleteVoyage(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Voyage voyage = objectMapper.readValue(request.getRequestBody(), Voyage.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.DELETE_VOYAGE.query);
        stmt.setInt(1, voyage.getIdVoyage());
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            return new Response(request.getRequestId(), "Voyage supprimé avec succès.");
        } else {
            return new Response(request.getRequestId(), "Voyage introuvable.");
        }
    }

    private Response updateVoyage(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Voyage voyage = objectMapper.readValue(request.getRequestBody(), Voyage.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.UPDATE_VOYAGE.query);
        stmt.setDouble(1, voyage.getMontant());
        stmt.setString(2, voyage.getTypeVoyage());
        stmt.setString(3, voyage.getDateDepart());
        stmt.setString(4, voyage.getDateRetour());
        stmt.setInt(5, voyage.getIdClient());
        stmt.setInt(6, voyage.getIdVoyage());
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            return new Response(request.getRequestId(), "Voyage mis à jour avec succès.");
        } else {
            return new Response(request.getRequestId(), "Voyage introuvable ou aucune modification effectuée.");
        }
    }



    private Response InsertEmpreinte(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final EmpreinteCarbone EmpreinteCarbone = objectMapper.readValue(request.getRequestBody(), EmpreinteCarbone.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_EMPREINTE.query, Statement.RETURN_GENERATED_KEYS);
        stmt.setDouble(2, EmpreinteCarbone.getEmpreinteKgCO2());

        stmt.executeUpdate();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            EmpreinteCarbone.setIdEmpreinte(generatedKeys.getInt(1));
        }

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(EmpreinteCarbone));
    }

    private Response SelectAllEmpreintes(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final Statement stmt = connection.createStatement();
        final ObjectMapper objectMapper = new ObjectMapper();
        final ResultSet res = stmt.executeQuery(Queries.SELECT_ALL_EMPREINTES.query);
        EmpreintesCarbone empreintes = new EmpreintesCarbone();
        while (res.next()) {
            EmpreinteCarbone empreinte = new EmpreinteCarbone();
            empreinte.setIdEmpreinte(res.getInt(1));
            empreinte.setEmpreinteKgCO2(res.getDouble(2));
            empreintes.add(empreinte);
        }
        return new Response(request.getRequestId(),objectMapper.writeValueAsString(empreintes));
    }

    private Response updateEmpreinte(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final EmpreinteCarbone empreinte = objectMapper.readValue(request.getRequestBody(), EmpreinteCarbone.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.UPDATE_EMPREINTE.query);
        stmt.setInt(1, empreinte.getIdEmpreinte());
        stmt.setDouble(2, empreinte.getEmpreinteKgCO2());

        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            return new Response(request.getRequestId(), "Voyage mis à jour avec succès.");
        } else {
            return new Response(request.getRequestId(), "Voyage introuvable ou aucune modification effectuée.");
        }
    }


    private Response insertDestination(Request request, Connection connection) throws SQLException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Destination d = mapper.readValue(request.getRequestBody(), Destination.class);
        PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_DESTINATION.query);
        stmt.setString(1, d.getIdDestination());
        stmt.setString(2, d.getPays());
        stmt.setString(3, d.getVille());
        stmt.setString(4, d.getClimat());
        stmt.setDouble(5, d.getPrixParJour());
        stmt.executeUpdate();
        return new Response(request.getRequestId(), mapper.writeValueAsString(d));
    }

    private Response updateDestination(Request request, Connection connection) throws SQLException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Destination d = mapper.readValue(request.getRequestBody(), Destination.class);
        PreparedStatement stmt = connection.prepareStatement(Queries.UPDATE_DESTINATION.query);
        stmt.setString(1, d.getPays());
        stmt.setString(2, d.getVille());
        stmt.setString(3, d.getClimat());
        stmt.setDouble(4, d.getPrixParJour());
        stmt.setString(5, d.getIdDestination());
        int rows = stmt.executeUpdate();
        return rows > 0
                ? new Response(request.getRequestId(), "Destination mise à jour avec succès.")
                : new Response(request.getRequestId(), "Destination introuvable ou aucune modification effectuée.");
    }

    private Response deleteDestination(Request request, Connection connection) throws SQLException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Destination d = mapper.readValue(request.getRequestBody(), Destination.class);
        PreparedStatement stmt = connection.prepareStatement(Queries.DELETE_DESTINATION.query);
        stmt.setString(1, d.getIdDestination());
        int rows = stmt.executeUpdate();
        return rows > 0
                ? new Response(request.getRequestId(), "Destination supprimée avec succès.")
                : new Response(request.getRequestId(), "Destination introuvable.");
    }

    private Response selectAllActivites(Request request, Connection connection) throws SQLException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(Queries.SELECT_ALL_ACTIVITES.query);

        Activites activites = new Activites(); // ✅ maintenant activites.getActivites() n'est plus null

        while (rs.next()) {
            Activite a = new Activite();
            a.setIdActivite(rs.getInt(1));
            a.setNom(rs.getString(2));
            a.setDescription(rs.getString(3));
            a.setPrix(rs.getDouble(4));
            a.setImagePath(rs.getString(5));
            a.setIdDestination(rs.getString(6));

            activites.getActivites().add(a); // ✅ plus de NullPointerException ici
        }

        return new Response(request.getRequestId(), mapper.writeValueAsString(activites));
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
    private Response SelectAllHebergements(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(Queries.SELECT_ALL_HEBERGEMENTS.query);
        Hebergements hebergements = new Hebergements();
        while (res.next()) {
            Hebergement hebergement = new Hebergement();
            hebergement.setIdHebergement(res.getInt(1));
            hebergement.setPrixNuit(res.getInt(2));
            hebergement.setNomH(res.getString(3));
            hebergement.setType(res.getString(4));
            hebergement.setImage(res.getString(5));
            hebergement.setEmissionParNuit(res.getFloat(6));
            hebergements.add(hebergement);
        }
        return new Response(request.getRequestId(), objectMapper.writeValueAsString(hebergements));
    }

    private Response InsertHebergement(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Hebergement hebergement = objectMapper.readValue(request.getRequestBody(), Hebergement.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_HEBERGEMENT.query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, hebergement.getIdHebergement());
        stmt.setInt(2, hebergement.getPrixNuit());
        stmt.setString(3, hebergement.getNomH());
        stmt.setString(4, hebergement.getType());
        stmt.setString(5, hebergement.getImage());
        stmt.setFloat(6, hebergement.getPrixNuit());
        stmt.executeUpdate();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            hebergement.setIdHebergement(generatedKeys.getInt(1));
        }

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(hebergement));
    }

    private Response DeleteHebergement(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Hebergement hebergement = objectMapper.readValue(request.getRequestBody(), Hebergement.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.DELETE_HEBERGEMENT.query);
        stmt.setInt(1, hebergement.getIdHebergement());
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            return new Response(request.getRequestId(), "Hebergement supprimé avec succès.");
        } else {
            return new Response(request.getRequestId(), "Hebergement introuvable.");
        }
    }

    private Response UpdateHebergement(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Hebergement hebergement = objectMapper.readValue(request.getRequestBody(), Hebergement.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.UPDATE_HEBERGEMENT.query);
        stmt.setInt(1, hebergement.getIdHebergement());
        stmt.setInt(2, hebergement.getPrixNuit());
        stmt.setString(3, hebergement.getNomH());
        stmt.setString(4, hebergement.getType());
        stmt.setString(5, hebergement.getImage());
        stmt.setFloat(6, hebergement.getPrixNuit());
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            return new Response(request.getRequestId(), "Hebergement mis à jour avec succès.");
        } else {
            return new Response(request.getRequestId(), "Hebergement introuvable ou aucune modification effectuée.");
        }
    }





    private Response selectActivitesByDestination(Request request, Connection connection) throws SQLException, JsonProcessingException {
        // Récupère l'ID depuis le body JSON
        String idDestination = request.getRequestBody().replace("\"", "");


        ObjectMapper mapper = new ObjectMapper();
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT id_activite, nom, description, prix, image_path, id_destination FROM activite WHERE id_destination = ?"
        );
        stmt.setString(1, idDestination);
        ResultSet rs = stmt.executeQuery();

        Activites activites = new Activites(); // Initialise avec liste vide

        while (rs.next()) {
            Activite a = new Activite();
            a.setIdActivite(rs.getInt("id_activite"));
            a.setNom(rs.getString("nom"));
            a.setDescription(rs.getString("description"));
            a.setPrix(rs.getDouble("prix"));
            a.setImagePath(rs.getString("image_path"));
            a.setIdDestination(rs.getString("id_destination"));
            activites.getActivites().add(a);
        }
        System.out.println("Nombre d'activités récupérées pour " + idDestination + " : " + activites.getActivites().size());

        return new Response(request.getRequestId(), mapper.writeValueAsString(activites));
    }


    private Response insertActivite(Request request, Connection connection) throws SQLException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Activite a = mapper.readValue(request.getRequestBody(), Activite.class);
        PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_ACTIVITE.query, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, a.getNom());
        stmt.setString(2, a.getDescription());
        stmt.setDouble(3, a.getPrix());
        stmt.setString(4, a.getImagePath());
        stmt.setString(5, a.getIdDestination());
        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            a.setIdActivite(keys.getInt(1));
        }
        return new Response(request.getRequestId(), mapper.writeValueAsString(a));
    }

    private Response updateActivite(Request request, Connection connection) throws SQLException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Activite a = mapper.readValue(request.getRequestBody(), Activite.class);
        PreparedStatement stmt = connection.prepareStatement(Queries.UPDATE_ACTIVITE.query);
        stmt.setString(1, a.getNom());
        stmt.setString(2, a.getDescription());
        stmt.setDouble(3, a.getPrix());
        stmt.setString(4, a.getImagePath());
        stmt.setString(5, a.getIdDestination());
        stmt.setInt(6, a.getIdActivite());
        int rows = stmt.executeUpdate();
        return rows > 0
                ? new Response(request.getRequestId(), "Activité mise à jour avec succès.")
                : new Response(request.getRequestId(), "Activité introuvable ou aucune modification effectuée.");
    }

    private Response deleteActivite(Request request, Connection connection) throws SQLException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Activite a = mapper.readValue(request.getRequestBody(), Activite.class);
        PreparedStatement stmt = connection.prepareStatement(Queries.DELETE_ACTIVITE.query);
        stmt.setInt(1, a.getIdActivite());
        int rows = stmt.executeUpdate();
        return rows > 0
                ? new Response(request.getRequestId(), "Activité supprimée avec succès.")
                : new Response(request.getRequestId(), "Activité introuvable.");
    }
}


