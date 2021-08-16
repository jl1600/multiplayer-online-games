package system.entities.template;

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

    public HangmanTemplate(HangmanTemplate template){
        super(template);
        this.hasHints = template.hasHints();
    }

    public void setHasHints(boolean haveHints) {
        this.hasHints = haveHints;
    }

    public boolean hasHints() {
        return hasHints;
    }
}
