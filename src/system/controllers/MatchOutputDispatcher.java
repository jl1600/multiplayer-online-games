package system.controllers;

import com.google.gson.Gson;
import shared.DTOs.sockets.MatchOutput;
import shared.exceptions.use_case_exceptions.InvalidIDException;
import system.use_cases.managers.MatchManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * MatchOutputDispatcher Class
 */
public class MatchOutputDispatcher implements Observer {

    /**
     * the match manager that contains all matches and can manipulate them
     */
    public final MatchManager matchManager;
    /**
     * the outputs
     */
    public final OutputStream outStream;
    /**
     * the current match id
     */
    public final String matchID;
    /**
     * current user id
     */
    public final String userID;
    /**
     * gson used to convert to json, for communication across parts
     */
    public final Gson gson;

    /**
     * @param out     the output stream
     * @param manager the match manager that contains all matches and can manipulate them
     * @param matchID the observed match id
     * @param userID  the client user id
     */
    public MatchOutputDispatcher(OutputStream out, MatchManager manager, String matchID, String userID) {
        this.matchManager = manager;
        this.matchID = matchID;
        outStream = out;
        this.userID = userID;
        gson = new Gson();
    }

    /**
     * Sends the latest match output to the output stream when the observed game match is modified.
     *
     * @param o   the observed object, not used, required by Observer
     * @param arg input argument, not used, required by Observer
     */
    @Override
    public void update(Observable o, Object arg) {
        MatchOutput matchOutput = new MatchOutput();
        try {
            matchOutput.status = matchManager.getMatchStatus(matchID);
            matchOutput.textContent = matchManager.getMatchTextContent(matchID);
            matchOutput.playerStats = matchManager.getAllPlayerStats(matchID);
            matchOutput.numPlayers = matchManager.getPlayerCount(matchID);
        } catch (InvalidIDException e) {
            System.out.println("Match no longer exists");
        }
        try {
            sendWSMessage(outStream, gson.toJson(matchOutput));
        } catch (IOException e) {
            try {
                matchManager.deleteObserver(this, matchID);
                matchManager.removePlayer(userID, matchID);
            } catch (InvalidIDException e2) {
                System.out.println("Player already left or match doesn't exist anymore.");
            }

            System.out.println("Can't connect to this player. They may have left the match.");
        }
    }

    // Read a websocket text message
    // Sending a websocket text message
    // Formatting the byte array so that it follows the standard for websocket communication
    static void sendWSMessage(OutputStream output, String message) throws IOException {
        byte[] firstTwo = new byte[2];
        firstTwo[0] |= (1 << 7);  // FIN, telling the client that this is a whole message
        firstTwo[0] |= 1; // Op code, 0x1, telling that this is a text
        int lenCode;    // this is the length of the message if length < 126
        byte[] uint16Len = new byte[2];   // A backup for the length in the case it exceeds 125

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
