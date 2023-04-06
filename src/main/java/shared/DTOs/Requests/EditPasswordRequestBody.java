package shared.DTOs.Requests;

/**
 * EditPasswordRequestBody Class
 * Content used to perform edit password request
 */
public class EditPasswordRequestBody {
    /**
     * the user which will have their password edited
     */
    public String userID;
    /**
     * the old password to check if they know about the old password,
     * temp password can be accepted too for reset password
     */
    public String oldPassword;
    /**
     * the new password
     */
    public String newPassword;

}
