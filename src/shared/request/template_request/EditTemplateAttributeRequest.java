package shared.request.template_request;

/**
 * EditTemplateAttributeRequest Class
 */
public class EditTemplateAttributeRequest extends TemplateRequest{

    private final String attributeName;
    private final String attributeValue;
    private final String templateID;

    /**
     * EditTemplateAttributeRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     * @param templateID of the template
     * @param attributeName of the attribute name
     * @param attributeValue of the attribute value
     */
    public EditTemplateAttributeRequest(String sessionID, String senderID,
                               String templateID, String attributeName, String attributeValue) {
        super(sessionID, senderID);
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.templateID = templateID;
    }

    /**
     *
     * @return the attributeName
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     *
     * @return the attributeValue
     */
    public String getAttributeValue() {
        return attributeValue;
    }

    /**
     *
     * @return the templateID
     */
    public String getTemplateID() {
        return templateID;
    }
}
