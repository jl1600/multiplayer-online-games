package shared.DTOs.Responses;

import shared.constants.GameAccessLevel;
import shared.constants.GameGenre;

/**
 * GameDataResponseBody
 */
public class GameDataResponseBody {
    /**
     * id of the game
     */
    public String id;
    /**
     * username of owner
     */
    public String ownerName;
    /**
     * the title of the game
     */
    public String title;
    /**
     * the game's access level
     */
    public GameAccessLevel accessLevel;
    /**
     * the game's previous access level
     */
    public GameAccessLevel previousAccessLevel;
    /**
     * the genre of the game
     */
    public GameGenre genre;
}
