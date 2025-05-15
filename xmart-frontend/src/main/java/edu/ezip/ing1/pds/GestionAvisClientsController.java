package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.AvisClient;
import edu.ezip.ing1.pds.business.dto.AvisClients;
import edu.ezip.ing1.pds.services.AvisClientService;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class GestionAvisClientsController {

    @FXML
    private TableView<AvisClient> tableAvis;
    @FXML
    private TableColumn<AvisClient, Integer> colIdAvis;
    @FXML
    private TableColumn<AvisClient, Integer> colNote;
    @FXML
    private TableColumn<AvisClient, String> colDate;
    @FXML
    private TableColumn<AvisClient, String> colCommentaire;
    @FXML
    private TableColumn<AvisClient, String> colNomClient;
    @FXML
    private TableColumn<AvisClient, String> colPrenomClient;

    @FXML
    private ComboBox<String> noteComboBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField txtCommentaire;
    @FXML
    private TextField txtNomClient;
    @FXML
    private TextField txtPrenomClient;

    private AvisClientService avisClientService;
    private AvisClient selectedAvis;

    public GestionAvisClientsController() {
        try {
            final String networkConfigFile = "network.yaml";
            final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, networkConfigFile);
            avisClientService = new AvisClientService(networkConfig);
        } catch (Exception e) {
            System.err.println("Erreur réseau (chargement ignoré dans le constructeur) : " + e.getMessage());
            avisClientService = null;
        }
    }

    @FXML
    private void initialize() {
        // Init ComboBox
        noteComboBox.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5"));

        colIdAvis.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdAvis()).asObject());
        colNote.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNote()).asObject());
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateAvis()));
        colCommentaire.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCommentaires()));

        colNomClient.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getNomClient() != null ? cell.getValue().getNomClient() : ""));
        colPrenomClient.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getPrenomClient() != null ? cell.getValue().getPrenomClient() : ""));
        tableAvis.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedAvis = newSelection;
                remplirFormulaire(newSelection);
            }
        });

        actualiserListe();
    }

    private void remplirFormulaire(AvisClient avis) {
        noteComboBox.setValue(String.valueOf(avis.getNote()));
        if (avis.getDateAvis() != null && !avis.getDateAvis().isEmpty()) {
            datePicker.setValue(LocalDate.parse(avis.getDateAvis()));
        } else {
            datePicker.setValue(null);
        }
        txtCommentaire.setText(avis.getCommentaires());
        txtNomClient.setText(avis.getNomClient());
        txtPrenomClient.setText(avis.getPrenomClient());
    }

    private void viderFormulaire() {
        noteComboBox.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        txtCommentaire.clear();
        txtNomClient.clear();
        txtPrenomClient.clear();
    }

    @FXML
    private void ajouterAvis() {
        try {
            // Vérification des champs obligatoires
            if (noteComboBox.getValue() == null || datePicker.getValue() == null ||
                    txtCommentaire.getText().isBlank() || txtNomClient.getText().isBlank() || txtPrenomClient.getText().isBlank()) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants", null, "Veuillez remplir tous les champs.");
                return;
            }

            // Récupération des valeurs
            int note = Integer.parseInt(noteComboBox.getValue());
            String date = datePicker.getValue().toString();
            String commentaire = txtCommentaire.getText().trim();
            String nomClient = txtNomClient.getText().trim();
            String prenomClient = txtPrenomClient.getText().trim();

            System.out.println("Insertion d'un avis pour : " + nomClient + " " + prenomClient);

            // Recherche de l'id du client
            int idClient;
            try {
                idClient = avisClientService.findClientIdByNomPrenom(nomClient, prenomClient);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Client introuvable",
                        null, "Aucun client trouvé avec le nom " + nomClient + " et prénom " + prenomClient);
                return;

                // 💡 Si tu veux créer un nouveau client automatiquement, décommente ceci :
            /*
            Client newClient = new Client(nomClient, prenomClient, 0, "FR", 0.0, "AUCUN");
            idClient = avisClientService.insertClient(newClient); // à implémenter si besoin
            */
            }

            // Insertion de l'avis
            avisClientService.insertAvis(new AvisClient(0, note, date, commentaire, idClient));
            actualiserListe();
            viderFormulaire();
            showAlert(Alert.AlertType.INFORMATION, "Ajout réussi", null, "L'avis a été ajouté avec succès");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Ajout échoué", e.getMessage());
        }
    }


    @FXML
    private void modifierAvis() {
        if (selectedAvis == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", null, "Veuillez sélectionner un avis à modifier");
            return;
        }

        try {
            int note = Integer.parseInt(noteComboBox.getValue());
            String date = datePicker.getValue().toString();
            String commentaire = txtCommentaire.getText();
            String nomClient = txtNomClient.getText();
            String prenomClient = txtPrenomClient.getText();

            int idClient = avisClientService.findClientIdByNomPrenom(nomClient, prenomClient);

            AvisClient avis = new AvisClient(selectedAvis.getIdAvis(), note, date, commentaire, idClient);
            avisClientService.updateAvis(avis);
            actualiserListe();
            viderFormulaire();
            selectedAvis = null;
            showAlert(Alert.AlertType.INFORMATION, "Modification réussie", null, "L'avis a été modifié avec succès");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Modification échouée", e.getMessage());
        }
    }

    @FXML
    private void supprimerAvis() {
        if (selectedAvis == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", null, "Veuillez sélectionner un avis à supprimer");
            return;
        }

        try {
            avisClientService.deleteAvis(selectedAvis);
            actualiserListe();
            viderFormulaire();
            selectedAvis = null;
            showAlert(Alert.AlertType.INFORMATION, "Suppression réussie", null, "L'avis a été supprimé avec succès");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Suppression échouée", e.getMessage());
        }
    }

    private void actualiserListe() {
        try {
            AvisClients avisClients = avisClientService.selectAvisClients();
            ObservableList<AvisClient> avisList = FXCollections.observableArrayList(avisClients.getAvisClients());
            tableAvis.setItems(avisList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Chargement échoué", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
