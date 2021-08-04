package system.use_cases.game_matches;

import shared.exceptions.entities_exception.IDAlreadyExistsException;
import shared.exceptions.use_case_exceptions.DuplicateUserIDException;
import shared.exceptions.use_case_exceptions.InvalidIDException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;
import system.entities.game.Game;

public abstract class GameMatch {
    private final String id;
    private boolean finished;
    private final String hostID;


    public GameMatch(String matchID, String userID) {
        this.id = matchID;
        this.hostID = userID;
        this.finished = false;
    }

    public String getID() {
        return id;
    }

    /**
     * Returns the current text content of this word game match. The content may be different depending on the player.
     *
     * @param playerID The unique string identifier of the player.
     * @return The human-readable string that represents the state of this game.
     * */
    public abstract String getTextContent(String playerID) throws InvalidUserIDException;

    public abstract Game getGame();

    public String getHostID() {
        return hostID;
    }

    public boolean isFinished() {
        return finished;
    }
    protected void setFinished(boolean b) {
        finished = b;
    }

    /**
     * Add a new player to the match.
     *
     * @param playerID The unique string identifier of the player.
     * */
    public abstract void addPlayer(String playerID) throws DuplicateUserIDException;

    /**
     * Play a game move.
     *
     * @param playerID The unique string identifier of the player.
     * @param move The string representing the player input
     *
     * */
    public abstract void playMove(String playerID, String move) throws InvalidUserIDException, InvalidInputException;
}
