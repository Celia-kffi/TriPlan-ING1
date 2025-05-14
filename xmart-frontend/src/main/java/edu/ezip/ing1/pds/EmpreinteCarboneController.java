package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.Hebergement;
import edu.ezip.ing1.pds.business.dto.MoyenTransport;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class EmpreinteCarboneController {

    private Hebergement hebergementSelectionne;
    private MoyenTransport transportSelectionne;
    private int nuits = 0;
    private float distance = 0;

    @FXML private Label labelHebergement;
    @FXML private Label labelTransport;
    @FXML private Label labelDistance;
    @FXML private Label labelNuits;
    @FXML private Label resultatLabel;

    @FXML
    public void initialize() {
        updateAffichage();
    }

    private void updateAffichage() {
        labelHebergement.setText("Hébergement : " + (hebergementSelectionne != null ? hebergementSelectionne.getNomH() : "Aucun"));
        labelTransport.setText("Transport : " + (transportSelectionne != null ? transportSelectionne.getTypeTransports() : "Aucun"));
        labelDistance.setText("Distance : " + distance + " km");
        labelNuits.setText("Nuits : " + nuits);
    }

    @FXML
    public void allerVersHebergement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_hebergement.fxml")); // chemin correct
            Parent root = loader.load();

            GestionHebergementController controller = loader.getController();
            controller.setEmpreinteCarboneController(this);

            Stage stage = new Stage();
            stage.setTitle("Choix Hébergement");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void allerVersTransport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/choix_transport.fxml")); // chemin correct
            Parent root = loader.load();

            MoyenTransportController controller = loader.getController();
            controller.setEmpreinteCarboneController(this);

            Stage stage = new Stage();
            stage.setTitle("Choix Transport");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void calculerEmpreinte() {
        if (hebergementSelectionne == null || transportSelectionne == null) {
            resultatLabel.setText("Veuillez d'abord choisir un hébergement et un transport.");
            return;
        }

        double total = hebergementSelectionne.getEmissionParNuit() * nuits +
                transportSelectionne.getFacteurEmission() * distance;

        resultatLabel.setText(String.format("Empreinte totale : %.2f KgCO2", total));
    }

    public void setHebergementEtNuits(Hebergement h, int nuits) {
        this.hebergementSelectionne = h;
        this.nuits = nuits;
        updateAffichage();
    }

    public void setTransportEtDistance(MoyenTransport t, float distance) {
        this.transportSelectionne = t;
        this.distance = distance;
        updateAffichage();
    }
}
