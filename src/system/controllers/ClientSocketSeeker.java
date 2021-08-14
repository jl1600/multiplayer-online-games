package system.controllers;

import shared.exceptions.use_case_exceptions.InvalidMatchIDException;
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

import java.io.InputStream;
import java.io.OutputStream;


public class ClientSocketSeeker extends Thread {
    private final String userID;
    private final String matchID;
    private final MatchManager matchManager;
    private final ServerSocket serverSocket;
    public ClientSocketSeeker(MatchManager matchManager, ServerSocket serverSocket, String userID, String matchID) {
        this.userID = userID;
        this.matchID = matchID;
        this.matchManager = matchManager;
        this.serverSocket = serverSocket;
    }

    public void run() {
        try {
            acceptPlayerSocket();
        } catch (IOException | InvalidMatchIDException e) {
            e.printStackTrace();
        }
    }

    private void acceptPlayerSocket() throws IOException, InvalidMatchIDException {
        Socket connection = serverSocket.accept();
        handShake(connection);
        String playerID = readWSMessage(connection.getInputStream());

        // Refusing connection until it's the correct user ID that we are looking for.
        while (!playerID.equals(userID)) {
            connection.close();
            connection = serverSocket.accept();
            handShake(connection);
            playerID = readWSMessage(connection.getInputStream());
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
        while ((input = reader.readLine())!= null) {
           if(input.startsWith("Sec-WebSocket-Key:"))
               break;
        }
        OutputStream out = client.getOutputStream();
        String key = input.split(" ")[1];
        byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                + "Connection: Upgrade\r\n"
                + "Upgrade: websocket\r\n"
                + "Sec-WebSocket-Accept: "
                + encode(key)
                + "\r\n\r\n").getBytes("UTF-8");
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

    // Read a websocket text message
    private String readWSMessage(InputStream input) throws IOException {
        input.read(); // Skip the first bit, FIN, which should always be 1.
        int byteValue = input.read();
        int messageLen;
        if (byteValue - 128 <= 125) {
            messageLen = byteValue - 128;
        } else if (byteValue - 128 == 126) {
            byte[] buffer = new byte[2];
            messageLen = input.read(buffer, 0, 2);
        } else {
            byte[] buffer = new byte[8];
            messageLen = input.read(buffer, 0, 8);
        }

        byte[] mask = new byte[4];
        input.read(mask, 0, 4);
        byte[] encoded = new byte[messageLen];
        byte[] decoded = new byte[messageLen];
        input.read(encoded, 0, messageLen);
        for (int i = 0; i < messageLen; i++) {
            decoded[i] = (byte)(encoded[i] ^ mask[i % 4]);
        }
        return new String(decoded);
    }

    // Sending a websocket text message
    // Formatting the byte array so that it follows the standard for websocket communication
    private void sendWSMessage(OutputStream output, String message) throws IOException {
        byte[] firstTwo = new byte[2];
        firstTwo[0] |= (1 << 7);  // FIN, telling the client that this is a whole message
        firstTwo[0] |= 1; // Op code, 0x1, telling that this is a text
        int lenCode;    // this is the length of the message if length < 126
        byte [] uint16Len = new byte [2];   // A backup for the length in the case it exceeds 125

        if (message.length() < 126) {
            lenCode = message.length();
        } else {
            lenCode = 126;
            uint16Len[1] = (byte) (message.length() & 0xFF);
            uint16Len[0] = (byte) ((message.length() >>> 8) & 0xFF);
        } // It will never happen that message.len > 65536
        firstTwo[1] |= (byte) lenCode;  // writing the lenCode to the last 7 bits of the second byte.

        byte[] result;
        if (lenCode < 126) {
            result = new byte[2 + message.length()];
            System.arraycopy(firstTwo, 0, result, 0, 2);
            System.arraycopy(message.getBytes(StandardCharsets.UTF_8), 0, result, 2, message.length());
        } else {
            result = new byte[4 + message.length()];
            System.arraycopy(firstTwo, 0, result, 0, 2);
            System.arraycopy(uint16Len, 0, result, 2, 2);
            System.arraycopy(message.getBytes(StandardCharsets.UTF_8), 0, result, 4, message.length());
        }
        output.write(result);
        output.flush();
    }
}
