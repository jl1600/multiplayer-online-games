package shared.DTOs.Requests;

/**
 * Content required to perforn ban user request
 */
public class BanUserRequestBody {
    /**
     * the subject's user id
     */
    public String userID;
    /**
     * the ban length, in days
     */
    public int banLength;
    /**
     * the admin who will be performing this ban
     */
    public String adminID;
}
