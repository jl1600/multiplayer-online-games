package system.use_cases.builders.interactive_builders;

import shared.exceptions.use_case_exceptions.InvalidInputException;

public abstract class InteractiveBuilder {

    protected boolean readyToBuild;

    public InteractiveBuilder(){
        this.readyToBuild = false;
    }

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
}
