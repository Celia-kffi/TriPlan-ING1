package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.Hebergement;
import edu.ezip.ing1.pds.business.dto.Hebergements;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.HebergementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GestionHebergementController {

    @FXML private TableView<Hebergement> tableHebergement;
    @FXML private TableColumn<Hebergement, Integer> colId;
    @FXML private TableColumn<Hebergement, Integer> colPrix;
    @FXML private TableColumn<Hebergement, String> colNom;
    @FXML private TableColumn<Hebergement, String> colType;
    @FXML private TableColumn<Hebergement, String> colImage;

    private ObservableList<Hebergement> hebergementList = FXCollections.observableArrayList();
    private HebergementService hebergementService;

    @FXML
    public void initialize() {
        try {
            NetworkConfig config = ConfigLoader.loadConfig(NetworkConfig.class, "network.yaml");
            hebergementService = new HebergementService(config);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de configuration", e.getMessage());
            return;
        }

        initTableColumns();
        loadHebergements();
    }

    private void initTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idHebergement"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixNuit"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomH"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));

        colImage.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imageName, boolean empty) {
                super.updateItem(imageName, empty);
                if (empty || imageName == null || imageName.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        Image img = new Image(getClass().getResource("/photo/" + imageName).toExternalForm());
                        imageView.setImage(img);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(new Label("Erreur image"));
                    }
                }
            }
        });

        tableHebergement.setItems(hebergementList);
    }

    private void loadHebergements() {
        hebergementList.clear();
        try {
            Hebergements response = hebergementService.selectHebergement();
            if (response != null && response.getHebergements() != null) {
                hebergementList.addAll(response.getHebergements());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur chargement", e.getMessage());
        }
    }

    @FXML
    private void onAjouter() {
        try {
            int id = Integer.parseInt(prompt("ID de l'hébergement :"));
            int prix = Integer.parseInt(prompt("Prix par nuit :"));
            String nom = prompt("Nom :");
            String type = prompt("Type :");
            String image = prompt("Nom de l'image (ex: hotel1.png) :");

            Hebergement hebergement = new Hebergement(id, prix, nom, type, image);
            hebergementService.insertHebergement(hebergement);
            showAlert(Alert.AlertType.INFORMATION, "Ajout", "Hébergement ajouté !");
            loadHebergements();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur ajout", e.getMessage());
        }
    }

    @FXML
    private void onModifier() {
        Hebergement selected = tableHebergement.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez un hébergement à modifier.");
            return;
        }

        try {
            int prix = Integer.parseInt(prompt("Prix par nuit :", String.valueOf(selected.getPrixNuit())));
            String nom = prompt("Nom :", selected.getNomH());
            String type = prompt("Type :", selected.getType());
            String image = prompt("Nom de l'image :", selected.getImage());

            Hebergement updated = new Hebergement(selected.getIdHebergement(), prix, nom, type, image);
            hebergementService.updateHebergement(updated);
            showAlert(Alert.AlertType.INFORMATION, "Modification", "Hébergement modifié !");
            loadHebergements();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur modification", e.getMessage());
        }
    }

    @FXML
    private void onSupprimer() {
        Hebergement selected = tableHebergement.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez un hébergement à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cet hébergement ?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirmation");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    hebergementService.deleteHebergement(selected);
                    showAlert(Alert.AlertType.INFORMATION, "Suppression", "Hébergement supprimé !");
                    loadHebergements();
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
