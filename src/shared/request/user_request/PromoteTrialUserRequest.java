package shared.request.user_request;

/**
 * PromoteTrialUserRequest Class
 */
public class PromoteTrialUserRequest extends UserRequest {
    private final String userId;
    private final String username;
    private final String password;

    /**
     * PromoteTrialUserRequest Constructor
     * @param sessionID of the session
     * @param userId of the user
     * @param username of the user
     * @param password of the user
     */
    public PromoteTrialUserRequest(String sessionID, String userId, String username, String password) {
        super(sessionID);
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    /**
     *
     * @return the userId of the user
     */
    public String getUserId() {
        return userId;
    }

    /**
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }
}