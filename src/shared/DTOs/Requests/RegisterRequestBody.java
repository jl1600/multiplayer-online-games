package shared.DTOs.Requests;

import shared.constants.UserRole;

/**
 * Content used to perform register request
 */
public class RegisterRequestBody {
    /**
     * the submitted username
     */
    public String username;
    /**
     * the submitted password
     */
    public String password;
    /**
     * the submitted email
     */
    public String email;
    /**
     * the selected username
     */
    public UserRole role;
}
