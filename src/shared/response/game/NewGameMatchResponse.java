package shared.response.game;

import shared.response.Response;

/**
 * NewGameMatchResponse Class
 */
public class NewGameMatchResponse extends Response {

    private final String matchID;
    private final String text;

    /**
     * NewGameMatchResponse Constructor
     * @param sessionID of the session
     * @param matchID of the match
     * @param text of the match
     */
    public NewGameMatchResponse(String sessionID, String matchID, String text) {
        super(sessionID);
        this.matchID = matchID;
        this.text = text;
    }

    /**
     *
     * @return the matchID
     */
    public String getMatchID() {
        return matchID;
    }

    /**
     *
     * @return the text
     */
    public String getText() {
        return text;
    }
}
