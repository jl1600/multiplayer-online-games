package system.use_cases.builders;

import shared.exceptions.use_case_exceptions.InsufficientInputException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.entities.game.Game;

public abstract class GameInteractiveBuilder {

    private final String creatorID;
    protected boolean readyToBuild;

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
     *
     * @param designChoice A String that represents the user input for the design of the creation.
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
     * @throws InsufficientInputException if the game is not fully formed
     */
    public abstract Game build(String id) throws InsufficientInputException;
}
