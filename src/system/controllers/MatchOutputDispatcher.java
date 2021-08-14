package system.controllers;

import com.google.gson.Gson;
import shared.DTOs.sockets.MatchOutput;
import shared.exceptions.use_case_exceptions.InvalidMatchIDException;
import system.use_cases.managers.MatchManager;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
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
                ClientSocketSeeker.sendWSMessage(out, gson.toJson(matchOutput));
            } catch (IOException e) {
                outStreams.remove(out);
                System.out.println("Can't connect to this player. They may have left the match.");
            }
        }
    }
}
