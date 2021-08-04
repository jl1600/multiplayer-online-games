package system.use_cases.managers;

import shared.exceptions.use_case_exceptions.*;
import system.entities.game.Game;
import system.use_cases.game_matches.GameMatch;
import system.entities.template.Template;
import system.use_cases.factories.GameMatchFactory;

import java.util.HashMap;
import java.util.Map;

public class MatchManager {
    private final HashMap<String, GameMatch> matches;
    private final IdManager matchIdMgr;
    private final GameMatchFactory matchFactory;

    public MatchManager() {
        matches = new HashMap<>();
        matchIdMgr = new IdManager(0);
        matchFactory = new GameMatchFactory();
    }

    public String newMatch(String userID, Game game, Template template) {
        GameMatch newMatch = matchFactory.getGameMatch(matchIdMgr.getNextId(), userID, game, template);
        matches.put(newMatch.getID(), newMatch);
        return newMatch.getID();
    }

    public void addPlayer(String userID, String matchID) throws InvalidMatchIDException, DuplicateUserIDException {
        if (matches.containsKey(matchID)) {
            matches.get(matchID).addPlayer(userID);
        }
        else {
            throw new InvalidMatchIDException();
        }
    }

    public String getGameIdFromMatch(String matchID) throws InvalidMatchIDException {
        if (matches.containsKey(matchID)) {
            return matches.get(matchID).getGame().getID();
        }
        else
            throw new InvalidMatchIDException();
    }

    public Map<String, String> getAllMatchToGameMap(){
        HashMap<String, String> matchToGame = new HashMap<>();
        for (String matchID: matches.keySet()) {
            matchToGame.put(matchID, matches.get(matchID).getGame().getID());
        }
        return matchToGame;
    }

    public void playGameMove(String playerID, String matchID, String move)
            throws InvalidMatchIDException, InvalidInputException, InvalidUserIDException {
        if (matches.containsKey(matchID)) {
            matches.get(matchID).playMove(playerID, move);
        }
        else {
            throw new InvalidMatchIDException();
        }
    }

    public String getMatchTextContent(String playerID, String matchID) throws
            InvalidMatchIDException, InvalidUserIDException{
        if (matches.containsKey(matchID)) {
            return matches.get(matchID).getTextContent(playerID);
        }
        else throw new InvalidMatchIDException();
    }

    public boolean checkFinished(String matchID) throws InvalidMatchIDException {
        if (matches.containsKey(matchID)) {
            return matches.get(matchID).isFinished();
        }
        else {
            throw new InvalidMatchIDException();
        }
    }

    public void deleteMatch(String matchID) throws InvalidMatchIDException {
        if (matches.containsKey(matchID)) {
            matches.remove(matchID);
        }
        else {
            throw new InvalidMatchIDException();
        }
    }
}
