package shared.DTOs.Requests;

import shared.constants.GameAccessLevel;

public class AccessLevelRequestBody {
    public String gameID;
    public String userID;
    public GameAccessLevel accessLevel;
}
