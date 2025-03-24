package edu.ezip.ing1.pds.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.EmpreintesCarbone;
import edu.ezip.ing1.pds.business.dto.EmpreinteCarbone;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectAllEmpreinteCarboneClientRequest extends ClientRequest<Object, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(SelectAllEmpreinteCarboneClientRequest.class);

    public SelectAllEmpreinteCarboneClientRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, Object info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public Integer readResult(String body) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        logger.debug("Body reçu : {}", body); // Log du body reçu

        if (body == null || body.isEmpty()) {
            logger.error("Body reçu est null ou vide.");
            return null;
        }


        EmpreintesCarbone empreintesCarbone = mapper.readValue(body, EmpreintesCarbone.class);
        logger.debug("EmpreintesCarbone désérialisées : {}", empreintesCarbone);


        if (!empreintesCarbone.getEmpreintesCarbone().isEmpty()) {
            EmpreinteCarbone premier = empreintesCarbone.getEmpreintesCarbone().iterator().next();
            return premier.getIdEmpreinte();
        }

        logger.error("Aucune empreinte carbone trouvée.");
        return null;
    }
}