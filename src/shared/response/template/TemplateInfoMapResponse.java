package shared.response.template;

import shared.response.Response;

import java.util.Map;

/**
 * TemplateInfoMapResponse Class
 */
public class TemplateInfoMapResponse extends Response {

    private final Map<String, String> idToTitle;

    /**
     * TemplateInfoMapResponse Constructor
     * @param sessionID of the session
     * @param idToTitle map of IDs to Titles
     */
    public TemplateInfoMapResponse(String sessionID, Map<String, String> idToTitle) {
        super(sessionID);
        this.idToTitle = idToTitle;
    }

    /**
     *
     * @return the map idToTitle
     */
    public Map<String, String> getIdToTitleMap() {
        return idToTitle;
    }
}
