package shared.request.user_request;

/**
 * LoginRequest Class
 */
public class LoginRequest extends UserRequest {
    private final String username;
    private final String password;

    /**
     * LoginRequest Constructor
     * @param sessionID of the session
     * @param username of the user
     * @param password of the user
     */
    public LoginRequest(String sessionID, String username, String password) {
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
