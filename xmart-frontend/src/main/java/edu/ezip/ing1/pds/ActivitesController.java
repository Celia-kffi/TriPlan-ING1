package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.Activite;
import edu.ezip.ing1.pds.business.dto.Activites;
import edu.ezip.ing1.pds.business.dto.Destination;
import edu.ezip.ing1.pds.services.ActiviteService;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ActivitesController {

    @FXML
    private Label labelTitre;

    @FXML
    private TableView<Activite> tableActivites;

    @FXML
    private TableColumn<Activite, Integer> colIdActivite;

    @FXML
    private TableColumn<Activite, String> colNom;

    @FXML
    private TableColumn<Activite, String> colDescription;

    @FXML
    private TableColumn<Activite, Double> colPrix;

    @FXML
    private TableColumn<Activite, ImageView> colImagePath;

    @FXML
    private TableColumn<Activite, String> colIdDestination;

    private ActiviteService activiteService;
    private Destination selectedDestination;

    public ActivitesController() {
        try {
            final String networkConfigFile = "network.yaml";
            final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, networkConfigFile);
            activiteService = new ActiviteService(networkConfig);
        } catch (Exception e) {
            System.err.println("Erreur réseau (chargement ignoré dans le constructeur) : " + e.getMessage());
            activiteService = null;
        }
    }

    public void initData(Destination destination) {
        this.selectedDestination = destination;
        System.out.println(">> Destination reçue dans ActivitesController : " + destination.getVille() + " (ID = " + destination.getIdDestination() + ")");
        labelTitre.setText("Activités pour " + destination.getVille());
        actualiserListe();
    }

    private void actualiserListe() {
        try {
            String idDestination = selectedDestination.getIdDestination();
            Activites activites = activiteService.getActivitesByDestination(idDestination);

            if (activites == null || activites.getActivites() == null || activites.getActivites().isEmpty()) {
//                showAlert(Alert.AlertType.INFORMATION, "Aucune activité disponible",
//                        "Aucune activité trouvée pour cette destination.",
//                        "ID destination : " + idDestination);
                tableActivites.setItems(FXCollections.emptyObservableList());
                return;
            }

            ObservableList<Activite> activiteList = FXCollections.observableArrayList(activites.getActivites());

            colIdActivite.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdActivite()).asObject());
            colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
            colDescription.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
            colPrix.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrix()).asObject());
            colIdDestination.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIdDestination()));

            // Affichage de l'image depuis le dossier /images/
            colImagePath.setCellValueFactory(data -> {
                String imageName = data.getValue().getImagePath();
                try {
                    Image image = new Image(getClass().getResourceAsStream("/images/" + imageName), 60, 40, true, true);
                    ImageView imageView = new ImageView(image);
                    return new SimpleObjectProperty<>(imageView);
                } catch (Exception e) {
                    System.err.println("Image non trouvée : " + imageName);
                    return new SimpleObjectProperty<>(new ImageView());
                }
            });

            tableActivites.setItems(activiteList);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Chargement des activités échoué", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void initDataFromCarte(String destinationId, Activites activites) {
        labelTitre.setText("Activités pour la destination " + destinationId);

        ObservableList<Activite> activiteList = FXCollections.observableArrayList(activites.getActivites());

        colIdActivite.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdActivite()).asObject());
        colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        colDescription.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        colPrix.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrix()).asObject());
        colImagePath.setCellValueFactory(data -> {
            String imageName = data.getValue().getImagePath();
            try {
                Image image = new Image(getClass().getResourceAsStream("/images/" + imageName), 60, 40, true, true);
                return new SimpleObjectProperty<>(new ImageView(image));
            } catch (Exception e) {
                return new SimpleObjectProperty<>(new ImageView());
            }
        });
        colIdDestination.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIdDestination()));

        tableActivites.setItems(activiteList);
    }

}
