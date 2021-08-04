package shared.request.template_request;

import shared.request.Request;

/**
 * TemplateRequest Class
 */
public abstract class TemplateRequest extends Request {

    private final String senderID;

    /**
     * TemplateRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     */
    public TemplateRequest(String sessionID, String senderID) {
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
