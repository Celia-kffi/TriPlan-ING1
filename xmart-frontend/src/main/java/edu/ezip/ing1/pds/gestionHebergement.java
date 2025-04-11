package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.Hebergement;
import edu.ezip.ing1.pds.business.dto.Hebergements;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.HebergementService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class gestionHebergement extends JFrame {

    private DefaultTableModel tableModel;
    private JTable tableHebergement;
    private HebergementService hebergementService;

    public gestionHebergement() throws IOException {
        setTitle("TriPlan - Gestion des Hebergement");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color backgroundColor = new Color(235, 229, 210);
        getContentPane().setBackground(backgroundColor);

        try {
            final String networkConfigFile = "network.yaml";
            final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, networkConfigFile);
            hebergementService = new HebergementService(networkConfig);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de configuration réseau : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        JLabel title = new JLabel("TriPlan", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 25));
        add(title, BorderLayout.NORTH);

        JPanel panelCenter = new JPanel(new BorderLayout());
        JLabel lblListe = new JLabel("Liste des hebergements :");
        lblListe.setFont(new Font("Serif", Font.BOLD, 16));
        lblListe.setForeground(new Color(40, 40, 100));
        lblListe.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelCenter.add(lblListe, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"ID Heberegement","Prix par nuit", "Nom", "Type"});
        tableHebergement = new JTable(tableModel);
        tableHebergement.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableHebergement.getTableHeader().setBackground(new Color(220, 220, 220));
        tableHebergement.setBackground(Color.WHITE);
        tableHebergement.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(tableHebergement);
        panelCenter.add(scrollPane, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.Y_AXIS));
        panelButtons.setBackground(backgroundColor);
        panelButtons.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnAjouter = new JButton("Ajouter un hebergement");
        JButton btnSupprimer = new JButton("Supprimer un hebergement");
        JButton btnActualiser = new JButton("Actualiser la liste");
        JButton btnModifier = new JButton("Modifier un hebergement");

        btnAjouter.setBackground(new Color(39, 174, 96));
        btnAjouter.setForeground(Color.WHITE);

        btnSupprimer.setBackground(new Color(192, 57, 43));
        btnSupprimer.setForeground(Color.WHITE);

        btnModifier.setBackground(new Color(243, 156, 18));
        btnModifier.setForeground(Color.WHITE);

        btnActualiser.setBackground(new Color(41, 128, 185));
        btnActualiser.setForeground(Color.WHITE);

        btnAjouter.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSupprimer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnActualiser.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnModifier.setAlignmentX(Component.CENTER_ALIGNMENT);


        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(btnAjouter);
        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(btnSupprimer);
        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(btnModifier);
        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(btnActualiser);
        panelButtons.add(Box.createVerticalStrut(10));

        add(panelButtons, BorderLayout.WEST);

        btnAjouter.addActionListener(this::ajouterHebergement);
        btnSupprimer.addActionListener(this::supprimerHebergement);
        btnActualiser.addActionListener(this::actualiserListe);
        btnModifier.addActionListener(this::modifierHebergement);

        actualiserListe(null);
    }

    private void ajouterHebergement(ActionEvent e) {
        String idStr = JOptionPane.showInputDialog(this, "id :");
        String prixStr = JOptionPane.showInputDialog(this, "Prix :");
        String nom = JOptionPane.showInputDialog(this, "Nom :");
        String type = JOptionPane.showInputDialog(this, "Type :");

        if (idStr != null && prixStr != null && nom != null && type != null) {
            try {
                int id = Integer.parseInt(idStr);
                int prix = Integer.parseInt(prixStr);

                Hebergement hebergement = new Hebergement(id, prix, nom, type);
                hebergementService.insertHebergement(hebergement);
                JOptionPane.showMessageDialog(this, "Hebergement ajouté avec succès !");
                actualiserListe(null);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "L'ID et le prix doivent être des nombres entiers.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'hébergement : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void supprimerHebergement(ActionEvent e) {
        int selectedRow = tableHebergement.getSelectedRow();
        if (selectedRow != -1) {
            int idhebergement = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());



            try {
                Hebergement hebergement = new Hebergement(idhebergement, 0, "","");
                hebergementService.deleteHebergement(hebergement);
                actualiserListe(null);
                JOptionPane.showMessageDialog(this, "Heberegement supprimé avec succès !");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actualiserListe(ActionEvent e) {
        tableModel.setRowCount(0);
        try {
            Hebergements hebergements = hebergementService.selectHebergement();
            if (hebergements != null) {
                for (Hebergement hebergement : hebergements.getHebergements()) {
                    tableModel.addRow(new Object[]{
                            hebergement.getIdHebergement(),
                            hebergement.getPrixNuit(),
                            hebergement.getNomH(),
                            hebergement.getType(),

                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de rafraîchissement : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void modifierHebergement(ActionEvent e) {
        int selectedRow = tableHebergement.getSelectedRow();
        if (selectedRow != -1) {
            int idHebergement = (int) tableModel.getValueAt(selectedRow, 0);
            int prix = (int) tableModel.getValueAt(selectedRow, 1);
            String nomH = (String) tableModel.getValueAt(selectedRow, 2);
            String type = JOptionPane.showInputDialog(this, "Nouvel type :", tableModel.getValueAt(selectedRow, 3));


            if ( nomH!= null &  type != null) {
                try {
                    Hebergement hebergement = new Hebergement(idHebergement,prix, nomH, type);
                    hebergementService.updateHebergement(hebergement);
                    JOptionPane.showMessageDialog(this, "hebergeement modifié avec succès !");
                    actualiserListe(null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new gestionHebergement().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erreur au lancement de l'application : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
}}