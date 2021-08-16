package shared.DTOs.Requests;

/**
 * LeaveMatchRequestBody Class
 * Content used to perform a leave match request
 */
public class LeaveMatchRequestBody {
    /**
     * the leaving user's userID
     */
    public String userID;
    /**
     * the id of the match that user is leaving
     */
    public String matchID;
}
