package system.entities.template;

import shared.constants.GameGenre;

/**
 * Hangman Game Template
 */
public class HangmanTemplate extends Template {

    private boolean hasHints;
    /**
     * Hangman Template Constructor
     */
    public HangmanTemplate() {
        super();
        this.hasHints = false;
        setTitle("Unnamed Hangman Template");
    }

    @Override
    public GameGenre getGenre() {
        return GameGenre.HANGMAN;
    }

    /**
     * Constructor of Hangman Template with a given template
     * @param template the input template
     */
    public HangmanTemplate(HangmanTemplate template){
        super(template);
        this.hasHints = template.hasHints();
    }

    /**
     * set the state of there has or no Hints
     * @param haveHints the current state of whether there has/have Hint or not
     */
    public void setHasHints(boolean haveHints) {
        this.hasHints = haveHints;
    }

    /**
     * @return has hints or not
     */
    public boolean hasHints() {
        return hasHints;
    }
}
