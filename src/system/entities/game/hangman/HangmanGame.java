package system.entities.game.hangman;

import shared.exceptions.use_case_exceptions.InsufficientInputException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.entities.game.Game;

import java.util.ArrayList;

/** Hangman Game
 *
 */
public class HangmanGame extends Game {
    private String title;
    private ArrayList<ArrayList<String>> puzzles;

    /** Hangman Game Constructor
     *
     */

    public HangmanGame() {
        super();
        this.puzzles = new ArrayList<>();
    }

    /** set title of Hangman game
     *
     * @param title the title of the Hangman Game
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /** Get the title of Hangman Game
     *
     * @return the tile of the game
     */
    public String getTitle() {
        return this.title;
    }

    /** add a puzzle
     *
     * @param puzzle the puzzle to be added
     * @param prompt the prompt of the puzzle
     * @throws InsufficientInputException when parameters are illegal and passed a null value
     */
    public void addPuzzle(String puzzle, String prompt) throws InsufficientInputException {
        assert puzzle != null;
        if (puzzle == null | prompt == null) {
            throw new InsufficientInputException();
        }
        ArrayList<String> combined = new ArrayList<>();
        combined.add(puzzle);
        combined.add(prompt);
        this.puzzles.add(combined);
    }

    /**
     * Get a puzzle
     * @param index of puzzle to add
     * @return a String ArrayList representative of the Puzzle
     * @throws InvalidInputException when the index is larger than total puzzle size
     */
    public ArrayList<String> getPuzzle(int index) throws InvalidInputException {
        if (index >= this.puzzles.size()) {
            throw new InvalidInputException();
        }
        ArrayList<String> result = new ArrayList<>();
        result.addAll(this.puzzles.get(index));
        return result;
    }

    /**
     * Remove a puzzle
     * @param index of puzzle to remove
     * @throws InvalidInputException when the index is larger than total puzzle size
     */
    public void removePuzzle(int index) throws InvalidInputException {
        if (index >= this.puzzles.size()) {
            throw new InvalidInputException();
        }
        this.puzzles.remove(index);
    }

    /**
     * Get All Puzzles
     * @return Nested ArrayList that represents all the puzzles
     */
    public ArrayList<ArrayList<String>> getPuzzles() {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for (ArrayList<String> set : this.puzzles) {
            ArrayList<String> entry = new ArrayList<>();
            entry.addAll(set);
            result.add(entry);
        }
        return result;
    }

}
