package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.AvisClient;
import edu.ezip.ing1.pds.business.dto.AvisClients;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.AvisClientService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class GestionAvisClients extends JFrame {

    private DefaultTableModel tableModel;
    private JTable tableAvis;
    private AvisClientService avisClientService;

    public GestionAvisClients(NetworkConfig networkConfig) {
        setTitle("Gestion des Avis Clients");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        avisClientService = new AvisClientService(networkConfig);

        JPanel panelTop = new JPanel();
        panelTop.setBackground(new Color(70, 130, 180));
        panelTop.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel title = new JLabel("Gestion des Avis Clients", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panelTop.add(title);
        add(panelTop, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Note", "Date", "Commentaire", "ID Client"});
        tableAvis = new JTable(tableModel);
        tableAvis.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tableAvis.setRowHeight(25);
        add(new JScrollPane(tableAvis), BorderLayout.CENTER);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(1, 3, 10, 10));
        panelButtons.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnAjouter = new JButton("Ajouter Avis");
        btnAjouter.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAjouter.setIcon(new ImageIcon("icons/add.png"));
        btnAjouter.setBackground(new Color(50, 205, 50));
        btnAjouter.setForeground(Color.WHITE);

        JButton btnActualiser = new JButton("Actualiser Liste");
        btnActualiser.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnActualiser.setIcon(new ImageIcon("icons/refresh.png"));
        btnActualiser.setBackground(new Color(30, 144, 255));
        btnActualiser.setForeground(Color.WHITE);

        panelButtons.add(btnAjouter);
        panelButtons.add(btnActualiser);

        add(panelButtons, BorderLayout.SOUTH);

        btnAjouter.addActionListener(this::ajouterAvis);
        btnActualiser.addActionListener(this::actualiserListe);

        actualiserListe(null);
        setVisible(true);
    }

    private void ajouterAvis(ActionEvent e) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JTextField fieldNote = new JTextField();
        JTextField fieldDate = new JTextField();
        JTextField fieldCommentaire = new JTextField();
        JTextField fieldIdClient = new JTextField();

        panel.add(new JLabel("Note (1-5):"));
        panel.add(fieldNote);
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(fieldDate);
        panel.add(new JLabel("Commentaire:"));
        panel.add(fieldCommentaire);
        panel.add(new JLabel("ID Client:"));
        panel.add(fieldIdClient);

        int result = JOptionPane.showConfirmDialog(this, panel, "Ajouter un Avis", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                AvisClient avisClient = new AvisClient(0, Integer.parseInt(fieldNote.getText()),
                        fieldDate.getText(), fieldCommentaire.getText(), Integer.parseInt(fieldIdClient.getText()));
                avisClientService.insertAvis(avisClient);
                JOptionPane.showMessageDialog(this, "Avis ajouté avec succès !");
                actualiserListe(null);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'avis : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actualiserListe(ActionEvent e) {
        tableModel.setRowCount(0);
        try {
            AvisClients avisClients = avisClientService.selectAvisClients();
            for (AvisClient avis : avisClients.getAvisClients()) {
                tableModel.addRow(new Object[]{
                        avis.getIdAvis(),
                        avis.getNote(),
                        avis.getDateAvis(),
                        avis.getCommentaires(),
                        avis.getIdClient()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de rafraîchissement : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void openFrame(NetworkConfig networkConfig) {
        SwingUtilities.invokeLater(() -> new GestionAvisClients(networkConfig));
    }
}