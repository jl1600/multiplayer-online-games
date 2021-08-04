package shared.request.game_request;

/**
 * GetAllMatchInfoRequest Class
 */
public class GetAllMatchInfoRequest extends GameRequest {
    /**
     * GetAllMatchInfoRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     */
    public GetAllMatchInfoRequest(String sessionID, String senderID) {
        super(sessionID, senderID);
    }
}
