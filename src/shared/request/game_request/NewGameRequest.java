package shared.request.game_request;

/**
 * NewGameRequest Class
 */
public class NewGameRequest extends GameRequest {

    private final String templateID;

    /**
     * NewGameRequest Class
     * @param sessionID of the session
     * @param senderID of the sender
     * @param templateID of the template
     */
    public NewGameRequest(String sessionID, String senderID, String templateID) {
        super(sessionID, senderID);
        this.templateID = templateID;
    }

    /**
     *
     * @return the templateID
     */
    public String getTemplateID() {
        return templateID;
    }
}
