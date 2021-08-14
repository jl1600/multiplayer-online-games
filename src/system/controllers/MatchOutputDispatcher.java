package system.controllers;

import com.google.gson.Gson;
import shared.DTOs.sockets.MatchOutput;
import shared.exceptions.use_case_exceptions.InvalidMatchIDException;
import system.use_cases.managers.MatchManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MatchOutputDispatcher implements Observer {

    public final MatchManager matchManager;
    public final List<OutputStream> outStreams;
    public final String matchID;
    public final Gson gson;

    public MatchOutputDispatcher(MatchManager manager, String matchID) {
        this.matchManager = manager;
        this.matchID = matchID;
        this.outStreams = new ArrayList<>();
        gson = new Gson();
    }

    /**
     * Add a new output stream that is associated with a new player.
     * */
    public void addPlayerOutput(Socket socket) throws IOException {
        this.outStreams.add(socket.getOutputStream());
    }


    /**
     * Send match output to all players.
     *
     * @param o the game match that is being observed
     * @param arg the String ID of the player who caused the change.
     * */
    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Trying to update");
        for (OutputStream out: outStreams) {
            MatchOutput matchOutput = new MatchOutput();
            try {
                matchOutput.status = matchManager.getMatchStatus(matchID);
                matchOutput.textContent = matchManager.getMatchTextContent(matchID);
                matchOutput.lastTurnMoves = matchManager.getPlayersLastMove(matchID);
                matchOutput.numPlayers = matchManager.getPlayerCount(matchID);
            } catch (InvalidMatchIDException e) {
                throw new RuntimeException("Invalid match ID. This should never happen.");
            }
            try {
                sendWSMessage(out, gson.toJson(matchOutput));
            } catch (IOException e) {
                outStreams.remove(out);
                System.out.println("Can't connect to this player. They may have left the match.");
            }
        }
    }
    // Read a websocket text message
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
