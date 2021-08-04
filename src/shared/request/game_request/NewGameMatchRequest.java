package shared.request.game_request;

/**
 * NewGameMatchRequest Class
 */
public class NewGameMatchRequest extends GameRequest{

    String gameID;

    /**
     * NewGameMatchRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     * @param gameID of the game
     */
    public NewGameMatchRequest(String sessionID, String senderID, String gameID) {
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
