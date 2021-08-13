package system.controllers;

import shared.exceptions.use_case_exceptions.InvalidMatchIDException;
import system.use_cases.managers.MatchManager;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClientSocketSeeker extends Thread {
    private String userID;
    private String matchID;
    private MatchManager matchManager;
    private ServerSocket serverSocket;
    public ClientSocketSeeker(MatchManager matchManager, ServerSocket serverSocket, String userID, String matchID) {
        this.userID = userID;
        this.matchID = matchID;
        this.matchManager = matchManager;
        this.serverSocket = serverSocket;
    }

    public void run() {
        try {
            acceptPlayerSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidMatchIDException e) {
            e.printStackTrace();
        }
    }

    private void acceptPlayerSocket() throws IOException, InvalidMatchIDException {
        Socket connection = serverSocket.accept();
        handShake(connection);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String playerID = reader.readLine();
        // System.out.println("User id is: " + userID);
        //while ((playerID = reader.readLine())!= null) {
         //   System.out.println(playerID);
        //}
        // Refusing connection until it's the correct user ID that we are looking for.
        //while (!playerID.equals(userID)) {
        //    connection.close();
        //    connection = serverSocket.accept();
        //    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        //    playerID = reader.readLine();
        //}
        System.out.println("The player id is: " + playerID);
        PlayerInputListener inputListener = new PlayerInputListener(connection, matchManager, matchID, playerID);
        MatchOutputDispatcher outputDispatcher = new MatchOutputDispatcher(matchManager, matchID);
        outputDispatcher.addPlayerOutput(connection);
        matchManager.addObserver(outputDispatcher, matchID);
        inputListener.start();
    }

    private void handShake(Socket client) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String input;
        while ((input = reader.readLine())!= null) {
           if(input.startsWith("Sec-WebSocket-Key:"))
               break;
        }
        System.out.println(input);
        String key = input.split(" ")[1];
        System.out.println(key);
        String template = "HTTP/1.1 101 Switching Protocols\r\nUpgrade: " +
                "websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: " + encode(key) + "\r\n\r\n";

        PrintWriter printWriter = new PrintWriter(client.getOutputStream());
        printWriter.write(template);
        printWriter.flush();
    }

    private String encode(String key) {
        String extended_key = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        byte[] bytes;
        try {
            bytes = MessageDigest.getInstance("SHA-1").digest(extended_key.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No digesting algorithm");
        }
        return DatatypeConverter.printBase64Binary(bytes);
    }


}
