package shared.DTOs.Requests;

/**
 * UndoAccessLevelRequestBody Class
 * The content used to perform revert access level request
 */
public class UndoAccessLevelRequestBody {
    /**
     * the id of the game that will have access level reverted
     */
    public String gameID;
    /**
     * the id of the user who request this revert
     */
    public String userID;
}
