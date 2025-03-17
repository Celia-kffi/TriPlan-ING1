package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.services.ClientService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;

public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private final NetworkConfig networkConfig;
    private final ClientService clientService;

    public MainController() throws IOException {
        this.networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, "network.yaml");
        this.clientService = new ClientService(networkConfig);
    }

    @FXML
    private void handleClient(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionClients.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root);


            Stage stage1 = new Stage();
            stage1.setTitle("Gestion des Clients");
            stage1.setScene(scene);


            stage1.show();
        } catch (IOException e) {
            logger.error("Erreur lors de l'affichage de GestionClients", e);
            showError("Erreur lors de l'affichage de GestionClients");
        }

    }



    @FXML
    private void handleAvis(ActionEvent event) {
        GestionAvisClients.openFrame(networkConfig);
    }

    @FXML
    private void handleEmpreinte(ActionEvent event) {
        new EmpreinteCarboneGUI();
    }

    @FXML
    private void handleQuitter(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }


}
