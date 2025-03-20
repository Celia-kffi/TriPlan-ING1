package edu.ezip.ing1.pds;

import edu.ezip.commons.connectionpool.config.DatabaseConnectionBasicConfiguration;
import edu.ezip.ing1.pds.business.dto.Client;
import edu.ezip.ing1.pds.business.dto.Clients;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.ClientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class GestionClients extends JFrame {

    private DefaultTableModel tableModel;
    private JTable tableClients;
    private ClientService clientService;

    public GestionClients() throws IOException {
        setTitle("TriPlan - Gestion des Clients");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        try {
            final String networkConfigFile = "network.yaml";
            final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, networkConfigFile);
            clientService = new ClientService(networkConfig);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de configuration réseau : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        JLabel title = new JLabel("TriPlan", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel panelCenter = new JPanel(new BorderLayout());
        JLabel lblListe = new JLabel("Liste des clients :");
        lblListe.setFont(new Font("Serif", Font.BOLD, 16));
        panelCenter.add(lblListe, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"ID Client","Nom", "Prenom", "Age", "Nationalite", "Budget", "ID Paiement"});
        tableClients = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableClients);
        panelCenter.add(scrollPane, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.Y_AXIS));

        JButton btnAjouter = new JButton("Ajouter un client");
        JButton btnSupprimer = new JButton("Supprimer un client");
        JButton btnActualiser = new JButton("Actualiser la liste");
        JButton btnModifier = new JButton("Modifier un client");

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

        btnAjouter.addActionListener(this::ajouterClient);
        btnSupprimer.addActionListener(this::supprimerClient);
        btnActualiser.addActionListener(this::actualiserListe);
        btnModifier.addActionListener(this::modifierClient);

        actualiserListe(null);
    }

    private void ajouterClient(ActionEvent e) {
        String nom = JOptionPane.showInputDialog(this, "Nom :");
        String prenom = JOptionPane.showInputDialog(this, "Prenom :");
        String age = JOptionPane.showInputDialog(this, "Age :");
        String nationalite = JOptionPane.showInputDialog(this, "Nationalite :");
        String budget = JOptionPane.showInputDialog(this, "Budget :");
        String idPaiement = JOptionPane.showInputDialog(this, "ID Paiement :");

        if (nom != null && prenom != null && age != null && nationalite != null && budget != null) {
            try {
                Client client = new Client(nom, prenom, Integer.parseInt(age), nationalite, Double.parseDouble(budget), idPaiement);
                clientService.insertClient(client);
                JOptionPane.showMessageDialog(this, "Client ajouté avec succès !");
                actualiserListe(null);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du client : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerClient(ActionEvent e) {
        int selectedRow = tableClients.getSelectedRow();
        if (selectedRow != -1) {
            int idClient = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());



            try {
                Client client = new Client(idClient, "", "", 0, "", 0.0, "");
                clientService.deleteClient(client);
                actualiserListe(null);
                JOptionPane.showMessageDialog(this, "Client supprimé avec succès !");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actualiserListe(ActionEvent e) {
        tableModel.setRowCount(0);
        try {
            Clients clients = clientService.selectClients();
            if (clients != null) {
                for (Client client : clients.getClients()) {
                    tableModel.addRow(new Object[]{
                            client.getIdClient(),
                            client.getNom(),
                            client.getPrenom(),
                            client.getAge(),
                            client.getNationalite(),
                            client.getBudget(),
                            client.getIdPaiement()
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de rafraîchissement : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void modifierClient(ActionEvent e) {
        int selectedRow = tableClients.getSelectedRow();
        if (selectedRow != -1) {
            int idClient = (int) tableModel.getValueAt(selectedRow, 0);
            String nom = (String) tableModel.getValueAt(selectedRow, 1);
            String prenom = (String) tableModel.getValueAt(selectedRow, 2);
            String age = JOptionPane.showInputDialog(this, "Nouvel age :", tableModel.getValueAt(selectedRow, 3));
            String nationalite = JOptionPane.showInputDialog(this, "Nouvelle nationalité :", tableModel.getValueAt(selectedRow, 4));
            String budget = JOptionPane.showInputDialog(this, "Nouveau budget :", tableModel.getValueAt(selectedRow, 5));
            String idPaiement = JOptionPane.showInputDialog(this, "Nouvel ID Paiement :", tableModel.getValueAt(selectedRow, 6));


            if (age != null && nationalite != null && budget != null) {
                try {
                    Client client = new Client(idClient,nom, prenom, Integer.parseInt(age), nationalite, Double.parseDouble(budget), idPaiement);
                    clientService.updateClient(client);
                    JOptionPane.showMessageDialog(this, "Client modifié avec succès !");
                    actualiserListe(null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}