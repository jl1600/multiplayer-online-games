package shared.response.user;

import shared.constants.UserRole;
import shared.response.Response;

/**
 * UserRoleResponse Class
 */
public class UserRoleResponse extends Response {
    UserRole userRole;

    /**
     * UserRoleResponse Class
     * @param sessionID of the session
     * @param userRole of the user
     */
    public UserRoleResponse(String sessionID, UserRole userRole) {
        super(sessionID);
        this.userRole = userRole;
    }

    /**
     *
     * @return the userRole
     */
    public UserRole getUserRole() {
        return userRole;
    }
}
