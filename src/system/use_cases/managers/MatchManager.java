package system.use_cases.managers;

import shared.constants.IDType;
import shared.constants.MatchStatus;
import shared.exceptions.use_case_exceptions.*;
import system.entities.game.Game;
import system.use_cases.game_matches.GameMatch;
import system.entities.template.Template;
import system.use_cases.factories.GameMatchFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * MatchManager Class
 */
public class MatchManager {
    private final ConcurrentMap<String, GameMatch> preparingMatches;
    private final ConcurrentMap<String, GameMatch> ongoingMatches;
    private final ConcurrentMap<String, GameMatch> finishedMatches;
    private final IdManager matchIdMgr;
    private final GameMatchFactory matchFactory;

    /**
     * Constructor of MatchManager
     */
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
    public String newMatch(String userID, String username, Game game, Template template) {
        GameMatch newMatch = matchFactory.getGameMatch(matchIdMgr.getNextId(), userID, username, game, template);
        preparingMatches.put(newMatch.getID(), newMatch);
        return newMatch.getID();
    }
    /**
     * Starts a game match with PREPARING status.
     *
     * @param matchID The unique string identifier of the match.
     * @throws InvalidIDException When there is no such match with the PREPARING status.
     * */
    public void startMatch(String matchID) throws InvalidIDException {
        if (preparingMatches.containsKey(matchID)) {
            ongoingMatches.put(matchID, preparingMatches.remove(matchID));
            ongoingMatches.get(matchID).startMatch();
        } else {
            throw new InvalidIDException(IDType.MATCH);
        }
    }
    /**
     * Add a player to a preparing match.
     *
     * @param userID The string identifier of the user.
     * @param matchID The string identifier of the match.
     * @throws InvalidIDException When there is no match with the given ID is having PREPARING status.
     * @throws DuplicateUserIDException When the user is already in the match.
     * */
    public void addPlayer(String userID, String username, String matchID) throws
            InvalidIDException, DuplicateUserIDException,
            MaxPlayerReachedException {
        if (preparingMatches.containsKey(matchID)) {
            preparingMatches.get(matchID).addPlayer(userID, username);
        }
        else {
            throw new InvalidIDException(IDType.MATCH);
        }
    }

    /**
     * Remove a player form a match.
     * @param userID the ID of the player.
     * @param matchID the ID of the match.
     * @throws InvalidIDException if the match doesn't exist.
     * */
    public void removePlayer(String userID, String matchID) throws InvalidIDException {
        if (preparingMatches.containsKey(matchID)) {
            preparingMatches.get(matchID).removePlayer(userID);
            if (preparingMatches.get(matchID).getPlayerCount() == 0)
                preparingMatches.remove(matchID);
        } else if (ongoingMatches.containsKey(matchID)) {
            ongoingMatches.get(matchID).removePlayer(userID);
            if (ongoingMatches.get(matchID).getPlayerCount() == 0)
                ongoingMatches.remove(matchID);
        } else if (finishedMatches.containsKey(matchID)) {
            finishedMatches.get(matchID).removePlayer(userID);
            if (finishedMatches.get(matchID).getPlayerCount() == 0)
                finishedMatches.remove(matchID);
        } else {
            throw new InvalidIDException(IDType.MATCH);
        }
    }

    /**
     * Returns the status of a match.
     *
     * @param matchID The String identifier of the match.
     * */
    public MatchStatus getMatchStatus(String matchID) throws InvalidIDException {
        if (preparingMatches.containsKey(matchID)) {
            return preparingMatches.get(matchID).getStatus();
        } else if (ongoingMatches.containsKey(matchID)) {
            return ongoingMatches.get(matchID).getStatus();
        } else if (finishedMatches.containsKey(matchID)) {
            return finishedMatches.get(matchID).getStatus();
        } else {
            throw new InvalidIDException(IDType.MATCH);
        }
    }

    /**
     * Add an observer to a match.
     *
     * @param o the observer
     * @param matchID The ID of the match.
     * @throws InvalidIDException When there is no preparing match with the given ID.
     * */
    public void addObserver(Observer o, String matchID) throws InvalidIDException {
            if (preparingMatches.containsKey(matchID)) {
                preparingMatches.get(matchID).addObserver(o);
            } else {
                throw new InvalidIDException(IDType.MATCH);
            }
    }

    /**
     * Remove an observer from a match.
     *
     * @param o the observer
     * @param matchID The ID of the match.
     * @throws InvalidIDException When there is no preparing match with the given ID.
     * */
    public void deleteObserver(Observer o, String matchID) throws InvalidIDException {
        if (preparingMatches.containsKey(matchID)) {
            preparingMatches.get(matchID).deleteObserver(o);
        } else if (ongoingMatches.containsKey(matchID)) {
            ongoingMatches.get(matchID).deleteObserver(o);
        } else if (finishedMatches.containsKey(matchID)) {
            finishedMatches.get(matchID).deleteObserver(o);
        } else {
            throw new InvalidIDException(IDType.MATCH);
        }
    }

    /**
     * Returns the game ID of a match.
     *
     * @param matchID the string identifier of the match.
     * @throws InvalidIDException There is no match with such ID in the system.
     * */
    public String getGameIdFromMatch(String matchID) throws InvalidIDException {
        if (preparingMatches.containsKey(matchID)) {
            return preparingMatches.get(matchID).getGameId();
        } else if (ongoingMatches.containsKey(matchID)) {
            return ongoingMatches.get(matchID).getGameId();
        } else if (finishedMatches.containsKey(matchID)) {
            return finishedMatches.get(matchID).getGameId();
        } else {
            throw new InvalidIDException(IDType.MATCH);
        }
    }

    /**
     * Returns the player count of a match.
     *
     * @param matchID The ID of the match
     * @throws InvalidIDException When there is no such match in the system.
     * */
    public int getPlayerCount(String matchID) throws InvalidIDException {
        if (preparingMatches.containsKey(matchID)) {
            return preparingMatches.get(matchID).getPlayerCount();
        } else if (ongoingMatches.containsKey(matchID)) {
            return ongoingMatches.get(matchID).getPlayerCount();
        } else if (finishedMatches.containsKey(matchID)) {
            return finishedMatches.get(matchID).getPlayerCount();
        } else {
            throw new InvalidIDException(IDType.MATCH);
        }
    }

    /**
     * Returns the ID of the player who created this match
     *
     * @param matchID The ID of the match.
     * */
    public String getHostId(String matchID) throws InvalidIDException {
        if (preparingMatches.containsKey(matchID)) {
            return preparingMatches.get(matchID).getHostID();
        } else if (ongoingMatches.containsKey(matchID)) {
            return ongoingMatches.get(matchID).getHostID();
        } else if (finishedMatches.containsKey(matchID)) {
            return finishedMatches.get(matchID).getHostID();
        } else {
            throw new InvalidIDException(IDType.MATCH);
        }
    }

    /**
     * Returns the userName of the player who created this match
     *
     * @param matchID The ID of the match.
     * */
    public String getHostName(String matchID) throws InvalidIDException {
        if (preparingMatches.containsKey(matchID)) {
            return preparingMatches.get(matchID).getHostName();
        } else if (ongoingMatches.containsKey(matchID)) {
            return ongoingMatches.get(matchID).getHostName();
        } else if (finishedMatches.containsKey(matchID)) {
            return finishedMatches.get(matchID).getHostName();
        } else {
            throw new InvalidIDException(IDType.MATCH);
        }
    }

    /**
     * Returns the maximum number of players allowed for a particular match.
     *
     * @param matchID The ID of the match.
     * */
    public int getPlayerCountLimit(String matchID) throws InvalidIDException {
        if (preparingMatches.containsKey(matchID)) {
            return preparingMatches.get(matchID).getPlayerLimit();
        } else if (ongoingMatches.containsKey(matchID)) {
            return ongoingMatches.get(matchID).getPlayerLimit();
        } else if (finishedMatches.containsKey(matchID)) {
            return finishedMatches.get(matchID).getPlayerLimit();
        } else {
            throw new InvalidIDException(IDType.MATCH);
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
     * @throws InvalidIDException With ID type MATCH if there is no such ongoing match.
     * @throws InvalidIDException With ID type USER if the match doesn't contain such a player.
     * @throws InvalidInputException The move is invalid.
     * */
    public void playGameMove(String playerID, String matchID, String move)
            throws InvalidIDException, InvalidInputException{
        if (ongoingMatches.containsKey(matchID)) {
            ongoingMatches.get(matchID).playMove(playerID, move);
            if (ongoingMatches.get(matchID).getStatus() == MatchStatus.FINISHED) {
                finishedMatches.put(matchID, ongoingMatches.remove(matchID));
            }
        } else if (preparingMatches.containsKey(matchID)) {
            preparingMatches.get(matchID).playMove(playerID, move);
        } else if (finishedMatches.containsKey(matchID)) {
            finishedMatches.get(matchID).playMove(playerID, move);
        }
        else {
            throw new InvalidIDException(IDType.USER);
        }
    }

    /**
     * Returns the text content of a game match.
     * @param matchID The ID of the match.
     * @throws InvalidIDException When there is no such ONGOING match.
     * */
    public String getMatchTextContent(String matchID) throws
            InvalidIDException {
        if (preparingMatches.containsKey(matchID)) {
            return preparingMatches.get(matchID).getTextContent();
        } else if (ongoingMatches.containsKey(matchID)) {
            return ongoingMatches.get(matchID).getTextContent();
        } else if (finishedMatches.containsKey(matchID)) {
            return finishedMatches.get(matchID).getTextContent();
        } else {
            throw new InvalidIDException(IDType.USER);
        }
    }

    /**
     * Returns a mapping of player's name to their last moves.
     * */
    public Map<String, String> getAllPlayerStats(String matchID) throws InvalidIDException {
        if (ongoingMatches.containsKey(matchID)) {
            return ongoingMatches.get(matchID).getAllPlayerStats();
        } else if (preparingMatches.containsKey(matchID)) {
            return new HashMap<>();
        }
        else throw new InvalidIDException(IDType.MATCH);
    }
}
