package shared.request.template_request;

/**
 * DeleteTemplateRequest Class
 */
public class DeleteTemplateRequest extends TemplateRequest {
    private final String templateID;

    /**
     * DeleteTemplateRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     * @param templateID of the template
     */
    public DeleteTemplateRequest(String sessionID, String senderID, String templateID) {
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
