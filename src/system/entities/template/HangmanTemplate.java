package system.entities.template;

/**
 * Hangman Game Template
 */
public class HangmanTemplate extends Template {

    private boolean haveHints;
    /**
     * Hangman Template Constructor
     */
    public HangmanTemplate() {
        super();
    }

    public void setHaveHints(boolean haveHints) {
        this.haveHints = haveHints;
    }

    public boolean haveHints() {
        return haveHints;
    }
}
