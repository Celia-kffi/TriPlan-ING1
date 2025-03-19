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
import java.util.regex.Pattern;

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

        JPanel panelTop = new JPanel();
        panelTop.setBackground(new Color(70, 130, 180));
        JLabel title = new JLabel("Gestion des Avis Clients", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panelTop.add(title);
        add(panelTop, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Note", "Date", "Commentaire", "ID Client"});
        tableAvis = new JTable(tableModel);
        tableAvis.setRowHeight(25);
        add(new JScrollPane(tableAvis), BorderLayout.CENTER);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(1, 4, 10, 10));

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
            int note = demanderEntier("Note (1-5) :", 1, 5);
            String date = demanderDate("Date (YYYY-MM-DD) :");
            String commentaire = demanderTexte("Commentaire :");
            int idClient = demanderEntier("ID Client :", 1, Integer.MAX_VALUE);

            avisClientService.insertAvis(new AvisClient(0, note, date, commentaire, idClient));
            JOptionPane.showMessageDialog(this, "Avis ajouté avec succès !");
            actualiserListe();
        } catch (Exception ex) {
            afficherErreur("Erreur lors de l'ajout de l'avis : " + ex.getMessage());
        }
    }

    private void modifierAvis(ActionEvent e) {
        int selectedRow = tableAvis.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un avis à modifier.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idAvis = (int) tableModel.getValueAt(selectedRow, 0);
        String note = JOptionPane.showInputDialog(this, "Nouvelle note (1-5) :", tableModel.getValueAt(selectedRow, 1));
        String date = JOptionPane.showInputDialog(this, "Nouvelle date (YYYY-MM-DD) :", tableModel.getValueAt(selectedRow, 2));
        String commentaire = JOptionPane.showInputDialog(this, "Nouveau commentaire :", tableModel.getValueAt(selectedRow, 3));

        if (note != null && date != null && commentaire != null) {
            try {
                avisClientService.updateAvis(new AvisClient(idAvis, Integer.parseInt(note), date, commentaire, 0));
                JOptionPane.showMessageDialog(this, "Avis modifié avec succès !");
                actualiserListe();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification de l'avis : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void supprimerAvis(ActionEvent e) {
        int selectedRow = tableAvis.getSelectedRow();
        if (selectedRow == -1) {
            afficherErreur("Veuillez sélectionner un avis à supprimer.");
            return;
        }

        int idAvis = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cet avis ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                AvisClient avisClient = new AvisClient(idAvis, 0, "", "", 0);
                avisClientService.deleteAvis(avisClient);
                JOptionPane.showMessageDialog(this, "Avis supprimé avec succès !");
                actualiserListe();
            } catch (Exception ex) {
                afficherErreur("Erreur lors de la suppression de l'avis : " + ex.getMessage());
            }
        }
    }

    private void actualiserListe() {
        tableModel.setRowCount(0);
        try {
            AvisClients avisClients = avisClientService.selectAvisClients();
            if (avisClients != null && avisClients.getAvisClients() != null) {
                for (AvisClient avis : avisClients.getAvisClients()) {
                    tableModel.addRow(new Object[]{avis.getIdAvis(), avis.getNote(), avis.getDateAvis(), avis.getCommentaires(), avis.getIdClient()});
                }
            } else {
                JOptionPane.showMessageDialog(this, "Aucun avis trouvé.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            afficherErreur("Erreur de rafraîchissement : " + ex.getMessage());
        }
    }

    private int demanderEntier(String message, int min, int max) {
        while (true) {
            String input = JOptionPane.showInputDialog(this, message);
            if (input == null) return min; // Permet d'annuler
            try {
                int valeur = Integer.parseInt(input);
                if (valeur < min || valeur > max) throw new NumberFormatException();
                return valeur;
            } catch (NumberFormatException ex) {
                afficherErreur("Veuillez entrer un nombre valide entre " + min + " et " + max + ".");
            }
        }
    }

    private String demanderDate(String message) {
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        while (true) {
            String input = JOptionPane.showInputDialog(this, message);
            if (input == null) return "0000-00-00";
            if (pattern.matcher(input).matches()) return input;
            afficherErreur("Veuillez entrer une date au format YYYY-MM-DD.");
        }
    }

    private String demanderTexte(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(this, message);
            if (input != null && !input.trim().isEmpty()) return input;
            afficherErreur("Ce champ ne peut pas être vide.");
        }
    }

    private void afficherErreur(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
