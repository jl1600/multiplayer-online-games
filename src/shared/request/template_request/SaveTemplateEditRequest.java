package shared.request.template_request;

/**
 * SaveTemplateEditRequest Class
 */
public class SaveTemplateEditRequest extends TemplateRequest {
    private final String templateID;

    /**
     * SaveTemplateEditRequest Class
     * @param sessionID of the session
     * @param senderID of the sender
     * @param templateID of the template
     */
    public SaveTemplateEditRequest(String sessionID, String senderID, String templateID) {
        super(sessionID, senderID);
        this.templateID = templateID;
    }

    /**
     *
     * @return the templateId
     */
    public String getTemplateID() {
        return templateID;
    }
}
