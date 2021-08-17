package system.entities.game;

import shared.constants.GameAccessLevel;
import shared.constants.GameGenre;
import shared.exceptions.entities_exception.IDAlreadySetException;

/**
 * Abstract Game Class
 */
public abstract class Game {

    protected String gameId;
    protected String ownerId;
    protected String templateId;
    protected GameAccessLevel gameAccessLevel;
    protected GameAccessLevel previousGameAccessLevel;
    private String title;

    /**
     * Game Constructor
     */
    public Game() {
        gameId = null;
        gameAccessLevel = GameAccessLevel.PRIVATE;
        previousGameAccessLevel = gameAccessLevel;
    }

    /**
     * set game ID
     * @param id to be assigned to the game
     */
    public void setID(String id) {
        if (id != null) {
            if (this.gameId != null) {
                    throw new IDAlreadySetException();
            }
            this.gameId = id;
        }
    }

    /**
     * get game ID
     * @return the ID assigned to the game
     */
    public String getID() {
        if (gameId == null) {
                throw new IDAlreadySetException();
        }
        return gameId;
    }

    /**
     *
     * @param id of the template
     */
    public void setTemplateID(String id) {
        this.templateId = id;
    }

    /**
     *
     * @return the template ID
     */
    public String getTemplateID() {
        return templateId;
    }

    /**
     * set title of game
     * @param title of the Game
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return the title of the Game
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * set template id of game
     * @param id of the Template
     * @return True indicating it's success
     */
    public boolean setTemplateId(String id) {
        this.templateId = id;
        return true;
    }

    /**
     *
     * @return owner ID of game
     */
    public String getOwnerId() {
        return this.ownerId;
    }

    /**
     * set game owner id
     * @param id to be set as Owner of the Game
     * @return True indicating success
     */
    public boolean setOwnerId(String id) {
        this.ownerId = id;
        return true;
    }

    /**
     *
     * @return boolean if game is public
     */
    public GameAccessLevel getGameAccessLevel() {
        return this.gameAccessLevel;
    }

    /**
     * set the game access level
     * @param b boolean indicating public or not
     * @return True indicating success
     */
    public void setGameAccessLevel(GameAccessLevel b) {
        setPreviousGameAccessLevel(this.gameAccessLevel);
        this.gameAccessLevel = b;
    }

    /**
     *
     * @return boolean indicating if Game is public
     */
    public boolean isPublic(){
        if (this.gameAccessLevel.equals(GameAccessLevel.PUBLIC)){
            return true;
        }
        return false;
    }

    public GameAccessLevel getPreviousGameAccessLevel() {
        return previousGameAccessLevel;
    }

    public void setPreviousGameAccessLevel(GameAccessLevel previousGameAccessLevel) {
        this.previousGameAccessLevel = previousGameAccessLevel;
    }

    /**
     * Return the genre or type of this game
     * */
    public abstract GameGenre getGenre();
}
