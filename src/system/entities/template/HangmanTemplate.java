package system.entities.template;
import shared.exceptions.use_case_exceptions.InsufficientInputException;

/**
 * Hangman Game Template
 */
public class HangmanTemplate extends Template {
    private int numPuzzles;
    private int numLives;
    private int numHints;

    /**
     * Hangman Template Constructor
     */
    public HangmanTemplate() {
        super();
    }

    /**
     * @param numPuzzles number of puzzles
     * @throws InsufficientInputException when numPuzzles is illegal and less than 1
     */
    public void setNumPuzzles(int numPuzzles) throws InsufficientInputException {
        if (numPuzzles < 1) {
            throw new InsufficientInputException();
        }
        this.numPuzzles = numPuzzles;
    }

    /**
     * @return number of puzzles
     */
    public int getNumPuzzles() {
        return this.numPuzzles;
    }

    /**
     * @param numLives number of lives
     * @throws InsufficientInputException when numLives is illegal and less than 1
     */
    public void setNumLives(int numLives) throws InsufficientInputException {
        if (numLives < 1) {
            throw new InsufficientInputException();
        }
        this.numLives = numLives;
    }

    /**
     * @return number of lives
     */
    public int getNumLives() {
        return this.numLives;
    }

    /**
     * @param numHints number of hints
     * @throws InsufficientInputException when numHints is illegal and less than 0
     */
    public void setNumHints(int numHints) throws InsufficientInputException {
        if (numHints < 0) {
            throw new InsufficientInputException();
        }
        this.numHints = numHints;
    }

    /**
     * @return number of hints
     */
    public int getNumHints() {
        return this.numHints;
    }
}
