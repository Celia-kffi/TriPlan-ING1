package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.Clients;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class MainFrontEnd extends JFrame {

    private static final String LOGGING_LABEL = "FrontEnd";
    private static final Logger logger = LoggerFactory.getLogger(LOGGING_LABEL);
    private static final String NETWORK_CONFIG_FILE = "network.yaml";
    private static final Deque<ClientRequest> clientRequests = new ArrayDeque<>();
    private final NetworkConfig networkConfig;

    public static void main(String[] args) {
        try {
            final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, NETWORK_CONFIG_FILE);
            logger.debug("Chargement du fichier de configuration réseau : {}", networkConfig);

            final ClientService clientService = new ClientService(networkConfig);
            Clients clients = clientService.selectClients();

            new MainFrontEnd(networkConfig);
        } catch (IOException | InterruptedException e) {
            logger.error("Erreur lors du chargement de la configuration ou du service client", e);
        }
    }

    public MainFrontEnd(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
        setTitle("TriPlan - Agence de Voyage");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 🔹 MenuBar en haut
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFichier = new JMenu("Fichier");
        JMenuItem menuQuitter = new JMenuItem("Quitter");
        menuQuitter.addActionListener(e -> System.exit(0));
        menuFichier.add(menuQuitter);
        menuBar.add(menuFichier);
        setJMenuBar(menuBar);

        // 🔹 Logo centré en haut
        JPanel panelTop = new JPanel(new BorderLayout());
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panelTop.add(logoLabel, BorderLayout.CENTER);
        add(panelTop, BorderLayout.NORTH);

        // 🔹 Message de bienvenue
        JLabel welcomeLabel = new JLabel("🌍 Découvrez le monde avec TriPlan ! ✈️", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(0, 102, 204)); // Bleu
        add(welcomeLabel, BorderLayout.CENTER);

        // 🔹 Panneau des boutons (Aligné sur le côté gauche)
        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(4, 1, 15, 15));
        panelButtons.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));

        JButton boutonClient = createStyledButton("Espace Client");
        JButton boutonAvis = createStyledButton("Avis Clients");
        JButton boutonEmpreinte = createStyledButton("Empreinte Carbone");
        JButton boutonQuitter = createStyledButton("Quitter", Color.RED, new Color(180, 0, 0));

        panelButtons.add(boutonClient);
        panelButtons.add(boutonAvis);
        panelButtons.add(boutonEmpreinte);
        panelButtons.add(boutonQuitter);
        add(panelButtons, BorderLayout.WEST);

        // 🔹 Actions des boutons
        boutonClient.addActionListener(e -> {
            try {
                new GestionClients().setVisible(true);
            } catch (IOException ex) {
                logger.error("Erreur lors de l'ouverture de l'espace client", ex);
                JOptionPane.showMessageDialog(this, "Erreur lors de l'accès à l'espace client", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        boutonAvis.addActionListener(e ->
                new GestionAvisClients(networkConfig)
        );
        boutonEmpreinte.addActionListener(e -> new EmpreinteCarboneGUI());
        boutonQuitter.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    // 🔹 Création d'un bouton stylisé
    private JButton createStyledButton(String text) {
        return createStyledButton(text, new Color(0, 102, 204), new Color(0, 76, 153));
    }

    // 🔹 Création d'un bouton stylisé avec couleur personnalisée
    private JButton createStyledButton(String text, Color normalColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());

        // Effet de survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(normalColor);
            }
        });

        return button;
    }
}
