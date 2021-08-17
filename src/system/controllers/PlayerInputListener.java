package system.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import shared.DTOs.sockets.MatchInput;
import shared.constants.IDType;
import shared.constants.MatchStatus;
import shared.exceptions.use_case_exceptions.InvalidIDException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.use_cases.managers.MatchManager;

import java.io.*;
import java.net.Socket;

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
     *
     * @param socket   the web socket used for communication
     * @param manager  the match manager that contains all matches and can manipulate them
     * @param matchID  the current match id
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
     * Run PlayerInputListener
     * <p>
     * Monitors the socket input stream and handles player inputs depending on the match status.
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
                        } catch (InvalidIDException e2) {
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
            } catch (InvalidIDException e) {
                throw new RuntimeException("Match ID or Player ID is invalid. This should never happen.");

            } catch (InvalidInputException e) {
                try {
                    MatchOutputDispatcher.sendWSMessage(outStream, "Invalid input.");
                } catch (IOException ioException) {
                    try {
                        manager.removePlayer(playerID, matchID);
                    } catch (InvalidIDException e3) {
                        System.out.println("Match no longer exist or player already removed.");
                    }
                    return; // Terminate this thread.
                }
            } catch (IOException e) {
                try {
                    manager.removePlayer(playerID, matchID);
                } catch (InvalidIDException e4) {
                    System.out.println("Match no longer exist or player already removed.");
                }
            }
        }
    }

    static String readWSMessage(InputStream input) throws IOException {
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
            decoded[i] = (byte) (encoded[i] ^ mask[i % 4]);
        }
        return new String(decoded);
    }

}
