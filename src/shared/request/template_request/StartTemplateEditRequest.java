package shared.request.template_request;

/**
 * StartTemplateEditRequest Class
 */
public class StartTemplateEditRequest extends TemplateRequest {

    private final String templateID;

    /**
     * StartTemplateEditRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     * @param templateID of the template
     */
    public StartTemplateEditRequest(String sessionID, String senderID, String templateID) {
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
