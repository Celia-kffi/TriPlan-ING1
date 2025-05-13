package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.EmpreinteCarbone;
import edu.ezip.ing1.pds.business.dto.EmpreintesCarbone;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.EmpreinteCarboneService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class EmpreinteCarboneController {

    @FXML private TableView<EmpreinteCarbone> tableEmpreinte;
    @FXML private TableColumn<EmpreinteCarbone, Integer> colId;
    @FXML private TableColumn<EmpreinteCarbone, Double> colKgCO2;

    private ObservableList<EmpreinteCarbone> empreinteList = FXCollections.observableArrayList();
    private EmpreinteCarboneService empreinteService;

    @FXML
    public void initialize() {
        try {
            NetworkConfig config = ConfigLoader.loadConfig(NetworkConfig.class, "network.yaml");
            empreinteService = new EmpreinteCarboneService(config);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de configuration", e.getMessage());
            return;
        }

        initTableColumns();
        loadEmpreintes();
    }

    private void initTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idEmpreinte"));
        colKgCO2.setCellValueFactory(new PropertyValueFactory<>("empreinteKgCO2"));

        tableEmpreinte.setItems(empreinteList);
    }

    private void loadEmpreintes() {
        empreinteList.clear();
        try {
            EmpreintesCarbone response = empreinteService.selectEmpreinte();
            if (response != null && response.getEmpreintesCarbone() != null) {
                empreinteList.addAll(response.getEmpreintesCarbone());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur chargement", e.getMessage());
        }
    }

    @FXML
    private void onAjouter() {
        try {
            int id = Integer.parseInt(prompt("ID de l'empreinte :"));
            double kgCO2 = Double.parseDouble(prompt("Quantité de CO2 (kg) :"));

            EmpreinteCarbone empreinte = new EmpreinteCarbone(id, kgCO2);
            empreinteService.insertEmpreinte(empreinte);
            showAlert(Alert.AlertType.INFORMATION, "Ajout", "Empreinte carbone ajoutée !");
            loadEmpreintes();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur ajout", e.getMessage());
        }
    }

    @FXML
    private void onModifier() {
        EmpreinteCarbone selected = tableEmpreinte.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez une empreinte carbone à modifier.");
            return;
        }

        try {
            double kgCO2 = Double.parseDouble(prompt("Quantité de CO2 (kg) :", String.valueOf(selected.getEmpreinteKgCO2())));
            EmpreinteCarbone updated = new EmpreinteCarbone(selected.getIdEmpreinte(), kgCO2);
            empreinteService.updateEmpreinte(updated);
            showAlert(Alert.AlertType.INFORMATION, "Modification", "Empreinte carbone modifiée !");
            loadEmpreintes();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur modification", e.getMessage());
        }
    }

    @FXML
    private void onSupprimer() {
        EmpreinteCarbone selected = tableEmpreinte.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez une empreinte carbone à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cette empreinte carbone ?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirmation");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    empreinteService.deleteEmpreinte(selected);
                    showAlert(Alert.AlertType.INFORMATION, "Suppression", "Empreinte carbone supprimée !");
                    loadEmpreintes();
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
