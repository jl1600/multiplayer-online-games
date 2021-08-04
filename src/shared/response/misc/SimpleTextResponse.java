package shared.response.misc;

import shared.response.Response;

/**
 * SimpleTextResponse Class
 */
public class SimpleTextResponse extends Response {

    private final String text;

    /**
     * SimpleTextResponse Constructor
     * @param sessionID of the session
     * @param text of the response
     */
    public SimpleTextResponse(String sessionID, String text) {
        super(sessionID);
        this.text = text;
    }

    /**
     *
     * @return the text
     */
    public String getText() {
        return text;
    }
}
