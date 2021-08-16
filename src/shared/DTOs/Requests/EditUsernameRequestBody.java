package shared.DTOs.Requests;

/**
 * content to perform edit user request
 */
public class EditUsernameRequestBody {
    /**
     * the subject user's userID
     */
    public String userID;
    /**
     * the new username
     */
    public String newUsername;
}
