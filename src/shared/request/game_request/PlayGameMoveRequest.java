package shared.request.game_request;

/**
 * PlayGameMoveRequest Class
 */
public class PlayGameMoveRequest extends GameRequest {

    private final String matchID;
    private final String move;

    /**
     * PlayGameMoveRequest Constructor
     * @param sessionID of the session
     * @param sender of the sender
     * @param matchID of the match
     * @param move play being made
     */
    public PlayGameMoveRequest(String sessionID, String sender, String matchID, String move) {
        super(sessionID, sender);
        this.matchID = matchID;
        this.move = move;
    }

    /**
     *
     * @return the matchID
     */
    public String getMatchID() {
        return matchID;
    }

    /**
     *
     * @return move play made
     */
    public String getMove() {
        return move;
    }
}
