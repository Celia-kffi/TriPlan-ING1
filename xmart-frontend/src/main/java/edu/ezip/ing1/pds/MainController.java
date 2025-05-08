package edu.ezip.ing1.pds;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class MainController {

    public MainController() {
        System.out.println(">>> Constructeur MainController appelé");
    }

    @FXML
    private AnchorPane leftMenu;

    private boolean leftMenuVisible = false;

    @FXML
    private void initialize() {
        System.out.println(">>> MÉTHODE initialize() appelée");

        // Test : menu coulissant uniquement, sans MySQL
        if (leftMenu != null) {
            leftMenu.setTranslateX(-200);
            leftMenu.parentProperty().addListener((obs, oldParent, newParent) -> {
                if (leftMenu.getScene() != null) {
                    leftMenu.getScene().setOnMouseMoved(event -> {
                        double x = event.getSceneX();
                        if (x < 10 && !leftMenuVisible) {
                            showLeftMenu();
                        } else if (x > 210 && leftMenuVisible) {
                            hideLeftMenu();
                        }
                    });
                }
            });
        }
    }

    private void showLeftMenu() {
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), leftMenu);
        slideIn.setToX(0);
        slideIn.play();
        leftMenuVisible = true;
    }

    private void hideLeftMenu() {
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), leftMenu);
        slideOut.setToX(-200);
        slideOut.play();
        leftMenuVisible = false;
    }

    @FXML
    private void handleTest(ActionEvent event) {
        System.out.println("✅ BOUTON TEST cliqué !");
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Le bouton TEST fonctionne !");
        alert.showAndWait();
    }
}
