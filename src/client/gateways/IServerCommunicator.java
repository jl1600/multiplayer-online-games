package client.gateways;

import shared.request.Request;
import shared.response.Response;

public interface IServerCommunicator {
    void sendRequest(Request request);
    Response getResponse();
    String getSessionID();
}
