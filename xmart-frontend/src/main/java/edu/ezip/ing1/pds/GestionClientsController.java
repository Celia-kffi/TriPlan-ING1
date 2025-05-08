package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.Client;
import edu.ezip.ing1.pds.business.dto.Clients;
import edu.ezip.ing1.pds.services.ClientService;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.Optional;

public class GestionClientsController {

    @FXML
    private TableView<Client> tableClients;
    @FXML
    private TableColumn<Client, Integer> colIdClient;
    @FXML
    private TableColumn<Client, String> colNom;
    @FXML
    private TableColumn<Client, String> colPrenom;
    @FXML
    private TableColumn<Client, Integer> colAge;
    @FXML
    private TableColumn<Client, String> colNationalite;
    @FXML
    private TableColumn<Client, Double> colBudget;
    @FXML
    private TableColumn<Client, String> colIdPaiement;

    private ClientService clientService;

    public GestionClientsController() throws IOException {
        try {
            final String networkConfigFile = "network.yaml";
            final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, networkConfigFile);
            clientService = new ClientService(networkConfig);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur de configuration réseau", "Erreur de configuration", e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        colIdClient.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdClient()).asObject());
        colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        colPrenom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        colAge.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAge()).asObject());
        colNationalite.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNationalite()));
        colBudget.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getBudget()).asObject());
        colIdPaiement.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdPaiement()));

        actualiserListe();
    }

    @FXML
    private void ajouterClient(ActionEvent event) {
        TextInputDialog dialogNom = new TextInputDialog();
        dialogNom.setTitle("Ajouter Client");
        dialogNom.setHeaderText("Saisir le nom du client :");
        String nom = dialogNom.showAndWait().orElse("");

        TextInputDialog dialogPrenom = new TextInputDialog();
        dialogPrenom.setTitle("Ajouter Client");
        dialogPrenom.setHeaderText("Saisir le prénom du client :");
        String prenom = dialogPrenom.showAndWait().orElse("");

        TextInputDialog dialogAge = new TextInputDialog();
        dialogAge.setTitle("Ajouter Client");
        dialogAge.setHeaderText("Saisir l'âge du client :");
        String age = dialogAge.showAndWait().orElse("");

        TextInputDialog dialogNationalite = new TextInputDialog();
        dialogNationalite.setTitle("Ajouter Client");
        dialogNationalite.setHeaderText("Saisir la nationalité du client :");
        String nationalite = dialogNationalite.showAndWait().orElse("");

        TextInputDialog dialogBudget = new TextInputDialog();
        dialogBudget.setTitle("Ajouter Client");
        dialogBudget.setHeaderText("Saisir le budget du client :");
        String budget = dialogBudget.showAndWait().orElse("");

        TextInputDialog dialogIdPaiement = new TextInputDialog();
        dialogIdPaiement.setTitle("Ajouter Client");
        dialogIdPaiement.setHeaderText("Saisir l'ID de paiement du client :");
        String idPaiement = dialogIdPaiement.showAndWait().orElse("");

        if (nom != null && prenom != null && age != null && nationalite != null && budget != null) {
            try {
                Client client = new Client(nom, prenom, Integer.parseInt(age), nationalite, Double.parseDouble(budget), idPaiement);
                clientService.insertClient(client);
                actualiserListe();
                showAlert(AlertType.INFORMATION, "Client ajouté", "Le client a été ajouté avec succès", "");
            } catch (Exception ex) {
                showAlert(AlertType.ERROR, "Erreur d'ajout", "Erreur lors de l'ajout du client", ex.getMessage());
            }
        }
    }

    @FXML
    private void modifierClient(ActionEvent event) {
        Client selectedClient = tableClients.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            TextInputDialog dialogNom = new TextInputDialog(selectedClient.getNom());
            dialogNom.setTitle("Modifier Client");
            dialogNom.setHeaderText("Nom :");
            String nom = dialogNom.showAndWait().orElse(selectedClient.getNom());

            TextInputDialog dialogPrenom = new TextInputDialog(selectedClient.getPrenom());
            dialogPrenom.setTitle("Modifier Client");
            dialogPrenom.setHeaderText("Prénom :");
            String prenom = dialogPrenom.showAndWait().orElse(selectedClient.getPrenom());

            TextInputDialog dialogAge = new TextInputDialog(String.valueOf(selectedClient.getAge()));
            dialogAge.setTitle("Modifier Client");
            dialogAge.setHeaderText("Âge :");
            String age = dialogAge.showAndWait().orElse(String.valueOf(selectedClient.getAge()));

            TextInputDialog dialogNationalite = new TextInputDialog(selectedClient.getNationalite());
            dialogNationalite.setTitle("Modifier Client");
            dialogNationalite.setHeaderText("Nationalité :");
            String nationalite = dialogNationalite.showAndWait().orElse(selectedClient.getNationalite());

            TextInputDialog dialogBudget = new TextInputDialog(String.valueOf(selectedClient.getBudget()));
            dialogBudget.setTitle("Modifier Client");
            dialogBudget.setHeaderText("Budget :");
            String budget = dialogBudget.showAndWait().orElse(String.valueOf(selectedClient.getBudget()));

            TextInputDialog dialogIdPaiement = new TextInputDialog(selectedClient.getIdPaiement());
            dialogIdPaiement.setTitle("Modifier Client");
            dialogIdPaiement.setHeaderText("ID Paiement :");
            String idPaiement = dialogIdPaiement.showAndWait().orElse(selectedClient.getIdPaiement());

            try {
                Client client = new Client(selectedClient.getIdClient(), nom, prenom, Integer.parseInt(age), nationalite, Double.parseDouble(budget), idPaiement);
                clientService.updateClient(client);
                actualiserListe();
                showAlert(AlertType.INFORMATION, "Client modifié", "Le client a été modifié avec succès", "");
            } catch (Exception ex) {
                showAlert(AlertType.ERROR, "Erreur de modification", "Erreur lors de la modification", ex.getMessage());
            }
        }
    }

    @FXML
    private void supprimerClient(ActionEvent event) {
        Client selectedClient = tableClients.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            try {
                clientService.deleteClient(selectedClient);
                actualiserListe();
                showAlert(AlertType.INFORMATION, "Client supprimé", "Le client a été supprimé avec succès", "");
            } catch (Exception ex) {
                showAlert(AlertType.ERROR, "Erreur de suppression", "Erreur lors de la suppression", ex.getMessage());
            }
        }
    }

    @FXML
    private void actualiserListe() {
        try {
            Clients clients = clientService.selectClients();
            tableClients.getItems().clear();
            if (clients != null) {
                tableClients.getItems().addAll(clients.getClients());
            }
        } catch (Exception ex) {
            showAlert(AlertType.ERROR, "Erreur de rafraîchissement", "Erreur lors du rafraîchissement des données", ex.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
}
}