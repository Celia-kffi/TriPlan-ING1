package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.Client;
import edu.ezip.ing1.pds.business.dto.Clients;
import edu.ezip.ing1.pds.services.ClientService;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.GestionVoyage;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class GestionClientsController {

    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtAge;
    @FXML
    private TextField txtNationalite;
    @FXML
    private TextField txtBudget;
    @FXML
    private TextField txtIdPaiement;


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

    private Client selectedClient;

    @FXML
    private void initialize() {
        colIdClient.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdClient()).asObject());
        colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        colPrenom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        colAge.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAge()).asObject());
        colNationalite.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNationalite()));
        colBudget.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getBudget()).asObject());
        colIdPaiement.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdPaiement()));




        tableClients.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedClient=newSelection;
                        remplirFormulaire(newSelection);
                    }
                }
        );

        actualiserListe();


    }


    private void remplirFormulaire(Client client) {
        txtNom.setText(client.getNom());
        txtPrenom.setText(client.getPrenom());
        txtAge.setText(String.valueOf(client.getAge()));
        txtNationalite.setText(client.getNationalite());
        txtBudget.setText(String.valueOf(client.getBudget()));
        txtIdPaiement.setText(client.getIdPaiement());
    }


    private void viderFormulaire() {
        txtNom.clear();
        txtPrenom.clear();
        txtAge.clear();
        txtNationalite.clear();
        txtBudget.clear();
        txtIdPaiement.clear();
    }


    private boolean validerFormulaire() {
        if (txtNom.getText().isEmpty() || txtPrenom.getText().isEmpty() ||
                txtAge.getText().isEmpty() || txtNationalite.getText().isEmpty() ||
                txtBudget.getText().isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur de validation", "Champs obligatoires",
                    "Veuillez remplir tous les champs obligatoires");
            return false;
        }

        try {
            if (!txtAge.getText().isEmpty()) Integer.parseInt(txtAge.getText());
            if (!txtBudget.getText().isEmpty()) Double.parseDouble(txtBudget.getText());
            return true;
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Erreur de format", "Format invalide",
                    "Veuillez saisir un format valide!");
            return false;
        }
    }

    @FXML
    private void ajouterClient(ActionEvent event) {
        if (!validerFormulaire()) return;

            String nom = txtNom.getText();
            String prenom = txtPrenom.getText();
            int age = Integer.parseInt(txtAge.getText());
            String nationalite = txtNationalite.getText();
            double budget = Double.parseDouble(txtBudget.getText());
            String idPaiement = txtIdPaiement.getText();

        for (Client existingClient : tableClients.getItems()) {
            if (existingClient.getNom().equalsIgnoreCase(nom) &&
                    existingClient.getPrenom().equalsIgnoreCase(prenom) &&
                    existingClient.getAge() == age &&
                    existingClient.getNationalite().equalsIgnoreCase(nationalite)){
                showAlert(AlertType.WARNING, "Doublon détecté", "Client déjà existant",
                        "Un client avec les mêmes informations existe déjà.");
                return;
            }
        }

        try {

            Client client = new Client(nom, prenom, age, nationalite, budget, idPaiement);
            clientService.insertClient(client);
            viderFormulaire();
            actualiserListe();
            showAlert(AlertType.INFORMATION, "Client ajouté", "Le client a été ajouté avec succès", "");
        } catch (Exception ex) {
            showAlert(AlertType.ERROR, "Erreur d'ajout", "Erreur lors de l'ajout du client", ex.getMessage());
        }
    }

    @FXML
    private void modifierClient(ActionEvent event) {
        if (selectedClient==null) {
            showAlert(AlertType.WARNING, "Sélection requise", "Aucun client sélectionné",
                    "Veuillez sélectionner un client à modifier");
            return;
        }

        if (!validerFormulaire()) return;

        try {
            int idClient = selectedClient.getIdClient();
            String nom = txtNom.getText();
            String prenom = txtPrenom.getText();
            int age = Integer.parseInt(txtAge.getText());
            String nationalite = txtNationalite.getText();
            double budget = Double.parseDouble(txtBudget.getText());
            String idPaiement = txtIdPaiement.getText();

            Client client = new Client(idClient, nom, prenom, age, nationalite, budget, idPaiement);
            clientService.updateClient(client);
            actualiserListe();
            showAlert(AlertType.INFORMATION, "Client modifié", "Le client a été modifié avec succès", "");
        } catch (Exception ex) {
            showAlert(AlertType.ERROR, "Erreur de modification", "Erreur lors de la modification", ex.getMessage());
        }
    }

    @FXML
    private void supprimerClient(ActionEvent event) {
        if (selectedClient == null) {
            showAlert(AlertType.WARNING, "Sélection requise", "Aucun client sélectionné",
                    "Veuillez sélectionner un client à supprimer dans le tableau");
            return;
        }

        try {
            clientService.deleteClient(selectedClient);
            viderFormulaire();
            actualiserListe();
            selectedClient = null;
            showAlert(AlertType.INFORMATION, "Client supprimé", "Le client a été supprimé avec succès", "");
        } catch (Exception ex) {
            showAlert(AlertType.ERROR, "Erreur de suppression", "Erreur lors de la suppression", ex.getMessage());
        }
    }
    private void actualiserListe() {
        try {
            Clients clients = clientService.selectClients();
            tableClients.getItems().clear();
            if (clients != null && clients.getClients() != null) {
                tableClients.getItems().addAll(clients.getClients());
            }
        } catch (Exception ex) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger les clients", ex.getMessage());
        }
    }

    @FXML
    private void ouvrirFenetreVoyage(ActionEvent event) {
        try{
            Client clientSelectionne = tableClients.getSelectionModel().getSelectedItem();

            if (clientSelectionne == null) {
                showAlert(AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un client d'abord", "");
                return;
            }
            FXMLLoader Loader = new FXMLLoader(getClass().getResource("/VoyageView.fxml"));
            Parent root = Loader.load();

            GestionVoyage voyageController = Loader.getController();
            voyageController.setClientSelectionne(clientSelectionne);


            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Création de voyage");
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur d'ouverture", "Impossible d'ouvrir la fenêtre de voyage", e.getMessage());
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