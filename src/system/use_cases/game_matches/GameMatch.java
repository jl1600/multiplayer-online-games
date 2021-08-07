package system.use_cases.game_matches;

import shared.constants.MatchStatus;
import shared.exceptions.use_case_exceptions.DuplicateUserIDException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;

import java.util.Set;

public abstract class GameMatch {
    private final String id;
    private final String hostID;
    private MatchStatus status;
    private int playerLimit;
    public GameMatch(String matchID, String userID, int playerLimit) {
        this.id = matchID;
        this.hostID = userID;
        this.status = MatchStatus.PREPARING;
        this.playerLimit = playerLimit;
    }


    protected void setPlayerLimit(int value) {
        playerLimit = value;
    }

    public int getPlayerLimit() {
        return playerLimit;
    }

    public String getID() {
        return id;
    }

    /**
     * Returns the current text content of this word game match. The content may be different depending on the player.
     *
     * @param playerID The unique string identifier of the player.
     * @return The human-readable string that represents the state of this game.
     * @throws InvalidUserIDException When the match doesn't contain such a player.
     * */
    public abstract String getTextContent(String playerID) throws InvalidUserIDException;

    /**
     * Returns the string representation of the stats of a player.
     *
     * @param playerID The unique string identifier of the player.
     * @throws InvalidUserIDException When the match doesn't contain such a player.
     * */
    public abstract String getPlayerStats(String playerID) throws InvalidUserIDException;

    /**
     * Returns a list of all ids of players in this match.
     * */
    public abstract Set<String> getAllPlayerIds();

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
    public void startMatch() {
        if (status == MatchStatus.PREPARING) {
            status = MatchStatus.ONGOING;
        }
    }

    /**
     * Turn the match status to FINISHED.
     * */
    public void setFinishedStatus() {
        status = MatchStatus.FINISHED;
    }

    /**
     * Add a new player to the match.
     *
     * @param playerID The unique string identifier of the player.
     * */
    public abstract void addPlayer(String playerID) throws DuplicateUserIDException;

    /**
     * Remove a player to the match.
     *
     * @param playerID The unique string identifier of the player.
     * */

    public abstract void removePlayer(String playerID) throws InvalidUserIDException;
    /**
     * Play a game move.
     *
     * @param playerID The unique string identifier of the player.
     * @param move The string representing the player input
     *
     * */
    public abstract void playMove(String playerID, String move) throws InvalidUserIDException, InvalidInputException;
}
