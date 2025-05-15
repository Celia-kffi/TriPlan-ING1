package edu.ezip.ing1.pds.requests;

import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;

public class FindClientIdByNomPrenomRequest extends ClientRequest<Object, Integer> {

    public FindClientIdByNomPrenomRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, Object info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public Integer readResult(String body) throws IOException {
        try {
            return Integer.parseInt(body.trim());
        } catch (NumberFormatException e) {
            return -1; // ou null si tu veux gérer différemment
        }
    }
}
