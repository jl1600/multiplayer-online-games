package shared.response.misc;

/**
 * FinishStateResponse Class
 */
public class FinishStateResponse extends SimpleTextResponse {
    /**
     * FinishStateResponse Constructor
     * @param sessionID of the session
     * @param text of the response
     */
    public FinishStateResponse(String sessionID, String text) {
        super(sessionID, text);
    }
}
