package system.controllers;

import com.google.gson.Gson;
import shared.DTOs.sockets.MatchInput;
import shared.constants.MatchStatus;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import shared.exceptions.use_case_exceptions.InvalidMatchIDException;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;
import system.use_cases.managers.MatchManager;
import java.io.*;
import java.net.Socket;

public class PlayerInputListener extends Thread {

    InputStream inStream;
    OutputStream outStream;
    MatchManager manager;
    String matchID;
    String playerID;
    Gson gson;

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

    public void run() {
        while (true) {
            try {
                if (manager.getMatchStatus(matchID) != MatchStatus.FINISHED) {
                    String userInput = ClientSocketSeeker.readWSMessage(inStream);
                    System.out.println(userInput);
                    MatchInput inData = gson.fromJson(userInput, MatchInput.class);
                    switch (inData.sysCommand) {
                        case "start":
                            if (manager.getHostId(matchID).equals(playerID))
                                manager.startMatch(matchID);
                            break;
                    }
                    if (!inData.gameMove.equals("")) {
                        System.out.println("trying to play move");
                        manager.playGameMove(playerID, matchID, inData.gameMove);
                    }
                }
            } catch (InvalidMatchIDException e) {
                throw new RuntimeException("Match ID is invalid. This should never happen.");
            } catch (InvalidInputException e) {
                try {
                    ClientSocketSeeker.sendWSMessage(outStream, "Invalid input.");
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

}
