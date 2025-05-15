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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import java.util.ArrayList;
import java.util.List;

public class GestionHebergementController {

    @FXML private HBox carouselBox;
    @FXML private ImageView selectedImage;
    @FXML private Label labelNom;
    @FXML private Label labelPrix;
    @FXML private Label labelType;
    @FXML private Label labelEmission;
    @FXML private Spinner<Integer> nbNuitsSpinner;

    private HebergementService hebergementService;
    private List<Hebergement> hebergementsList = new ArrayList<>();
    private List<ImageView> allImageViews = new ArrayList<>();

    private int currentIndex = 0;
    private int startIndex = 0;
    private final int visibleCount = 4;

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
        nbNuitsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 1));
    }

    private void loadHebergements() {
        try {
            Hebergements response = hebergementService.selectHebergement();
            if (response != null && response.getHebergements() != null) {
                hebergementsList = new ArrayList<>(response.getHebergements());
                allImageViews.clear();

                for (int i = 0; i < hebergementsList.size(); i++) {
                    Hebergement h = hebergementsList.get(i);
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
                        if (currentIndex < startIndex) {
                            startIndex = currentIndex;
                        } else if (currentIndex >= startIndex + visibleCount) {
                            startIndex = currentIndex - visibleCount + 1;
                        }
                        updateCarousel();
                    });

                    allImageViews.add(imageView);
                }
                currentIndex = 0;
                startIndex = 0;
                updateCarousel();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur chargement", e.getMessage());
        }
    }

    private void updateCarousel() {
        carouselBox.getChildren().clear();

        int endIndex = Math.min(startIndex + visibleCount, allImageViews.size());
        for (int i = startIndex; i < endIndex; i++) {
            ImageView imgView = allImageViews.get(i);
            if (i == currentIndex) {
                imgView.setStyle("-fx-border-color: red; -fx-border-width: 4px; -fx-border-radius: 5px;");
            } else {
                imgView.setStyle("-fx-border-color: black; -fx-border-width: 3px; -fx-border-radius: 5px;");
            }
            carouselBox.getChildren().add(imgView);
        }

        if (!hebergementsList.isEmpty()) {
            showHebergementDetails(hebergementsList.get(currentIndex));
        }
    }

    @FXML
    private void onScrollRight() {
        if (startIndex + visibleCount < allImageViews.size()) {
            startIndex++;
            if (currentIndex < startIndex) {
                currentIndex = startIndex;
            }
            updateCarousel();
        }
    }

    @FXML
    private void onScrollLeft() {

        if (startIndex > 0) {
            startIndex--;
            if (currentIndex > startIndex + visibleCount - 1) {
                currentIndex = startIndex + visibleCount - 1;
            }
            updateCarousel();
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
            int nbNuits = nbNuitsSpinner.getValue();

            if (empreinteCarboneController != null) {
                empreinteCarboneController.setHebergementEtNuits(selectionne, nbNuits);
            }

            carouselBox.getScene().getWindow().hide();
        }
    }
}
