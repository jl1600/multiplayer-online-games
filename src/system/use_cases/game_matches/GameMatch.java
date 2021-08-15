package system.use_cases.game_matches;

import shared.constants.MatchStatus;
import shared.exceptions.use_case_exceptions.DuplicateUserIDException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;
import shared.exceptions.use_case_exceptions.MaxPlayerReachedException;

import java.util.Map;
import java.util.Observable;

public abstract class GameMatch extends Observable {

    private final String id;
    private final String hostID;
    private final String hostName;
    private MatchStatus status;
    private int playerLimit;

    public GameMatch(String matchID, String userID, String username, int playerLimit) {
        this.id = matchID;
        this.hostID = userID;
        this.status = MatchStatus.PREPARING;
        this.playerLimit = playerLimit;
        this.hostName = username;
    }


    protected void setPlayerLimit(int value) {
        playerLimit = value;
    }

    public int getPlayerLimit() {
        return playerLimit;
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getID() {
        return id;
    }

    /**
     * Returns the current text content of this word game match. The content may be different depending on the player.
     *
     * @return The human-readable string that represents the state of this game.
     * @throws InvalidUserIDException When the match doesn't contain such a player.
     * */
    public abstract String getTextContent();

    /**
     * Returns the string representation of the stats of a player.
     *
     * @throws InvalidUserIDException When the match doesn't contain such a player.
     * */
    public abstract Map<String, String> getAllPlayerStats();

    /**
     * Returns te current number of players in this match.
     * */
    public abstract int getPlayerCount();

    /**
     * Returns the ID of the game of this match.
     * */
    public abstract String getGameId();

    /**
     * Returns the ID of the player who started this match.
     * */
    public String getHostID() {
        return hostID;
    }

    public MatchStatus getStatus() {
        return status;
    }

    /**
     * Turn the match status from PREPARING to ONGOING.
     * If The status is not PREPARING, do nothing.
     * */
    public abstract void startMatch();

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    /**
     * Add a new player to the match.
     *
     * @param playerID The unique string identifier of the player.
     * */
    public abstract void addPlayer(String playerID, String playerName) throws DuplicateUserIDException, MaxPlayerReachedException;

    /**
     * Remove a player to the match.
     *
     * @param playerID The unique string identifier of the player.
     * */
    public abstract void removePlayer(String playerID) throws InvalidUserIDException;

    /**
     * Play a game move. Do nothing if the match status is finished.
     *
     * @param playerID The unique string identifier of the player.
     * @param move The string representing the player input
     *
     * */
    public abstract void playMove(String playerID, String move) throws InvalidUserIDException, InvalidInputException;
}
