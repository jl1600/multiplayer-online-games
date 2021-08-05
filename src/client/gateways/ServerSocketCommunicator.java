package client.gateways;

import shared.request.Request;
import shared.response.Response;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class ServerSocketCommunicator implements IServerCommunicator, AutoCloseable {

    private Response currentResponse;
    private Socket socket;
    private final String sessionID;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;

    /**
     * Constructor for ServerSocketCommunicator
     * @param
     */
    public ServerSocketCommunicator(String hostAddress, int channel) {
        try {
            socket = new Socket(hostAddress, channel);
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());
            this.sessionID = "0"; // Temporary session ID, may change later.
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: Unidentified host. Make sure the server ip address and the port are valid.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Server I/O error!");
        }

    }

    /**
     * send a Request bundle to server to process
     * @param request the request, a bundle containing appropriate information
     */
    @Override
    public void sendRequest(Request request) {
        try {
            outStream.writeObject(request);
            currentResponse = (Response) inStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error: Cannot write the request to the output stream.");
        }
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

    @Override
    public void close() throws IOException {
        outStream.close();
        inStream.close();
        socket.close();
    }
}
