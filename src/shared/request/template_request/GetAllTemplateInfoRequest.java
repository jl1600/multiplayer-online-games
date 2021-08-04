package shared.request.template_request;

/**
 * GetAllTemplateInfoRequest Class
 */
public class GetAllTemplateInfoRequest extends TemplateRequest{
    /**
     * GetAllTemplateInfoRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     */
    public GetAllTemplateInfoRequest(String sessionID, String senderID) {
        super(sessionID, senderID);
    }

}
