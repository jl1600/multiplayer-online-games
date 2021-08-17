package system.use_cases.game_matches;

import shared.constants.MatchStatus;
import shared.exceptions.use_case_exceptions.DuplicateUserIDException;
import shared.exceptions.use_case_exceptions.InvalidIDException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import shared.exceptions.use_case_exceptions.MaxPlayerReachedException;

import java.util.Map;
import java.util.Observable;

/**
 * Abstract GameMatch Class
 */
public abstract class GameMatch extends Observable {

    private final String id;
    private final String hostID;
    private final String hostName;
    private MatchStatus status;
    private int playerLimit;

    /**
     * Constructor of GameMatch
     * @param matchID the current match id
     * @param userID the host user id
     * @param username the host user name
     * @param playerLimit the max player limit
     */
    public GameMatch(String matchID, String userID, String username, int playerLimit) {
        this.id = matchID;
        this.hostID = userID;
        this.status = MatchStatus.PREPARING;
        this.playerLimit = playerLimit;
        this.hostName = username;
    }

    /**
     *
     * @param value the desired player limit
     */
    protected void setPlayerLimit(int value) {
        playerLimit = value;
    }

    /**
     * Return the maximum allowed numer of players.
     * @return the maximum allowed number of players
     */
    public int getPlayerLimit() {
        return playerLimit;
    }

    /**
     * Return the userID of the match host.
     * @return the userID of the match host.
     */
    public String getHostName() {
        return this.hostName;
    }

    /**
     * Returns the matchID.
     * @return the matchID
     */
    public String getID() {
        return id;
    }

    /**
     * Returns the current text content of this word game match. The content may be different depending on the player.
     * @return The human-readable string that represents the state of this game.
     */
    public abstract String getTextContent();

    /**
     * Returns the string representation of the status of a player.
     * @return a map of player's username to their current display status
     */
    public abstract Map<String, String> getAllPlayerStats();

    /**
     * Returns the current number of players in this match.
     * @return the current number of players
     */
    public abstract int getPlayerCount();

    /**
     * Returns the ID of the game of this match.
     * @return the gameID of the game of this match
     */
    public abstract String getGameId();

    /**
     * Returns the ID of the player who started this match.
     * @return the userId of the host
     */
    public String getHostID() {
        return hostID;
    }

    /**
     * Returns the current match status.
     * @return the current match status
     */
    public MatchStatus getStatus() {
        return status;
    }

    /**
     * Change match satus from preparing to ongoing.
     * <p>
     * If the status is not preparing, do nothing.
     */
    public abstract void startMatch();

    /**
     * Change the match status to the input
     *
     * @param status the new status
     */
    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    /**
     * Adds a new player to the match.
     *
     * @param playerID   the player's userID
     * @param playerName the player's username
     * @throws DuplicateUserIDException  if the input userID is already in the game.
     * @throws MaxPlayerReachedException if the game is full.
     */
    public abstract void addPlayer(String playerID, String playerName) throws DuplicateUserIDException, MaxPlayerReachedException;

    /**
     * Remove a player from the match.
     *
     * @param playerID The unique string identifier of the player.
     * @throws InvalidIDException if the input playerID is not found in the match.
     */
    public abstract void removePlayer(String playerID) throws InvalidIDException;

    /**
     * Play a game move. Do nothing if the match status is finished.
     *
     * @param playerID The unique string identifier of the player.
     * @param move     The string representing the player input
     * @throws InvalidIDException if the input player's userID is not found
     * @throws InvalidInputException  if the input move is not recognized.
     */
    public abstract void playMove(String playerID, String move) throws InvalidIDException, InvalidInputException;
}
