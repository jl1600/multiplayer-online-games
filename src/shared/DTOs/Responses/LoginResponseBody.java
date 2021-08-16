package shared.DTOs.Responses;

import shared.constants.UserRole;

/**
 * LoginResponseBody Class
 */
public class LoginResponseBody {
    /**
     * the id of the user who has logged in
     */
    public String userID;
    /**
     * the role of the user who has logged in
     */
    public UserRole role;
}
