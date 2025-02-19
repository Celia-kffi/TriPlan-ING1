package edu.ezip.ing1.pds;

import de.vandermeer.asciitable.AsciiTable;
import edu.ezip.ing1.pds.business.dto.Client;
import edu.ezip.ing1.pds.business.dto.Clients;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class MainFrontEnd {

    private final static String LoggingLabel = "FrontEnd";
    private final static Logger logger = LoggerFactory.getLogger(LoggingLabel);
    private final static String networkConfigFile = "network.yaml";
    private static final Deque<ClientRequest> clientRequests = new ArrayDeque<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, networkConfigFile);
        logger.debug("Load Network config file : {}", networkConfig.toString());

        final ClientService clientService = new ClientService(networkConfig);


        clientService.insertClients(new Client());

        Clients clients = clientService.selectClients();
        final AsciiTable asciiTable = new AsciiTable();
        for (final Client client : clients.getClients()) {
            asciiTable.addRule();
            asciiTable.addRow(client.getNom(), client.getPrenom());
        }
        asciiTable.addRule();
        logger.debug("\n{}\n", asciiTable.render());

    }
}
