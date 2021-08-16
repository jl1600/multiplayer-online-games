package system.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import shared.DTOs.sockets.MatchInput;
import shared.constants.MatchStatus;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import shared.exceptions.use_case_exceptions.InvalidMatchIDException;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;
import system.use_cases.managers.MatchManager;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * PlayerInputListener Class
 */
public class PlayerInputListener extends Thread {

    InputStream inStream;
    OutputStream outStream;
    MatchManager manager;
    String matchID;
    String playerID;
    Gson gson;

    /**
     * Constructor of PlayerInputListener
     * @param socket the web socket used for communication
     * @param manager the match manager that contains all matches and can manipulate them
     * @param matchID the current match id
     * @param playerID the current player id
     */
    public PlayerInputListener(Socket socket, MatchManager manager, String matchID, String playerID) {
        try {
            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException("I/O problem when trying to get input stream from socket.");
        }
        this.manager = manager;
        this.matchID = matchID;
        this.playerID = playerID;
        this.gson = new Gson();
    }

    /**
     * run PlayerInputListener
     * play the game, and check current status to give corresponding handling.
     */
    public void run() {
        while (true) {
            try {
                if (manager.getMatchStatus(matchID) != MatchStatus.FINISHED) {
                    String userInput = readWSMessage(inStream);
                    MatchInput inData;
                    try {
                        inData = gson.fromJson(userInput, MatchInput.class);
                    } catch (JsonSyntaxException e) {
                        try {
                            manager.removePlayer(playerID, matchID);
                        } catch (InvalidMatchIDException | InvalidUserIDException invalidMatchIDException) {
                            System.out.println("Match no longer exist or player already removed.");
                        }
                        return; // Terminate this thread.
                    }
                    switch (inData.sysCommand) {
                        case "start":
                            if (manager.getHostId(matchID).equals(playerID))
                                manager.startMatch(matchID);
                            break;
                    }
                    if (!inData.gameMove.equals("")) {
                        manager.playGameMove(playerID, matchID, inData.gameMove);
                    }
                }
            } catch (InvalidMatchIDException e) {
                throw new RuntimeException("Match ID is invalid. This should never happen.");
            } catch (InvalidInputException e) {
                try {
                    sendWSMessage(outStream, "Invalid input.");
                } catch (IOException ioException) {
                    try {
                        manager.removePlayer(playerID, matchID);
                    } catch (InvalidMatchIDException | InvalidUserIDException invalidMatchIDException) {
                        System.out.println("Match no longer exist or player already removed.");
                    }
                    return; // Terminate this thread.
                }
            } catch (InvalidUserIDException e) {
                throw new RuntimeException("Invalid player ID. This should never happen.");
            } catch (IOException e) {
                try {
                    manager.removePlayer(playerID, matchID);
                } catch (InvalidMatchIDException | InvalidUserIDException invalidMatchIDException) {
                    System.out.println("Match no longer exist or player already removed.");
                }
            }
        }
    }

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
