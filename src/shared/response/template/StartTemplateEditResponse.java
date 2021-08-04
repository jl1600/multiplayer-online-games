package shared.response.template;

import shared.response.Response;

import java.util.Map;

/**
 * StartTemplateEditResponse Class
 */
public class StartTemplateEditResponse extends Response {

    Map<String, String> attributeMap;

    /**
     * StartTemplateEditResponse Constructor
     * @param sessionID of the session
     * @param attributeMap of the template
     */
    public StartTemplateEditResponse(String sessionID, Map<String, String> attributeMap) {
        super(sessionID);
        this.attributeMap = attributeMap;
    }

    /**
     *
     * @return the attributeMap
     */
    public Map<String, String> getAttributeMap() {
        return attributeMap;
    }
}
