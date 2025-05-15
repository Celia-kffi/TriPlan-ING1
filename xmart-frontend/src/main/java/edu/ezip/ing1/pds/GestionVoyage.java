package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.*;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.ClientService;
import edu.ezip.ing1.pds.services.DestinationService;
import edu.ezip.ing1.pds.services.VoyageService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.List;

public class GestionVoyage {

    @FXML
    private TextField txtClient;

    @FXML
    private DatePicker dpDepart;

    @FXML
    private DatePicker dpRetour;

    @FXML
    private ComboBox<String> cbTypeVoyage;

    @FXML
    private ComboBox<String> cbDestinations;

    @FXML
    private TextField txtMontant;

    private VoyageService voyageService;
    private ClientService clientService;
    private DestinationService destinationService;

    private Client clientSelectionne;

    public void setClientSelectionne(Client client) {
        this.clientSelectionne = client;
        txtClient.setText(client.getNom());
        //txtClient.setDisable(true);
    }



    public GestionVoyage() {
        try {
            final String networkConfigFile = "network.yaml";
            final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, networkConfigFile);
            voyageService = new VoyageService(networkConfig);
            clientService = new ClientService(networkConfig);
            destinationService = new DestinationService(networkConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        cbTypeVoyage.getItems().addAll("Vacances", "Affaires", "Aventure", "Luxe");
        cbTypeVoyage.setValue("Vacances");
        dpDepart.valueProperty().addListener((obs, oldDate, newDate) ->{ verifierDates(); calculerMontant();});
        dpRetour.valueProperty().addListener((obs, oldDate, newDate) -> {verifierDates(); calculerMontant();});
        cbDestinations.valueProperty().addListener((obs, oldDest, newDest) -> calculerMontant());

        loadDestinations();
    }

    @FXML
    private void handleRechercheClient() {
        String clientName = txtClient.getText().trim();

        if (clientName.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un nom de client.");
            return;
        }

        try {
            Client client = clientService.rechercherClientParNom(clientName);

            if (client != null) {
                showAlert("Client trouvé", "Nom du client : " + client.getNom() + ", ID : " + client.getIdClient());
            } else {
                showAlert("Aucun client trouvé", "Aucun client n'a été trouvé avec ce nom.");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la recherche du client.");
            e.printStackTrace();
            System.out.println("DEBUG: Exception pendant recherche client : " + e.getMessage());
        }
    }

    private void loadDestinations() {
        try {
            Destinations destinations = destinationService.selectDestinations();

            if (destinations != null) {

                for (Destination destination : destinations.getDestinations()) {
                    cbDestinations.getItems().add(destination.getVille() + ", " + destination.getPays());
                }
            } else {
                showAlert("Erreur", "Aucune destination trouvée.");
            }
        } catch (IOException | InterruptedException e) {
            showAlert("Erreur", "Une erreur est survenue lors du chargement des destinations.");
            e.printStackTrace();
        }
    }

    private void calculerMontant() {
        if (dpDepart.getValue() == null || dpRetour.getValue() == null || cbDestinations.getValue() == null) {
            return;
        }

        long jours = java.time.temporal.ChronoUnit.DAYS.between(dpDepart.getValue(), dpRetour.getValue());

        if (jours <= 0) {
            txtMontant.setText("");
            return;
        }

        String villePays = cbDestinations.getValue();

        try {
            Destinations destinations = destinationService.selectDestinations();
            for (Destination dest : destinations.getDestinations()) {
                String fullName = dest.getVille() + ", " + dest.getPays();
                if (fullName.equals(villePays)) {
                    double montant = jours * dest.getPrixParJour();
                    txtMontant.setText(String.valueOf(montant));
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleEnregistrer(ActionEvent event) {
        String client = txtClient.getText();
        String typeVoyage = cbTypeVoyage.getValue();
        String destination = cbDestinations.getValue();
        String montantText = txtMontant.getText();
        String dateDepart = dpDepart.getValue() != null ? dpDepart.getValue().toString() : "";
        String dateRetour = dpRetour.getValue() != null ? dpRetour.getValue().toString() : "";

        if (client.isEmpty() || destination == null || destination.isEmpty() || montantText.isEmpty()
                || dateDepart.isEmpty() || dateRetour.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        double montant;
        try {
            montant = Double.parseDouble(montantText);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le montant doit être un nombre valide.");
            return;
        }

        try {
            Client foundClient = clientService.rechercherClientParNom(client);
            if (foundClient == null) {
                showAlert("Erreur", "Client introuvable.");
                return;
            }

            if (foundClient.getBudget() < montant) {
                showAlert("Erreur", "Le budget du client est insuffisant pour ce voyage.");
                return;
            }

            Voyage voyage = new Voyage(montant, typeVoyage, dateDepart, dateRetour, foundClient.getIdClient());
            voyageService.insertVoyage(voyage);
            showAlert("Succès", "Le voyage a été enregistré avec succès.");
        } catch (IOException | InterruptedException e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'enregistrement du voyage.");
            e.printStackTrace();
        }

        handleAnnuler(event);
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        txtClient.clear();
        cbDestinations.setValue(null);
        txtMontant.clear();
        dpDepart.setValue(null);
        dpRetour.setValue(null);
        cbTypeVoyage.setValue("Vacances");
    }

    @FXML
    private void handleListeVoyages(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeVoyages.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setTitle("Liste des voyages");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'ouverture de la liste des voyages.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleActivitees(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DestinationsView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setTitle("Activitees");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'ouverture des activitées.");
            e.printStackTrace();
        }

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void verifierDates() {
        if (dpDepart.getValue() != null && dpRetour.getValue() != null) {
            if (!dpDepart.getValue().isBefore(dpRetour.getValue())) {
                showAlert("Erreur de dates", "La date de retour doit être postérieur à la date de départ.");
                txtMontant.clear();
            }
        }
    }

}