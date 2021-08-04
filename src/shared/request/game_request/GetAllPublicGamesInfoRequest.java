package shared.request.game_request;

/**
 * GetAllPublicGamesInfoRequest Class
 */
public class GetAllPublicGamesInfoRequest extends GameRequest {
    /**
     * GetAllPublicGamesInfoRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     */
    public GetAllPublicGamesInfoRequest(String sessionID, String senderID) {
        super(sessionID, senderID);
    }
}
