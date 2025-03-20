package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.AvisClient;
import edu.ezip.ing1.pds.business.dto.AvisClients;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.AvisClientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class GestionAvisClients extends JFrame {

    private DefaultTableModel tableModel;
    private JTable tableAvis;
    private final AvisClientService avisClientService;

    public GestionAvisClients(NetworkConfig networkConfig) {
        setTitle("Gestion des Avis Clients");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        avisClientService = new AvisClientService(networkConfig);

        // Barre de menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFichier = new JMenu("Fichier");
        JMenuItem menuQuitter = new JMenuItem("Quitter");
        menuQuitter.addActionListener(e -> dispose());
        menuFichier.add(menuQuitter);
        menuBar.add(menuFichier);
        setJMenuBar(menuBar);

        // En-tête
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(new Color(70, 130, 180));
        JLabel title = new JLabel("Gestion des Avis Clients", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panelTop.add(title, BorderLayout.CENTER);
        add(panelTop, BorderLayout.NORTH);


        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Note", "Date", "Commentaire", "ID Client"});
        tableAvis = new JTable(tableModel);
        tableAvis.setRowHeight(25);
        add(new JScrollPane(tableAvis), BorderLayout.CENTER);


        JPanel panelButtons = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton btnAjouter = new JButton("Ajouter Avis");
        JButton btnModifier = new JButton("Modifier Avis");
        JButton btnSupprimer = new JButton("Supprimer Avis");
        JButton btnActualiser = new JButton("Actualiser Liste");

        panelButtons.add(btnAjouter);
        panelButtons.add(btnModifier);
        panelButtons.add(btnSupprimer);
        panelButtons.add(btnActualiser);
        add(panelButtons, BorderLayout.SOUTH);


        btnAjouter.addActionListener(this::ajouterAvis);
        btnModifier.addActionListener(this::modifierAvis);
        btnSupprimer.addActionListener(this::supprimerAvis);
        btnActualiser.addActionListener(e -> actualiserListe());

        actualiserListe();
        setVisible(true);
    }

    private void ajouterAvis(ActionEvent e) {
        try {
            String[] notes = {"1", "2", "3", "4", "5"};
            String note = (String) JOptionPane.showInputDialog(this, "Note:", "Sélectionner la note",
                    JOptionPane.QUESTION_MESSAGE, null, notes, notes[0]);
            if (note == null) return;

            String date = JOptionPane.showInputDialog(this, "Date (YYYY-MM-DD) :");
            String commentaire = JOptionPane.showInputDialog(this, "Commentaire :");
            String idClient = JOptionPane.showInputDialog(this, "ID Client :");

            avisClientService.insertAvis(new AvisClient(0, Integer.parseInt(note), date, commentaire, Integer.parseInt(idClient)));
            JOptionPane.showMessageDialog(this, "Avis ajouté avec succès !");
            actualiserListe();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierAvis(ActionEvent e) {
        int selectedRow = tableAvis.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un avis à modifier.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idAvis = (int) tableModel.getValueAt(selectedRow, 0);
        String note = (String) JOptionPane.showInputDialog(this, "Nouvelle note :", "Modifier la note",
                JOptionPane.QUESTION_MESSAGE, null, new String[]{"1", "2", "3", "4", "5"}, tableModel.getValueAt(selectedRow, 1));
        if (note == null) return;

        String date = JOptionPane.showInputDialog(this, "Nouvelle date (YYYY-MM-DD) :", tableModel.getValueAt(selectedRow, 2));
        String commentaire = JOptionPane.showInputDialog(this, "Nouveau commentaire :", tableModel.getValueAt(selectedRow, 3));

        if (date != null && commentaire != null) {
            try {
                avisClientService.updateAvis(new AvisClient(idAvis, Integer.parseInt(note), date, commentaire, 0));
                JOptionPane.showMessageDialog(this, "Avis modifié avec succès !");
                actualiserListe();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerAvis(ActionEvent e) {
        int selectedRow = tableAvis.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un avis à supprimer.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idAvis = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cet avis ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                avisClientService.deleteAvis(new AvisClient(idAvis, 0, "", "", 0));
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            JOptionPane.showMessageDialog(this, "Avis supprimé avec succès !");
            actualiserListe();
        }
    }

    private void actualiserListe() {
        tableModel.setRowCount(0);
        try {
            AvisClients avisClients = avisClientService.selectAvisClients();
            for (AvisClient avis : avisClients.getAvisClients()) {
                tableModel.addRow(new Object[]{avis.getIdAvis(), avis.getNote(), avis.getDateAvis(), avis.getCommentaires(), avis.getIdClient()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de rafraîchissement : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
