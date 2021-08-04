package shared.request.user_request;

/**
 * LogoutRequest Class
 */
public class LogoutRequest extends UserRequest {
    private final String userId;

    /**
     * LogoutRequest Constructor
     * @param sessionID of the session
     * @param userId of the user
     */
    public LogoutRequest(String sessionID, String userId) {
        super(sessionID);
        this.userId = userId;
    }

    /**
     *
     * @return the userId of a user
     */
    public String getUserId() {
        return userId;
    }
}
