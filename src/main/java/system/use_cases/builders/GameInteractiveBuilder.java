package system.use_cases.builders;

import shared.exceptions.use_case_exceptions.NotReadyException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.entities.game.Game;

/**
 * Abstract GameInteractiveBuilder Class
 */
public abstract class GameInteractiveBuilder {

    private final String creatorID;
    /**
     * is this game ready to be build
     */
    protected boolean readyToBuild;

    /**
     * Constructor of GameInteractiveBuilder
     * @param creatorName the id of the creator user
     */
    public GameInteractiveBuilder(String creatorName) {
        this.creatorID = creatorName;
    }

    /**
     * Checks whether the game object being built is fully specified
     * @return true if the object is fully formed, otherwise false
     */
    public boolean isReadyToBuild() {
        return readyToBuild;
    }

    /**
     * Fulfill the design input that this builder is currently looking for.
     * @param designChoice A String that represents the user input for the design of the creation.
     * @throws InvalidInputException When parameters are illegal and passed a null value
     * */
    public abstract void makeDesignChoice(String designChoice) throws InvalidInputException;

    /**
     * Returns a human-readable string that describe the input this interactive builder is looking for
     * in order to build the object.
     * @return the current design question string
     */
    public abstract String getDesignQuestion();

    /**
     * Returns the userID of the game creator.
     * @return the userID of the game creator
     */
    public String getCreatorID(){
        return creatorID;
    }

    /**
     * Returns the Game object this builder has been building.
     * @param id the gameID to be assigned to the game
     * @return the game object
     * @throws NotReadyException if the game is not fully formed
     */
    public abstract Game build(String id) throws NotReadyException;
}
