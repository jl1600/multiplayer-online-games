package shared.request.template_request;

/**
 * NewTemplateRequest Class
 */
public class NewTemplateRequest extends TemplateRequest {

    /**
     * NewTemplateRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     */
    public NewTemplateRequest(String sessionID, String senderID) {
        super(sessionID, senderID);
    }

}
