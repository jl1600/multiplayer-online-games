package shared.DTOs.Requests;

import shared.constants.UserRole;

/**
 * RegisterRequestBody Class
 * Content used to perform register request
 */
public class RegisterRequestBody {

    /**
     * The ID if this request is to promote a trial user.
     * */
    public String userID;
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
