package system.use_cases.builders.interactive_builders;

import shared.exceptions.use_case_exceptions.InsufficientInputException;
import system.entities.game.Game;

public abstract class GameInteractiveBuilder extends InteractiveBuilder {

    private final String creatorID;

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
