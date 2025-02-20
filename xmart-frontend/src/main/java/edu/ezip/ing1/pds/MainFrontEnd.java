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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class MainFrontEnd extends JFrame {

    private final static String LoggingLabel = "FrontEnd";
    private final static Logger logger = LoggerFactory.getLogger(LoggingLabel);
    private final static String networkConfigFile = "network.yaml";
    private static final Deque<ClientRequest> clientRequests = new ArrayDeque<ClientRequest>();




    public static void main(String[] args) throws IOException, InterruptedException {
        final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, networkConfigFile);
        logger.debug("Load Network config file : {}", networkConfig.toString());

        final ClientService clientService = new ClientService(networkConfig);
        Clients clients = clientService.selectClients();

        new MainFrontEnd();

    }


    public MainFrontEnd(){

        setTitle("Acceuil");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Bienvenue dans notre agence de voyage", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        add(label,BorderLayout.CENTER);


        JPanel panelB = new JPanel();
        panelB.setLayout(new GridLayout(5,1,5,5));

        JButton boutonC= new JButton("Espace Client");
        JButton boutonV= new JButton("Espace Voyage");
        JButton boutonA= new JButton("Avis Clients");
        JButton boutonE= new JButton("Empreinte Carbone");
        JButton boutonQ= new JButton("Quitter");

        panelB.add(boutonC);
        panelB.add(boutonV);
        panelB.add(boutonA);
        panelB.add(boutonE);
        panelB.add(boutonQ);

        add(panelB,BorderLayout.WEST);

        boutonC.addActionListener(e ->{
            try {
                new GestionClients().setVisible(true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        boutonQ.addActionListener(e -> {
               dispose();
        });

        setVisible(true);

    }

}