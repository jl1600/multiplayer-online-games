package client.gateways;

import client.gateways.IServerCommunicator;
import shared.request.Request;
import shared.response.Response;
import system.controllers.WordGameSystem;

public class ServerSocketCommunicator implements IServerCommunicator {

    private Response currentResponse;
    private final WordGameSystem server;
    private final String sessionID;


    /**
     * Constructor for ServerSocketCommunicator
     * @param system the server that it will be communicating with
     */
    public ServerSocketCommunicator(WordGameSystem system) {
        this.server = system;
        this.sessionID = server.connect();
    }

    /**
     * send a Request bundle to server to process
     * @param request the request, a bundle containing appropriate information
     */
    @Override
    public void sendRequest(Request request) {
        currentResponse = server.processRequest(request);
    }

    /**
     * get a Response bundle from system side to client side
     * @return pass over the response
     */
    @Override
    public Response getResponse() {
        return currentResponse;
    }


    /**
     * Get the session identifier of this running session
     * @return the current sessionID
     */
    @Override
    public String getSessionID() {
        return sessionID;
    }
}
