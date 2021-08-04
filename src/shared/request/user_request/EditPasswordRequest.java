package shared.request.user_request;

/**
 * EditPasswordRequest Class
 */
public class EditPasswordRequest extends UserRequest {
    private final String userID;
    private final String password;
    private final String newPassword;

    /**
     * EditPasswordRequest Constructor
     * @param sessionID of the session
     * @param userID of the user
     * @param password of the user
     * @param newPassword of the user
     */
    public EditPasswordRequest(String sessionID, String userID, String password, String newPassword) {
        super(sessionID);
        this.password = password;
        this.newPassword = newPassword;
        this.userID = userID;
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
     * @return the newPassword of the user
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     *
     * @return the userID of the user
     */
    public String getUserID() {
        return userID;
    }
}
