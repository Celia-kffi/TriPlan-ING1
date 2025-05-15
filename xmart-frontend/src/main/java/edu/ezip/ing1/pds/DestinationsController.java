package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.Destination;
import edu.ezip.ing1.pds.business.dto.Destinations;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.DestinationService;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class DestinationsController {

    @FXML
    private TableView<Destination> tableDestinations;
    @FXML
    private TableColumn<Destination, String> colId;
    @FXML
    private TableColumn<Destination, String> colVille;
    @FXML
    private TableColumn<Destination, String> colPays;
    @FXML
    private TableColumn<Destination, String> colClimat;
    @FXML
    private TableColumn<Destination, Double> colPrixParJour;

    private DestinationService destinationService;

    public DestinationsController() {
        try {
            final NetworkConfig config = ConfigLoader.loadConfig(NetworkConfig.class, "network.yaml");
            destinationService = new DestinationService(config);
        } catch (Exception e) {
            System.err.println("Erreur chargement config : " + e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIdDestination()));
        colVille.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVille()));
        colPays.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPays()));
        colClimat.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClimat()));
        colPrixParJour.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrixParJour()).asObject());

        tableDestinations.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                afficherActivitesPourDestination(newSel);
            }
        });

        chargerDestinations();
    }

    private void chargerDestinations() {
        try {
            Destinations destinations = destinationService.selectDestinations();
            ObservableList<Destination> data = FXCollections.observableArrayList(destinations.getDestinations());
            tableDestinations.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void afficherActivitesPourDestination(Destination destination) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ActivitesView.fxml"));
            Parent root = loader.load();

            ActivitesController controller = loader.getController();
            controller.initData(destination);

            Stage stage = new Stage();
            stage.setTitle("Activités de " + destination.getVille());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur d'affichage des activités :\n" + e.getMessage());
            alert.showAndWait();
        }
    }
}