package shared.request.user_request;

/**
 * NewAdminUserRequest Class
 */
public class NewAdminUserRequest extends UserRequest {
    private final String username;
    private final String password;

    /**
     * NewAdminUserRequest Constructor
     * @param sessionID of the session
     * @param username of the user
     * @param password of the user
     */
    public NewAdminUserRequest(String sessionID, String username, String password) {
        super(sessionID);
        this.username = username;
        this.password = password;
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
