package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.MoyenTransport;
import edu.ezip.ing1.pds.business.dto.MoyenTransports;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.MoyenTransportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MoyenTransportController {

    @FXML private TableView<MoyenTransport> tableMoyenTransport;
    @FXML private TableColumn<MoyenTransport, String> colId;
    @FXML private TableColumn<MoyenTransport, String> colType;
    @FXML private TableColumn<MoyenTransport, Double> colFacteurEmission;

    private ObservableList<MoyenTransport> moyenTransportList = FXCollections.observableArrayList();
    private MoyenTransportService moyenTransportService;

    @FXML
    public void initialize() {
        try {
            NetworkConfig config = ConfigLoader.loadConfig(NetworkConfig.class, "network.yaml");
            moyenTransportService = new MoyenTransportService(config);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de configuration", e.getMessage());
            return;
        }

        initTableColumns();
        loadMoyensTransports();
    }

    private void initTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idMoyenDestination"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeTransports"));
        colFacteurEmission.setCellValueFactory(new PropertyValueFactory<>("facteurEmission"));

        tableMoyenTransport.setItems(moyenTransportList);
    }

    private void loadMoyensTransports() {
        moyenTransportList.clear();
        try {
            MoyenTransports response = moyenTransportService.selectTransport();
            if (response != null && response.getMoyenTransports() != null) {
                moyenTransportList.addAll(response.getMoyenTransports());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur chargement", e.getMessage());
        }
    }

    @FXML
    private void onAjouter() {
        try {
            String id = prompt("ID du moyen de transport :");
            String type = prompt("Type de transport :");
            double facteurEmission = Double.parseDouble(prompt("Facteur d'émission :"));

            MoyenTransport moyenTransport = new MoyenTransport(id, type, facteurEmission);
            moyenTransportService.insertTransport(moyenTransport);
            showAlert(Alert.AlertType.INFORMATION, "Ajout", "Moyen de transport ajouté !");
            loadMoyensTransports();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur ajout", e.getMessage());
        }
    }

    @FXML
    private void onModifier() {
        MoyenTransport selected = tableMoyenTransport.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez un moyen de transport à modifier.");
            return;
        }

        try {
            String type = prompt("Type de transport :", selected.getTypeTransports());
            double facteurEmission = Double.parseDouble(prompt("Facteur d'émission :", String.valueOf(selected.getFacteurEmission())));

            MoyenTransport updated = new MoyenTransport(selected.getIdMoyenDestination(), type, facteurEmission);
            moyenTransportService.updateTransport(updated);
            showAlert(Alert.AlertType.INFORMATION, "Modification", "Moyen de transport modifié !");
            loadMoyensTransports();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur modification", e.getMessage());
        }
    }

    @FXML
    private void onSupprimer() {
        MoyenTransport selected = tableMoyenTransport.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez un moyen de transport à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce moyen de transport ?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirmation");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    moyenTransportService.deleteTransport(selected);
                    showAlert(Alert.AlertType.INFORMATION, "Suppression", "Moyen de transport supprimé !");
                    loadMoyensTransports();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur suppression", e.getMessage());
                }
            }
        });
    }

    private String prompt(String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(message);
        dialog.showAndWait();
        return dialog.getResult();
    }

    private String prompt(String message, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setHeaderText(message);
        dialog.showAndWait();
        return dialog.getResult();
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
