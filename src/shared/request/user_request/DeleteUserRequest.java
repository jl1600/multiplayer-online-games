package shared.request.user_request;

/**
 * DeleteUserRequest Class
 */
public class DeleteUserRequest extends UserRequest {
    private final String password;
    private final String userId;

    /**
     * DeleteUserRequest Constructor
     * @param sessionID of the session
     * @param userId of the user
     * @param password of the user
     */
    public DeleteUserRequest(String sessionID, String userId, String password) {
        super(sessionID);
        this.password = password;
        this.userId = userId;
    }

    /**
     *
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @return the userId of the user
     */
    public String getUserId() {
        return userId;
    }
}
