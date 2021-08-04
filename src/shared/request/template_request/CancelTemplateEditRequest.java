package shared.request.template_request;

/**
 * CancelTemplateEditRequest Class
 */
public class CancelTemplateEditRequest extends TemplateRequest {
    String templateID;

    /**
     * CancelTemplateEditRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     * @param templateID of the template
     */
    public CancelTemplateEditRequest(String sessionID, String senderID, String templateID) {
        super(sessionID, senderID);
        this.templateID = templateID;
    }

    /**
     *
     * @return the templateID
     */
    public String getTemplateID(){
        return templateID;
    }
}
