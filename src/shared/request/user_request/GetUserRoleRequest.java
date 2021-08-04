package shared.request.user_request;

/**
 * GetUserRoleRequest Class
 */
public class GetUserRoleRequest extends UserRequest{
    private final String userId;

    /**
     * GetUserRoleRequest Constructor
     * @param sessionID of the session
     * @param userId of the user
     */
    public GetUserRoleRequest(String sessionID, String userId) {
        super(sessionID);
        this.userId = userId;
    }

    /**
     *
     * @return the userId of the user
     */
    public String getUserId() {
        return userId;
    }
}
