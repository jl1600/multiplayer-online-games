package system.use_cases.managers;

import shared.constants.MatchStatus;
import shared.exceptions.use_case_exceptions.*;
import system.entities.game.Game;
import system.use_cases.game_matches.GameMatch;
import system.entities.template.Template;
import system.use_cases.factories.GameMatchFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MatchManager {
    private final ConcurrentMap<String, GameMatch> preparingMatches;
    private final ConcurrentMap<String, GameMatch> ongoingMatches;
    private final ConcurrentMap<String, GameMatch> finishedMatches;
    private final IdManager matchIdMgr;
    private final GameMatchFactory matchFactory;

    public MatchManager() {
        preparingMatches = new ConcurrentHashMap<>();
        ongoingMatches = new ConcurrentHashMap<>();
        finishedMatches = new ConcurrentHashMap<>();
        matchIdMgr = new IdManager(0);
        matchFactory = new GameMatchFactory();
    }

    /**
     * Create a new game match and returns the match ID.
     * The match created will have a PREPARING status.
     *
     * @param userID The string identifier of the user.
     * @param game  The game object, contains the content of the game.
     * @param template The template of the game, which is used to assist determining game logic.
     *
     * @return The ID of the newly created match.
     * */
    public String newMatch(String userID, Game game, Template template) {
        GameMatch newMatch = matchFactory.getGameMatch(matchIdMgr.getNextId(), userID, game, template);
        preparingMatches.put(newMatch.getID(), newMatch);
        return newMatch.getID();
    }
    /**
     * Starts a game match with PREPARING status.
     *
     * @param matchID The unique string identifier of the match.
     * @throws InvalidMatchIDException When there is no such match with the PREPARING status.
     * */
    public void startMatch(String matchID) throws InvalidMatchIDException {
        if (preparingMatches.containsKey(matchID)) {
            preparingMatches.get(matchID).startMatch();
            ongoingMatches.put(matchID, preparingMatches.remove(matchID));
        } else {
            throw new InvalidMatchIDException();
        }
    }
    /**
     * Add a player to a preparing match.
     *
     * @param userID The string identifier of the user.
     * @param matchID The string identifier of the match.
     * @throws InvalidMatchIDException When there is no match with the given ID is having PREPARING status.
     * @throws DuplicateUserIDException When the user is already in the match.
     * */
    public void addPlayer(String userID, String matchID) throws InvalidMatchIDException, DuplicateUserIDException {
        if (preparingMatches.containsKey(matchID)) {
            preparingMatches.get(matchID).addPlayer(userID);
        }
        else {
            throw new InvalidMatchIDException();
        }
    }

    /**
     * Returns the status of a match.
     *
     * @param matchID The String identifier of the match.
     * */
    public MatchStatus getMatchStatus(String matchID) throws InvalidMatchIDException {
        if (preparingMatches.containsKey(matchID)) {
            return preparingMatches.get(matchID).getStatus();
        } else {
            throw new InvalidMatchIDException();
        }
    }

    /**
     * Returns the game ID of a match.
     *
     * @param matchID the string identifier of the match.
     * @throws InvalidMatchIDException There is no match with such ID in the system.
     * */
    public String getGameIdFromMatch(String matchID) throws InvalidMatchIDException {
        if (preparingMatches.containsKey(matchID)) {
            return preparingMatches.get(matchID).getGameId();
        } else if (ongoingMatches.containsKey(matchID)) {
            return ongoingMatches.get(matchID).getGameId();
        } else if (finishedMatches.containsKey(matchID)) {
            return finishedMatches.get(matchID).getGameId();
        } else {
            throw new InvalidMatchIDException();
        }
    }

    /**
     * Returns the player count of a match.
     *
     * @param matchID The ID of the match
     * @throws InvalidMatchIDException When there is no such match in the system.
     * */
    public int getPlayerCount(String matchID) throws InvalidMatchIDException {
        if (preparingMatches.containsKey(matchID)) {
            return preparingMatches.get(matchID).getPlayerCount();
        } else if (ongoingMatches.containsKey(matchID)) {
            return ongoingMatches.get(matchID).getPlayerCount();
        } else if (finishedMatches.containsKey(matchID)) {
            return finishedMatches.get(matchID).getPlayerCount();
        } else {
            throw new InvalidMatchIDException();
        }
    }

    /**
     * Returns the ID of the player who created this match
     *
     * @param matchID The ID of the match.
     * */
    public String getHostId(String matchID) throws InvalidMatchIDException {
        if (preparingMatches.containsKey(matchID)) {
            return preparingMatches.get(matchID).getHostID();
        } else if (ongoingMatches.containsKey(matchID)) {
            return ongoingMatches.get(matchID).getHostID();
        } else if (finishedMatches.containsKey(matchID)) {
            return finishedMatches.get(matchID).getHostID();
        } else {
            throw new InvalidMatchIDException();
        }
    }

    /**
     * Returns a set of all ongoing match ids.
     * */
    public Set<String> getAllOngoingMatchIds() {
        return new HashSet<>(ongoingMatches.keySet());
    }

    /**
     * Returns the maximum number of players allowed for a particular match.
     *
     * @param matchID The ID of the match.
     * */
    public int getPlayerCountLimit(String matchID) throws InvalidMatchIDException {
        if (preparingMatches.containsKey(matchID)) {
            return preparingMatches.get(matchID).getPlayerLimit();
        } else if (ongoingMatches.containsKey(matchID)) {
            return ongoingMatches.get(matchID).getPlayerLimit();
        } else if (finishedMatches.containsKey(matchID)) {
            return finishedMatches.get(matchID).getPlayerLimit();
        } else {
            throw new InvalidMatchIDException();
        }
    }

    /**
     * Returns a set of all match ids of match with PREPARING status.
     * */
    public Set<String> getAllPreparingMatchIds() {
        return new HashSet<>(preparingMatches.keySet());
    }

    /**
     * Play a game move in an ongoing match.
     *
     * @param playerID The unique string identifier of the player.
     * @param matchID The unique string identifier of the match.
     * @param move  The move.
     * @throws InvalidMatchIDException There is no such ongoing match.
     * @throws InvalidInputException The move is invalid.
     * @throws InvalidUserIDException The match doesn't contain such a player.
     * */
    public void playGameMove(String playerID, String matchID, String move)
            throws InvalidMatchIDException, InvalidInputException, InvalidUserIDException {
        if (ongoingMatches.containsKey(matchID)) {
            ongoingMatches.get(matchID).playMove(playerID, move);
        }
        else {
            throw new InvalidMatchIDException();
        }
    }

    /**
     * Returns the text content of a game match.
     *
     * @param playerID The ID of the player.
     * @param matchID The ID of the match.
     * @throws InvalidMatchIDException When there is no such ONGOING match.
     * @throws InvalidUserIDException When there is no such player in the match.
     * */
    public String getMatchTextContent(String playerID, String matchID) throws
            InvalidMatchIDException, InvalidUserIDException{
        if (ongoingMatches.containsKey(matchID)) {
            return ongoingMatches.get(matchID).getTextContent(playerID);
        }
        else throw new InvalidMatchIDException();
    }

    /**
     * Update all the ongoing and finished matches.
     * Finished matches with no player left will be removed from the system.
     * */
    public void update() {
        Set<String> toBeRemoved = new HashSet<>();
        Set<String> finished = new HashSet<>();
        for (String id: ongoingMatches.keySet()) {
            if(ongoingMatches.get(id).getStatus() == MatchStatus.FINISHED) {
                finished.add(id);
            }
        }
        for (String id: finishedMatches.keySet()) {
            if(finishedMatches.get(id).getPlayerCount() == 0) {
                toBeRemoved.add(id);
            }
        }
        for (String id: finished) {
            finishedMatches.put(id, ongoingMatches.remove(id));
        }
        for (String id: toBeRemoved) {
            finishedMatches.remove(id);
        }
    }
}
