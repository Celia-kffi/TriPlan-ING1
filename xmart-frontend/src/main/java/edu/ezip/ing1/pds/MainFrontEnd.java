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
        setTitle("Accueil - Agence de Voyage");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Bienvenue dans notre agence de voyage", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        add(label, BorderLayout.CENTER);

        JPanel panelB = new JPanel();
        panelB.setLayout(new GridLayout(5, 1, 5, 5));

        JButton boutonClient = new JButton("Espace Client");
        JButton boutonVoyage = new JButton("Espace Voyage");
        JButton boutonAvis = new JButton("Avis Clients");
        JButton boutonEmpreinte = new JButton("Empreinte Carbone");
        JButton boutonQuitter = new JButton("Quitter");

        panelB.add(boutonClient);
        panelB.add(boutonVoyage);
        panelB.add(boutonAvis);
        panelB.add(boutonEmpreinte);
        panelB.add(boutonQuitter);
        add(panelB, BorderLayout.WEST);

        boutonClient.addActionListener(e -> {
            try {
                new GestionClients().setVisible(true);
            } catch (IOException ex) {
                logger.error("Erreur lors de l'ouverture de l'espace client", ex);
                JOptionPane.showMessageDialog(this, "Erreur lors de l'accès à l'espace client", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        boutonAvis.addActionListener(e -> GestionAvisClients.openFrame(networkConfig));

        boutonEmpreinte.addActionListener(e -> {

                new EmpreinteCarboneGUI();

        });

        boutonQuitter.addActionListener(e -> dispose());

        setVisible(true);
    }
}
