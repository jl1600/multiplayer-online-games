package shared.request.game_request;

import shared.constants.GameAccessLevel;

/**
 * SetGamePublicStatusRequest Class
 */
public class SetGamePublicStatusRequest extends GameRequest{

    private final boolean isPublic;
    private final String gameID;
    private final GameAccessLevel gameAccessLevel;

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
        if (isPublic){
            this.gameAccessLevel = GameAccessLevel.PUBLIC;
        } else {
            this.gameAccessLevel = null;
        }
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
    public GameAccessLevel getGameAccessLevel(){return gameAccessLevel;}
}
