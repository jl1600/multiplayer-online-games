package shared.request.game_request;

/**
 * GetOwnedGameInfoRequest Class
 */
public class GetOwnedGameInfoRequest extends GameRequest {
    private final String targetUserID;

    /**
     * GetOwnedGameInfoRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     * @param targetUserID of the targeted user
     */
    public GetOwnedGameInfoRequest(String sessionID, String senderID, String targetUserID) {
        super(sessionID, senderID);
        this.targetUserID = targetUserID;
    }

    /**
     *
     * @return the targetUserID
     */
    public String getTargetUserID() {
        return targetUserID;
    }
}
