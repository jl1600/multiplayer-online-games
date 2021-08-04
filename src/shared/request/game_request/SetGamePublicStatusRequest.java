package shared.request.game_request;

/**
 * SetGamePublicStatusRequest Class
 */
public class SetGamePublicStatusRequest extends GameRequest{

    private final boolean isPublic;
    private final String gameID;

    /**
     * SetGamePublicStatusRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     * @param gameID of the game
     * @param isPublic boolean for the game
     */
    public SetGamePublicStatusRequest(String sessionID, String senderID, String gameID, boolean isPublic) {
        super(sessionID, senderID);
        this.gameID = gameID;
        this.isPublic = isPublic;
    }

    /**
     *
     * @return isPublic boolean of the game
     */
    public boolean isPublic() {
        return isPublic;
    }

    /**
     *
     * @return the gameID
     */
    public String getGameID() {
        return gameID;
    }
}
