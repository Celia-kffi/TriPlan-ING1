package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.Hebergement;
import edu.ezip.ing1.pds.business.dto.Hebergements;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.HebergementService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class GestionHebergementController {

    @FXML private HBox carouselBox;
    @FXML private ImageView selectedImage;
    @FXML private Label labelNom;
    @FXML private Label labelPrix;
    @FXML private Label labelType;
    @FXML private Label labelEmission;

    private HebergementService hebergementService;
    private int currentIndex = 0;
    private List<Hebergement> hebergementsList = new ArrayList<>();

    private EmpreinteCarboneController empreinteCarboneController;

    public void setEmpreinteCarboneController(EmpreinteCarboneController controller) {
        this.empreinteCarboneController = controller;
    }

    @FXML
    public void initialize() {
        try {
            NetworkConfig config = ConfigLoader.loadConfig(NetworkConfig.class, "network.yaml");
            hebergementService = new HebergementService(config);
            loadHebergements();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de configuration", e.getMessage());
        }
    }

    private void loadHebergements() {
        try {
            Hebergements response = hebergementService.selectHebergement();
            if (response != null && response.getHebergements() != null) {
                hebergementsList = new ArrayList<>(response.getHebergements());
                carouselBox.getChildren().clear();
                for (int i = 0; i < hebergementsList.size(); i++) {
                    Hebergement h = hebergementsList.get(i);
                    StackPane stackPane = new StackPane();
                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(120);
                    imageView.setFitHeight(90);
                    imageView.setPreserveRatio(true);

                    try {
                        Image image = new Image(getClass().getResource("/photo/" + h.getImage()).toExternalForm());
                        imageView.setImage(image);
                    } catch (Exception e) {
                        imageView.setImage(new Image("https://via.placeholder.com/120x90.png?text=Erreur"));
                    }

                    imageView.setStyle("-fx-border-color: black; -fx-border-width: 3px; -fx-border-radius: 5px;");
                    int finalI = i;
                    imageView.setOnMouseClicked(event -> {
                        currentIndex = finalI;
                        updateCarousel();
                    });

                    carouselBox.getChildren().add(imageView);
                }
                if (!hebergementsList.isEmpty()) {
                    updateCarousel();
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur chargement", e.getMessage());
        }
    }

    private void showHebergementDetails(Hebergement h) {
        try {
            Image image = new Image(getClass().getResource("/photo/" + h.getImage()).toExternalForm());
            selectedImage.setImage(image);
        } catch (Exception e) {
            selectedImage.setImage(new Image("https://via.placeholder.com/200.png?text=Erreur"));
        }

        labelNom.setText("Nom : " + h.getNomH());
        labelPrix.setText("Prix/nuit : " + h.getPrixNuit() + " €");
        labelType.setText("Type : " + h.getType());
        labelEmission.setText("Emission/nuit : " + h.getEmissionParNuit() + " KgCO2");
    }

    @FXML
    private void onScrollRight() {
        if (currentIndex < hebergementsList.size() - 1) {
            currentIndex++;
            updateCarousel();
        }
    }

    @FXML
    private void onScrollLeft() {
        if (currentIndex > 0) {
            currentIndex--;
            updateCarousel();
        }
    }

    private void updateCarousel() {
        if (!hebergementsList.isEmpty()) {
            Hebergement current = hebergementsList.get(currentIndex);
            showHebergementDetails(current);
        }
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    @FXML
    private void validerHebergement() {
        if (!hebergementsList.isEmpty()) {
            Hebergement selectionne = hebergementsList.get(currentIndex);
            int nbNuits = 2;
            if (empreinteCarboneController != null) {
                empreinteCarboneController.setHebergementEtNuits(selectionne, nbNuits);
            }

            carouselBox.getScene().getWindow().hide();
        }
    }
}
