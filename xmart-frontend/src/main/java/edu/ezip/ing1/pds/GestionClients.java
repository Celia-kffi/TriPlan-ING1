package edu.ezip.ing1.pds;

import edu.ezip.commons.connectionpool.config.DatabaseConnectionBasicConfiguration;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;
import edu.ezip.ing1.pds.business.server.XMartCityService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class GestionClients extends JFrame {
    private DefaultTableModel tableModel;
    private JTable tableClients;
    private Connection connection;

    public GestionClients() {
        setTitle("TriPlan - Gestion des Clients");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        DatabaseConnectionBasicConfiguration config = DatabaseConnectionBasicConfiguration.getInstance();
        try {
            String url = "jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "/" + config.getDatabaseName();
            connection = DriverManager.getConnection(url, config.getUsername(), config.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Connexion à la base échouée : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        JLabel title = new JLabel("TriPlan", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel panelCenter = new JPanel(new BorderLayout());
        JLabel lblListe = new JLabel("Liste des clients :");
        lblListe.setFont(new Font("Serif", Font.BOLD, 16));
        panelCenter.add(lblListe, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"Nom", "Prénom", "Age", "Nationalité", "Budget"});
        tableClients = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableClients);
        panelCenter.add(scrollPane, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.Y_AXIS));

        JButton btnAjouter = new JButton("Ajouter un client");
        JButton btnSupprimer = new JButton("Supprimer un client");
        JButton btnActualiser = new JButton("Actualiser la liste");

        btnAjouter.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSupprimer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnActualiser.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(btnAjouter);
        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(btnSupprimer);
        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(btnActualiser);
        panelButtons.add(Box.createVerticalStrut(10));

        add(panelButtons, BorderLayout.WEST);

        btnAjouter.addActionListener(this::ajouterClient);
        btnSupprimer.addActionListener(this::supprimerClient);
        btnActualiser.addActionListener(this::actualiserListe);

        actualiserListe(null);
    }

    private void ajouterClient(ActionEvent e) {
        String nom = JOptionPane.showInputDialog(this, "Nom :");
        String prenom = JOptionPane.showInputDialog(this, "Prénom :");
        String age = JOptionPane.showInputDialog(this, "Age :");
        String nationalite = JOptionPane.showInputDialog(this, "Nationalité :");
        String budget = JOptionPane.showInputDialog(this, "Budget :");

        if (nom != null && prenom != null && age != null && nationalite != null && budget != null) {
            try {
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO clients (nom, prenom, age, nationalite, budget) VALUES (?, ?, ?, ?, ?)");
                stmt.setString(1, nom);
                stmt.setString(2, prenom);
                stmt.setInt(3, Integer.parseInt(age));
                stmt.setString(4, nationalite);
                stmt.setDouble(5, Double.parseDouble(budget));
                stmt.executeUpdate();
                actualiserListe(null);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du client.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerClient(ActionEvent e) {
        int selectedRow = tableClients.getSelectedRow();
        if (selectedRow != -1) {
            String nom = (String) tableModel.getValueAt(selectedRow, 0);
            String prenom = (String) tableModel.getValueAt(selectedRow, 1);
            try {
                PreparedStatement stmt = connection.prepareStatement("DELETE FROM clients WHERE nom = ? AND prenom = ?");
                stmt.setString(1, nom);
                stmt.setString(2, prenom);
                stmt.executeUpdate();
                tableModel.removeRow(selectedRow);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du client.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actualiserListe(ActionEvent e) {
        tableModel.setRowCount(0);
        try {
            Statement stmt = connection.createStatement();
            ResultSet res = stmt.executeQuery("SELECT nom, prenom, age, nationalite, budget FROM clients");
            while (res.next()) {
                tableModel.addRow(new Object[]{
                        res.getString("nom"),
                        res.getString("prenom"),
                        res.getInt("age"),
                        res.getString("nationalite"),
                        res.getDouble("budget")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des clients.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GestionClients gestionClients = new GestionClients();
            gestionClients.setVisible(true);
        });
    }
}
