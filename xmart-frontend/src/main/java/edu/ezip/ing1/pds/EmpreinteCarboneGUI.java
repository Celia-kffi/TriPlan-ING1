package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.EmpreinteCarbone;
import edu.ezip.ing1.pds.services.EmpreinteCarboneService;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class EmpreinteCarboneGUI {
    private JFrame frame;
    private JComboBox<String> transportComboBox;
    private JTextField distanceField;
    private JLabel resultLabel;

    private EmpreinteCarboneService empreinteCarboneService;

    private final Map<String, Double> emissionFactors = new HashMap<String, Double>() {{
        put("Avion", 0.22);
        put("Voiture", 0.20);
        put("TGV", 0.00);
        put("Intercité", 0.01);
        put("TER", 0.01);
        put("Bateau", 0.27);
        put("Vélo", 0.00);
        put("Pieds", 0.00);
    }};

    public EmpreinteCarboneGUI() {
        try {
            final String networkConfigFile = "network.yaml";
            final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, networkConfigFile);
            empreinteCarboneService = new EmpreinteCarboneService(networkConfig);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur de configuration réseau : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        frame = new JFrame("Calculateur Empreinte Carbone");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(5, 2, 10, 10));

        JLabel transportLabel = new JLabel("Type de transport :");
        transportComboBox = new JComboBox<>(emissionFactors.keySet().toArray(new String[0]));

        JLabel distanceLabel = new JLabel("Distance (km) :");
        distanceField = new JTextField();

        JButton calculateButton = new JButton("Calculer");
        JButton addButton = new JButton("Ajouter");

        resultLabel = new JLabel("Empreinte carbone : ");

        calculateButton.addActionListener(e -> calculateEmpreinte());
        addButton.addActionListener(this::ajouterEmpreinteCarbone);

        frame.add(transportLabel);
        frame.add(transportComboBox);
        frame.add(distanceLabel);
        frame.add(distanceField);
        frame.add(new JLabel());
        frame.add(calculateButton);
        frame.add(new JLabel());
        frame.add(addButton);
        frame.add(new JLabel());
        frame.add(resultLabel);

        frame.setVisible(true);
    }

    private void calculateEmpreinte() {
        try {
            String transport = (String) transportComboBox.getSelectedItem();
            double distance = Double.parseDouble(distanceField.getText());
            double facteurEmission = emissionFactors.get(transport);
            double empreinte = distance * facteurEmission * 2;

            resultLabel.setText(String.format("Empreinte carbone : %.2f kgCO2", empreinte));
        } catch (NumberFormatException ex) {
            resultLabel.setText("Veuillez entrer une distance valide.");
        }
    }

    private void ajouterEmpreinteCarbone(ActionEvent e) {
        String distanceText = distanceField.getText();
        String transport = (String) transportComboBox.getSelectedItem();

        if (distanceText != null && !distanceText.isEmpty() && transport != null) {
            try {
                double distance = Double.parseDouble(distanceText);
                double facteurEmission = emissionFactors.get(transport);
                double empreinte = distance * facteurEmission * 2;

                EmpreinteCarbone empreinteCarbone = new EmpreinteCarbone();
                empreinteCarbone.setEmpreinteKgCO2((int) empreinte);  // Convertir en int
                empreinteCarbone.setTypeDeTransport(transport);
                empreinteCarbone.setFacteurEmission(facteurEmission);
                empreinteCarbone.setDistance(distance);

                empreinteCarboneService.insertEmpreinteCarbone(empreinteCarbone);

                JOptionPane.showMessageDialog(frame, "Empreinte carbone ajoutée avec succès !");
                resultLabel.setText(String.format("Empreinte carbone : %.2f kgCO2", empreinte));

            } catch (NumberFormatException ex) {
                resultLabel.setText("Veuillez entrer une distance valide.");
                JOptionPane.showMessageDialog(frame, "Erreur : Distance invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erreur inattendue : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new EmpreinteCarboneGUI();
    }
}
