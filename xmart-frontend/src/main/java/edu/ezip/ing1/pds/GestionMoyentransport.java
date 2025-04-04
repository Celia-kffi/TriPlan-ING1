package edu.ezip.ing1.pds;

import edu.ezip.commons.connectionpool.config.DatabaseConnectionBasicConfiguration;
import edu.ezip.ing1.pds.business.dto.Client;
import edu.ezip.ing1.pds.business.dto.Clients;
import edu.ezip.ing1.pds.business.dto.MoyenTransport;
import edu.ezip.ing1.pds.business.dto.MoyenTransports;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.ClientService;
import edu.ezip.ing1.pds.services.MoyenTransportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

import static com.mysql.cj.conf.PropertyKey.logger;


public class GestionMoyentransport extends JFrame {


    private DefaultTableModel tableModel;
    private JTable tableClients;
    private MoyenTransportService MoyenTransportService;

    public GestionMoyentransport() throws IOException {
        setTitle("TriPlan - Gestion des moyens de transport");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color backgroundColor = new Color(235, 229, 210);
        getContentPane().setBackground(backgroundColor);

        try {
            final String networkConfigFile = "network.yaml";
            final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, networkConfigFile);
            MoyenTransportService = new MoyenTransportService(networkConfig);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de configuration réseau : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        JLabel title = new JLabel("TriPlan", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 25));
        add(title, BorderLayout.NORTH);

        JPanel panelCenter = new JPanel(new BorderLayout());
        JLabel lblListe = new JLabel("Liste des moyens de transport :");
        lblListe.setFont(new Font("Serif", Font.BOLD, 16));
        lblListe.setForeground(new Color(40, 40, 100));
        lblListe.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelCenter.add(lblListe, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"ID transport", "type de transport", "facteur de transport"});
        tableClients = new JTable(tableModel);
        tableClients.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableClients.getTableHeader().setBackground(new Color(220, 220, 220));
        tableClients.setBackground(Color.WHITE);
        tableClients.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(tableClients);
        panelCenter.add(scrollPane, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.Y_AXIS));
        panelButtons.setBackground(backgroundColor);
        panelButtons.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnAjouter = new JButton("Ajouter un moyen de transport");
        JButton btnSupprimer = new JButton("Supprimer un moyen de transport");
        JButton btnActualiser = new JButton("Actualiser la liste");
        JButton btnModifier = new JButton("Modifier un moyen de trasnport");

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

        btnAjouter.addActionListener(this::ajouterClient);
        btnSupprimer.addActionListener(this::supprimerClient);
        btnActualiser.addActionListener(this::actualiserListe);
        btnModifier.addActionListener(this::modifierClient);

        actualiserListe(null);
    }

    private void ajouterClient(ActionEvent e) {
        String id = JOptionPane.showInputDialog(this, "id moyen de transport  :");
        String type = JOptionPane.showInputDialog(this, "Type de transport :");
        String facteurStr = JOptionPane.showInputDialog(this, "Facteur de transport :");


        if (id != null && type != null && facteurStr != null) {
            try {
                double facteur = Double.parseDouble(facteurStr);
                MoyenTransport transport = new MoyenTransport(id, type, facteur);
                MoyenTransportService.insertTransport(transport);
                JOptionPane.showMessageDialog(this, "Client ajouté avec succès !");
                //actualiserListe(null);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du client : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerClient(ActionEvent e) {
        int selectedRow = tableClients.getSelectedRow();
        if (selectedRow != -1)
            try {
                String idTransport = tableModel.getValueAt(selectedRow, 0).toString();
                MoyenTransport transport = new MoyenTransport(idTransport, "", 0.0);
                MoyenTransportService.deleteTransport(transport);
                //actualiserListe(null);
                JOptionPane.showMessageDialog(this, "Client supprimé avec succès !");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
    }




    private void actualiserListe(ActionEvent e) {
        tableModel.setRowCount(0);
        try {
            MoyenTransports transports  = MoyenTransportService.selectTransport();
            if (transports != null) {
                for (MoyenTransport transport : transports.getMoyenTransports()) {
                    tableModel.addRow(new Object[]{
                            transport.getIdMoyenDestination(),
                            transport.getTypeTransports(),
                            transport.getFacteurEmission(),

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
            //int idClient = (int) tableModel.getValueAt(selectedRow, 0);
            String id_moyen = (String) tableModel.getValueAt(selectedRow, 0);
            String type = (String) tableModel.getValueAt(selectedRow, 1);
            String facteur = JOptionPane.showInputDialog(selectedRow, 2);
            String facteurStr = (String) tableModel.getValueAt(selectedRow, 2);
           /* String nationalite = JOptionPane.showInputDialog(this, "Nouvelle nationalité :", tableModel.getValueAt(selectedRow, 4));
            String budget = JOptionPane.showInputDialog(this, "Nouveau budget :", tableModel.getValueAt(selectedRow, 5));
            String idPaiement = JOptionPane.showInputDialog(this, "Nouvel ID Paiement :", tableModel.getValueAt(selectedRow, 6));

*/
            if (id_moyen != null && type != null && facteur != null) {
                try {
                    MoyenTransport transport = new MoyenTransport(id_moyen, type, Double.parseDouble(facteur));
                    MoyenTransportService.updateTransport(transport);
                    JOptionPane.showMessageDialog(this, "Client modifié avec succès !");
                   // actualiserListe(null);
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
                new GestionMoyentransport().setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erreur au lancement de l'application : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
