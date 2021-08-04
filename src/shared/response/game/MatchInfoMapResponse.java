package shared.response.game;

import shared.response.Response;

import java.util.Map;

/**
 * MatchInfoMapResponse Class
 */
public class MatchInfoMapResponse extends Response {

    private final Map<String, String> idToTitle;

    /**
     * MatchInfoMapResponse Constructor
     * @param sessionID of the session
     * @param idToTitle map of IDs and Titles
     */
    public MatchInfoMapResponse(String sessionID, Map<String, String> idToTitle) {
        super(sessionID);
        this.idToTitle = idToTitle;
    }

    /**
     *
     * @return the map idToTitle
     */
    public Map<String, String> getIdToTitle() {
        return idToTitle;
    }
}
