package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.Voyage;
import edu.ezip.ing1.pds.business.dto.Voyages;
import edu.ezip.ing1.pds.services.VoyageService;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ListeVoyageController {

    @FXML
    private TableView<Voyage> tableVoyages;
    @FXML
    private TableColumn<Voyage, Integer> colId;
    @FXML
    private TableColumn<Voyage, Double> colMontant;
    @FXML
    private TableColumn<Voyage, String> colTypeVoyage;
    @FXML
    private TableColumn<Voyage, LocalDate> colDateDepart;
    @FXML
    private TableColumn<Voyage, LocalDate> colDateRetour;
    @FXML
    private TableColumn<Voyage, Integer> colIdClient;
    @FXML
    private Label lblTotalVoyages;

    private VoyageService voyageService;

    public ListeVoyageController() throws IOException {
        try {
            final String networkConfigFile = "network.yaml";
            final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, networkConfigFile);
            voyageService = new VoyageService(networkConfig);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur de configuration réseau", "Erreur de configuration", e.getMessage());
        }
    }

    @FXML
    private void initialize() {

        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdVoyage()).asObject());
        colMontant.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getMontant()).asObject());
        colTypeVoyage.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTypeVoyage()));
        colDateDepart.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
        colDateRetour.setCellValueFactory(new PropertyValueFactory<>("dateRetour"));
        colIdClient.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdClient()).asObject());


        tableVoyages.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

        });


        chargerVoyages();
    }
    @FXML
    private void rafraichir() {
        List<Voyage> voyagesActuels = new ArrayList<>(tableVoyages.getItems());
        voyagesActuels.sort(Comparator.comparingInt(Voyage::getIdVoyage));
        tableVoyages.getItems().setAll(voyagesActuels);


    }

    private void chargerVoyages() {
        try {
            Voyages voyages = voyageService.selectVoyages();
            tableVoyages.getItems().clear();
            if (voyages != null) {
                tableVoyages.getItems().addAll(voyages.getVoyages());


            int nombreVoyages = voyages.getVoyages().size();
            lblTotalVoyages.setText("Total: " + nombreVoyages + " voyage" + (nombreVoyages > 1 ? "s" : ""));
        } else{
            lblTotalVoyages.setText("Total: 0 voyage");
        }
        } catch (Exception ex) {
            showAlert(AlertType.ERROR, "Erreur de chargement", "Erreur lors du chargement des voyages", ex.getMessage());
        }
    }

    @FXML
    private void modifierVoyage() {
        Voyage selectedVoyage = tableVoyages.getSelectionModel().getSelectedItem();
        if (selectedVoyage != null) {
            TextInputDialog dialogMontant = new TextInputDialog(String.valueOf(selectedVoyage.getMontant()));
            dialogMontant.setTitle("Modifier Voyage");
            dialogMontant.setHeaderText("Montant :");
            String montant = dialogMontant.showAndWait().orElse(String.valueOf(selectedVoyage.getMontant()));

            TextInputDialog dialogTypeVoyage = new TextInputDialog(selectedVoyage.getTypeVoyage());
            dialogTypeVoyage.setTitle("Modifier Voyage");
            dialogTypeVoyage.setHeaderText("Type de voyage :");
            String typeVoyage = dialogTypeVoyage.showAndWait().orElse(selectedVoyage.getTypeVoyage());

            TextInputDialog dialogDateDepart = new TextInputDialog(selectedVoyage.getDateDepart().toString());
            dialogDateDepart.setTitle("Modifier Voyage");
            dialogDateDepart.setHeaderText("Date de départ (YYYY-MM-DD) :");
            String dateDepart = dialogDateDepart.showAndWait().orElse(selectedVoyage.getDateDepart().toString());

            TextInputDialog dialogDateRetour = new TextInputDialog(selectedVoyage.getDateRetour().toString());
            dialogDateRetour.setTitle("Modifier Voyage");
            dialogDateRetour.setHeaderText("Date de retour (YYYY-MM-DD) :");
            String dateRetour = dialogDateRetour.showAndWait().orElse(selectedVoyage.getDateRetour().toString());

            TextInputDialog dialogIdClient = new TextInputDialog(String.valueOf(selectedVoyage.getIdClient()));
            dialogIdClient.setTitle("Modifier Voyage");
            dialogIdClient.setHeaderText("ID Client :");
            String idClient = dialogIdClient.showAndWait().orElse(String.valueOf(selectedVoyage.getIdClient()));

            try {

                Voyage voyageModifie = new Voyage(
                        selectedVoyage.getIdVoyage(),
                        Double.parseDouble(montant),
                        typeVoyage,
                        dateDepart,
                        dateRetour,
                        Integer.parseInt(idClient)
                );


                voyageService.updateVoyage(voyageModifie);
                chargerVoyages();

                showAlert(AlertType.INFORMATION, "Voyage modifié", "Le voyage a été modifié avec succès", "");
            } catch (Exception ex) {
                showAlert(AlertType.ERROR, "Erreur de modification", "Erreur lors de la modification du voyage", ex.getMessage());
            }
        } else {
            showAlert(AlertType.WARNING, "Sélection requise", "Aucun voyage sélectionné", "Veuillez sélectionner un voyage à modifier.");
        }
    }

    @FXML
    private void supprimerVoyage() {
        Voyage selectedVoyage = tableVoyages.getSelectionModel().getSelectedItem();
        if (selectedVoyage != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer le voyage");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer ce voyage ?");

            Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                try {

                    voyageService.deleteVoyage(selectedVoyage);

                    chargerVoyages();

                    showAlert(AlertType.INFORMATION, "Voyage supprimé", "Le voyage a été supprimé avec succès", "");
                } catch (Exception ex) {
                    showAlert(AlertType.ERROR, "Erreur de suppression", "Erreur lors de la suppression du voyage", ex.getMessage());
                }
            }
        } else {
            showAlert(AlertType.WARNING, "Sélection requise", "Aucun voyage sélectionné", "Veuillez sélectionner un voyage à supprimer.");
        }
    }


    @FXML
    private void handleFermer() {
        Stage stage = (Stage) tableVoyages.getScene().getWindow();
        stage.close();
    }

    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}