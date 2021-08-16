package system.use_cases.builders;

import shared.exceptions.use_case_exceptions.InsufficientInputException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.entities.game.Game;

public abstract class GameInteractiveBuilder {

    private final String creatorID;

    protected boolean readyToBuild;

    /**
     * Returns whether the builder has collected enough input to build the object.
     *
     * */
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
     * */
    public abstract String getDesignQuestion();
    public GameInteractiveBuilder(String creatorName) {
        this.creatorID = creatorName;
    }

    public String getCreatorID(){
        return creatorID;
    }

    /**
     * Returns the Game object that this builder has been building.
     *
     * @param id The id to be assigned to the newly created Game object.
     * */
    public abstract Game build(String id) throws InsufficientInputException;
}
