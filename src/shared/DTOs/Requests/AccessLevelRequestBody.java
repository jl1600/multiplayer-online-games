package shared.DTOs.Requests;

import shared.constants.GameAccessLevel;

/**
 * AccessLevelRequestBody Class
 * Contents needed to perform access level requests
 */
public class AccessLevelRequestBody {
    /**
     * the subject game id
     */
    public String gameID;
    /**
     * the subject user id
     */
    public String userID;
    /**
     * the desired access level
     */
    public GameAccessLevel accessLevel;
}
