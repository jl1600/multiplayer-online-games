package shared.response.user;

import shared.response.Response;

/**
 * LoginResponse Class
 */
public class LoginResponse extends Response {
    private final String userId;
    private final String text;

    /**
     * LoginResponse Constructor
     * @param sessionId of the session
     * @param text of the response
     * @param userId of the user
     */
    public LoginResponse(String sessionId, String text, String userId) {
        super(sessionId);
        this.userId = userId;
        this.text = text;
    }

    /**
     *
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     *
     * @return the text
     */
    public String getText() {
        return text;
    }
}
