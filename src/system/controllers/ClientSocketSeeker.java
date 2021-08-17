package system.controllers;

import shared.exceptions.use_case_exceptions.InvalidIDException;
import system.use_cases.managers.MatchManager;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.OutputStream;

/**
 * ClientSocketSeeker Class
 */
public class ClientSocketSeeker extends Thread {
    private final String userID;
    private final String matchID;
    private final MatchManager matchManager;
    private final ServerSocket serverSocket;

    /**
     * Constructor of ClientSocketSeeker
     *
     * @param matchManager manager to store and manipulate GameMatch objects
     * @param serverSocket the target server socket for communication
     * @param userID       the player's userID
     * @param matchID      the target matchID
     */
    public ClientSocketSeeker(MatchManager matchManager, ServerSocket serverSocket, String userID, String matchID) {
        this.userID = userID;
        this.matchID = matchID;
        this.matchManager = matchManager;
        this.serverSocket = serverSocket;
    }

    /**
     * Run ClientSocketSeeker
     */
    public void run() {
        try {
            acceptPlayerSocket();
        } catch (IOException | InvalidIDException e) {
            e.printStackTrace();
        }
    }

    private void acceptPlayerSocket() throws IOException, InvalidIDException {
        Socket connection = serverSocket.accept();
        handShake(connection);
        String playerID = PlayerInputListener.readWSMessage(connection.getInputStream());

        // Refusing connection until it's the correct user ID that we are looking for.
        while (!playerID.equals(userID)) {
            connection.close();
            connection = serverSocket.accept();
            handShake(connection);
            playerID = PlayerInputListener.readWSMessage(connection.getInputStream());
        }
        PlayerInputListener inputListener = new PlayerInputListener(connection, matchManager, matchID, playerID);
        MatchOutputDispatcher outputDispatcher = new MatchOutputDispatcher(connection.getOutputStream(),
                matchManager, matchID, userID);
        matchManager.addObserver(outputDispatcher, matchID);
        inputListener.start();
    }

    private void handShake(Socket client) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String input;
        while ((input = reader.readLine()) != null) {
            if (input.startsWith("Sec-WebSocket-Key:"))
                break;
        }
        OutputStream out = client.getOutputStream();
        String key = input.split(" ")[1];
        byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                + "Connection: Upgrade\r\n"
                + "Upgrade: websocket\r\n"
                + "Sec-WebSocket-Accept: "
                + encode(key)
                + "\r\n\r\n").getBytes(StandardCharsets.UTF_8);
        out.write(response, 0, response.length);
        out.flush();
    }

    private String encode(String key) {
        String extended_key = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        byte[] buff;
        try {
            buff = MessageDigest.getInstance("SHA-1").digest(extended_key.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No digesting algorithm");
        }
        return DatatypeConverter.printBase64Binary(buff);
    }

}
