package shared.DTOs.Requests;

/**
 * PasswordResetRequestBody Class
 * Content used to perform password reset request
 */
public class PasswordResetRequestBody {
    /**
     * the submitted userID
     */
    public String username;
    /**
     * the submitted email
     */
    public String email;
}
