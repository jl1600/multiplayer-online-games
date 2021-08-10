package system.controllers;

import com.google.gson.Gson;
import shared.DTOs.sockets.MatchInput;
import shared.constants.MatchStatus;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import shared.exceptions.use_case_exceptions.InvalidMatchIDException;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;
import system.use_cases.managers.MatchManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayerInputListener extends Thread {

    BufferedReader reader;
    PrintWriter writer;
    MatchManager manager;
    String matchID;
    String playerID;
    Gson gson;

    public PlayerInputListener(Socket socket, MatchManager manager, String matchID, String playerID) {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
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
                    String userInput;
                    if((userInput = reader.readLine()) != null) {
                        MatchInput inData = gson.fromJson(userInput, MatchInput.class);
                        switch (inData.sysCommand) {
                            case "start":
                                if (manager.getHostId(matchID).equals(playerID))
                                    manager.startMatch(matchID);
                                break;
                        }
                        if (!inData.gameMove.equals(""))
                            manager.playGameMove(playerID, matchID, inData.gameMove);
                    }
                }
            } catch (InvalidMatchIDException | IOException e) {
                throw new RuntimeException("Match ID is invalid or IO problem. This should never happen.");
            } catch (InvalidInputException e) {
                writer.write("Error: Invalid input.");
            } catch (InvalidUserIDException e) {
                throw new RuntimeException("Invalid player ID. This should never happen.");
            }
        }
    }
}
