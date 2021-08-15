package system.entities.game.hangman;

import shared.exceptions.use_case_exceptions.InsufficientInputException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.entities.game.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Hangman Game
 */
public class HangmanGame extends Game {
    //private String title;
    private List<List<String>> puzzles;

    /**
     * Hangman Game Constructor
     */
    public HangmanGame() {
        super();
        this.puzzles = new ArrayList<>();
    }

    /**
     * add a puzzle
     *
     * @param answer the puzzle to be added
     * @param prompt the prompt of the puzzle
     * @throws InsufficientInputException when parameters are illegal and passed a null value
     */
    public void addPuzzle(String answer, String prompt) throws InsufficientInputException {
        if (answer == null | prompt == null) {
            throw new InsufficientInputException();
        }
        List<String> combined = new ArrayList<>();
        combined.add(answer);
        combined.add(prompt);
        this.puzzles.add(combined);
    }

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
        List<String> result = new ArrayList<>();
        result.addAll(this.puzzles.get(index));
        return result;
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
