package shared.request.game_request;

/**
 * DeleteGameRequest Class
 */
public class DeleteGameRequest extends GameRequest{
    String gameID;

    /**
     * DeleteGameRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     * @param gameID of the game
     */
    public DeleteGameRequest(String sessionID, String senderID, String gameID) {
        super(sessionID, senderID);
        this.gameID = gameID;
    }

    /**
     *
     * @return the gameID
     */
    public String getGameID() {
        return gameID;
    }
}
