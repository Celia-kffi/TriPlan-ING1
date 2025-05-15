package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.Hebergement;
import edu.ezip.ing1.pds.business.dto.MoyenTransport;
import edu.ezip.ing1.pds.business.dto.EmpreinteCarbone;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.EmpreinteCarboneService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class EmpreinteCarboneController {

    private Hebergement hebergementSelectionne;
    private MoyenTransport transportSelectionne;
    final String networkConfigFile = "network.yaml";
    final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, networkConfigFile);
    private int nuits = 0;
    private float distance = 0;
    private EmpreinteCarboneService empreinteService= new EmpreinteCarboneService(networkConfig);;
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
        labelDistance.setText("Empreinte transport : " + distance + " kgCO2");
        labelNuits.setText("Nuits : " + nuits);
    }

    @FXML
    public void allerVersHebergement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_hebergement.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_transport.fxml"));
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


    @FXML
    public void enregistrerEmpreinte() {

        if (hebergementSelectionne == null || transportSelectionne == null) {
            resultatLabel.setText("Veuillez sélectionner un hébergement et un transport.");
            return;
        }
        if (nuits <= 0 || distance <= 0) {
            resultatLabel.setText("Veuillez saisir un nombre de nuits et une distance valides.");
            return;
        }

        double total = hebergementSelectionne.getEmissionParNuit() * nuits
                + transportSelectionne.getFacteurEmission() * distance;

        EmpreinteCarbone empreinte = new EmpreinteCarbone();
        empreinte.setEmpreinteKgCO2((float) total);

        try {
            empreinteService.insertEmpreinte(empreinte);
            resultatLabel.setText("Empreinte enregistrée avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            resultatLabel.setText("Erreur lors de l'enregistrement.");
        }
    }


    public void setTransportEtDistance(MoyenTransport t, float distance) {
        this.transportSelectionne = t;
        this.distance = distance;
        updateAffichage();
    }

}
