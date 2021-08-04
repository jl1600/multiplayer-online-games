package shared.request.user_request;

/**
 * EditUsernameRequest Class
 */
public class EditUsernameRequest extends UserRequest {
    private final String newUsername;
    private final String userId;

    /**
     * EditUsernameRequest Constructor
     * @param sessionID of the session
     * @param userId of the user
     * @param newUsername of the user
     */
    public EditUsernameRequest(String sessionID, String userId, String newUsername) {
        super(sessionID);
        this.newUsername = newUsername;
        this.userId = userId;
    }

    /**
     *
     * @return userId of the user
     */
    public String getUserId() { return userId; }

    /**
     *
     * @return newUsername of the user
     */
    public String getNewUsername() { return newUsername; }
}
