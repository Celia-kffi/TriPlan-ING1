package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.EmpreinteCarbone;
import edu.ezip.ing1.pds.services.EmpreinteCarboneService;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class EmpreinteCarboneGUI extends JFrame {
    private JComboBox<String> transportComboBox;
    private JTextField distanceField;
    private JLabel resultLabel;
    private DefaultTableModel tableModel;
    private JTable empreinteTable;

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
        setTitle("Calculateur Empreinte Carbone");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        Color backgroundColor = new Color(235, 229, 210);
        getContentPane().setBackground(backgroundColor);

        try {
            final String networkConfigFile = "network.yaml";
            final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, networkConfigFile);
            empreinteCarboneService = new EmpreinteCarboneService(networkConfig);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de configuration réseau : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        JLabel title = new JLabel("Calculateur Empreinte Carbone", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel panelCenter = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Type de transport :"));
        transportComboBox = new JComboBox<>(emissionFactors.keySet().toArray(new String[0]));
        formPanel.add(transportComboBox);

        formPanel.add(new JLabel("Distance (km) :"));
        distanceField = new JTextField();
        distanceField.setBackground(Color.WHITE);
        distanceField.setForeground(Color.BLACK);
        //distanceField.setBorder(BorderFactory.createRoundedBorder(10));
        formPanel.add(distanceField);

        JButton calculateButton = new JButton("Calculer");
        calculateButton.setBackground(new Color(243,156,18));
        calculateButton.addActionListener(this::calculateEmpreinte);
        formPanel.add(calculateButton);

        JButton addButton = new JButton("Ajouter");
        addButton.setBackground(new Color(39,174,96));
        addButton.addActionListener(this::ajouterEmpreinteCarbone);
        formPanel.add(addButton);

        panelCenter.add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"Transport", "Distance (km)", "Empreinte (kgCO2)"});
        empreinteTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(empreinteTable);
        panelCenter.add(scrollPane, BorderLayout.CENTER);

        add(panelCenter, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JPanel southPanel = new JPanel(new BorderLayout());

        /*
        JButton deleteButton = new JButton("Supprimer");
        deleteButton.addActionListener(this::supprimerEmpreinte);
        bottomPanel.add(deleteButton);

        JButton updateButton = new JButton("Modifier");
        updateButton.addActionListener(this::modifierEmpreinte);
        bottomPanel.add(updateButton);

        JButton refreshButton = new JButton("Actualiser");
        bottomPanel.add(refreshButton);
        */

        resultLabel = new JLabel("Empreinte carbone : 0.00 kgCO2", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Serif", Font.BOLD, 16));

        southPanel.add(bottomPanel, BorderLayout.NORTH);
        southPanel.add(resultLabel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void calculateEmpreinte(ActionEvent e) {
        try {
            String transport = (String) transportComboBox.getSelectedItem();
            double distance = Double.parseDouble(distanceField.getText());
            double facteurEmission = emissionFactors.getOrDefault(transport, 0.0);

            double empreinte = distance * facteurEmission * 2;
            resultLabel.setText(String.format("Empreinte carbone : %.2f kgCO2", empreinte));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une distance valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterEmpreinteCarbone(ActionEvent e) {
        try {
            String transport = (String) transportComboBox.getSelectedItem();
            double distance = Double.parseDouble(distanceField.getText());
            double facteurEmission = emissionFactors.get(transport);

            double empreinte = distance * facteurEmission * 2;

            EmpreinteCarbone empreinteCarbone = new EmpreinteCarbone();
            empreinteCarbone.setEmpreinteKgCO2(empreinte);

            empreinteCarboneService.insertEmpreinteCarbonePublic(empreinteCarbone);

            tableModel.addRow(new Object[]{transport, distance, String.format("%.2f", empreinte)});

            JOptionPane.showMessageDialog(this, "Empreinte carbone ajoutée avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    /*
      private void supprimerEmpreinte(ActionEvent e) {
          int selectedRow = empreinteTable.getSelectedRow();

          if (selectedRow == -1) {
              JOptionPane.showMessageDialog(this, "Veuillez sélectionner une ligne.", "Avertissement", JOptionPane.WARNING_MESSAGE);
              return;
          }

          try {
              Object value = tableModel.getValueAt(selectedRow, 1);
              if (value == null) {
                  JOptionPane.showMessageDialog(this, "Valeur de l'empreinte carbone introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
                  return;
              }

              double empreinteCarbone = Double.parseDouble(value.toString());
              System.out.println("Empreinte carbone récupérée : " + empreinteCarbone);
              EmpreinteCarbone empreinte = new EmpreinteCarbone();
              empreinte.setEmpreinteKgCO2(empreinteCarbone);

              empreinteCarboneService.deleteEmpreinte(empreinte);

              tableModel.removeRow(selectedRow);

              JOptionPane.showMessageDialog(this, "Empreinte supprimée avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
          } catch (NumberFormatException ex) {
              JOptionPane.showMessageDialog(this, "Format incorrect pour l'empreinte carbone.", "Erreur", JOptionPane.ERROR_MESSAGE);
          } catch (Exception ex) {
              ex.printStackTrace();
              JOptionPane.showMessageDialog(this, "Erreur lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
          }
      }




         private void actualiserListeEmpreintes(ActionEvent e) {
          tableModel.setRowCount(0);
          try {
              EmpreintesCarbone empreintes = empreinteCarboneService.selectEmpreintesCarbone();
              if (empreintes != null ) {
                  for (EmpreinteCarbone empreinte : empreintes.getEmpreintesCarbone()) {
                      tableModel.addRow(new Object[]{
                              empreinte.getIdEmpreinte(),
                              empreintes.getEmpreintesCarbone(),

                      });
                  }
              }
          } catch (Exception ex) {
              ex.printStackTrace();
              JOptionPane.showMessageDialog(this, "Erreur de rafraîchissement : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
          }}
      private void modifierEmpreinte(ActionEvent e) {
          int selectedRow = empreinteTable.getSelectedRow();
          if (selectedRow != -1) {
              int idEmpreinte = (int) tableModel.getValueAt(selectedRow, 0);
              Double  empreintekgC02= (double) tableModel.getValueAt(selectedRow, 1);

              if (idEmpreinte != 0 && empreintekgC02!=null) {
                  try {
                      EmpreinteCarbone empreinte = new EmpreinteCarbone(idEmpreinte,empreintekgC02);
                      empreinteCarboneService.updateEmpreinte(empreinte);
                      JOptionPane.showMessageDialog(this, "Client modifié avec succès !");
                      //actualiserListeEmpreintes(null);
                  } catch (Exception ex) {
                      ex.printStackTrace();
                      JOptionPane.showMessageDialog(this, "Erreur lors de la modification : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                  }
              }
          }
      }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmpreinteCarboneGUI::new);
    }*/
}
