package shared.request.game_request;

import shared.request.Request;

/**
 * GameRequest Class
 */
public abstract class GameRequest extends Request {
    private final String senderID;

    /**
     * GameRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     */
    public GameRequest(String sessionID, String senderID) {
        super(sessionID);
        this.senderID = senderID;
    }

    /**
     *
     * @return the senderID
     */

    public String getSenderID() {
        return senderID;
    }
}
