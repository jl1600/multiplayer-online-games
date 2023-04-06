package system.entities.game.hangman;

import shared.constants.GameGenre;
import shared.exceptions.use_case_exceptions.NotReadyException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.entities.game.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Hangman Game
 */
public class HangmanGame extends Game {
    private List<List<String>> puzzles;
    private int numPuzzles;
    private int numLives;
    private int numHints;

    /**
     * Hangman Game Constructor
     */
    public HangmanGame() {
        super();
        this.puzzles = new ArrayList<>();
    }

    @Override
    public GameGenre getGenre() {
        return GameGenre.HANGMAN;
    }

    /**
     * add a puzzle
     *
     * @param answer the puzzle to be added
     * @param prompt the prompt of the puzzle
     * @throws NotReadyException when parameters are illegal and passed a null value
     */
    public void addPuzzle(String answer, String prompt) throws InvalidInputException {
        if (answer == null | prompt == null) {
            throw new InvalidInputException();
        }
        List<String> combined = new ArrayList<>();
        combined.add(answer);
        combined.add(prompt);
        this.puzzles.add(combined);
    }

    /**
     * add a answer
     * @param puzzleIndex the index of the puzzle
     * @param answer the answer to be added
     * @throws InvalidInputException when parameters are illegal and passed a null value
     */
    public void addAnswer(int puzzleIndex, String answer) throws InvalidInputException {
        if (answer == null | answer.equals("")) {
            throw new InvalidInputException();
        }
        if (puzzleIndex >= puzzles.size()) {
            List<String> newPuzzle = new ArrayList<>();
            newPuzzle.add(0, answer);
            newPuzzle.add(1, "no prompt");
            puzzles.add(newPuzzle);
        } else {
            puzzles.get(puzzleIndex).set(0, answer);
        }

    }

    /**
     * add a prompt
     * @param puzzleIndex the current puzzle index
     * @param prompt prompt to be added
     * @throws InvalidInputException when parameters are illegal and passed a null value
     */
    public void addPrompt(int puzzleIndex, String prompt) throws InvalidInputException {
        if (prompt == null | prompt.equals("")) {
            throw new InvalidInputException();
        }
        if (puzzleIndex >= puzzles.size()) {
            List<String> newPuzzle = new ArrayList<>();
            newPuzzle.add(0, "no answer");
            newPuzzle.add(1, prompt);
            puzzles.add(newPuzzle);
        } else {
            puzzles.get(puzzleIndex).set(1, prompt);
        }

    }

    /**
     * set the number of puzzles
     * @param numPuzzles the desired number of puzzles
     * @throws InvalidInputException when parameters are illegal and passed a null value
     */
    public void setNumPuzzles(int numPuzzles) throws InvalidInputException {
        if (numPuzzles < 1) {
            throw new InvalidInputException();
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
     * @throws InvalidInputException when numLives is illegal and less than 1
     */
    public void setNumLives(int numLives) throws InvalidInputException {
        if (numLives < 1) {
            throw new InvalidInputException();
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
     * @throws InvalidInputException when numHints is illegal and less than 0
     */
    public void setNumHints(int numHints) throws InvalidInputException {
        if (numHints < 0) {
            throw new InvalidInputException();
        }
        this.numHints = numHints;
    }

    /**
     * @return number of hints
     */
    public int getNumHints() {
        return this.numHints;
    }

    /**
     * Get a puzzle
     *
     * @param index of puzzle to add
     * @return a String ArrayList representative of the Puzzle
     * @throws InvalidInputException when the index is larger than total puzzle size
     */
    public List<String> getPuzzle(int index) throws InvalidInputException {
        if (index >= this.puzzles.size()) {
            throw new InvalidInputException();
        }
        return new ArrayList<>(this.puzzles.get(index));
    }

    public String getAnswer(int puzzleIndex) {
        return puzzles.get(puzzleIndex).get(0);
    }

    public String getPrompt(int puzzleIndex) {
        return puzzles.get(puzzleIndex).get(1);
    }


    /**
     * Remove a puzzle
     *
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
     *
     * @return Nested ArrayList that represents all the puzzles
     */
    public List<List<String>> getPuzzles() {
        List<List<String>> result = new ArrayList<>();
        for (List<String> set : this.puzzles) {
            ArrayList<String> entry = new ArrayList<>();
            entry.addAll(set);
            result.add(entry);
        }
        return result;
    }

}
