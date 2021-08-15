package shared.DTOs.Responses;

import shared.constants.GameAccessLevel;
import shared.constants.GameGenre;

public class GameDataResponseBody {
    public String id;
    public String ownerName;
    public String title;
    public GameAccessLevel accessLevel;
    public GameAccessLevel previousAccessLevel;
    public GameGenre genre;
}
