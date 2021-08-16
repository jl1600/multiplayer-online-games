package shared.DTOs.Requests;

/**
 * PasswordResetRequestBody Class
 * Content used to perform password reset request
 */
public class PasswordResetRequestBody {
    /**
     * the submitted userID
     */
    public String userID;
    /**
     * the submitted email
     */
    public String email;
}
