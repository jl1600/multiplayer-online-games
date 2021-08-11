package system.use_cases.game_matches;

import shared.exceptions.use_case_exceptions.*;
import system.entities.game.Game;
import system.entities.game.hangman.HangmanGame;
import system.entities.template.HangmanTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class HangmanMatch extends GameMatch {

    private HangmanGame game;
    private HangmanTemplate template;

    private int currentPuzzleIndex;
    private char[] currentAnswer;
    private char[] gameState;
    private int remainingLives;
    private int remainingHints;
    private List<Character> mistakes;
    private List<Character> guesses;
    private String output;

    private enum MoveType {HINT, INVALID, USED, NORMAL}

    private final String[] OUTPUT_STRINGS = {
            " invalid character. Try again."
            , " already guessed. Try again."
            , "No more hints remaining. Try again."
            , "You Lose! Better luck next time."
            , "YOU WIN!!!"
            , "Puzzle complete!"
            , "Next puzzle:"
            , "wrong guesses: "
            , "lives left: "
            , "hints left: "
            , "Hint used, "
            , " found!"
            , " not found."
    };

    public HangmanMatch(String matchID, String userID, HangmanGame game, HangmanTemplate template) {
        super(matchID, userID);
        this.template = template;
        this.game = game;
        this.remainingLives = template.getNumLives();
        this.remainingHints = template.getNumHints();
        this.guesses = new ArrayList<>();
        this.mistakes = new ArrayList<>();

        this.currentPuzzleIndex = 0;
        this.loadPuzzle();
        output = this.gameStatus();
    }

    /**
     * Returns the current game state as a string.
     *
     * @param playerID The unique string identifier of the player.
     * @return the current game state as a string.
     */
    @Override
    public String getTextContent(String playerID) {
        return output;
    }

    /**
     * Return the HangmanGame object inside this GameMatch.
     *
     * @return the HangmanGame object inside this GameMatch.
     */
    @Override
    public Game getGame() {
        return this.game;
    }


    @Override
    public void addPlayer(String playerID) {

    }

    /**
     * Advances the game state according to the input move.
     * <p>
     * The first alphanumeric character of the input is taken as the guessed letter.
     * "hint" is a special phrase that will consume a remaining hint and reveal an alphanumeric
     * character in the Answer.
     *
     * @param PlayerID the userId of the player making this move
     * @param move     The string representing the player input
     */
    @Override
    public void playMove(String PlayerID, String move) {
        char moveChar = Character.toLowerCase(move.charAt(0));
        switch (parseMove(move)) {
            case INVALID:
                output = "'" + moveChar + "'" + OUTPUT_STRINGS[0] + "\n\n"
                        + this.gameStatus();
                return;
            case USED:
                output = "'" + moveChar + "'" + OUTPUT_STRINGS[1] + "\n\n"
                        + this.gameStatus();
                return;
            case HINT:
                if (this.remainingHints > 0) {
                    remainingHints--;
                    guessChar(getHint(), MoveType.HINT);
                } else {
                    output = OUTPUT_STRINGS[2] + "\n\n"
                            + this.gameStatus();
                }
                return;
            case NORMAL:
                guessChar(moveChar, MoveType.NORMAL);
                return;
        }
    }

    private MoveType parseMove(String move) {
        char moveChar = Character.toLowerCase(move.charAt(0));

        if (Pattern.matches(".*hint.*", move.toLowerCase())) {
            return MoveType.HINT;
        } else if (!Pattern.matches("[a-zA-Z0-9]", move.substring(0, 1))) {
            return MoveType.INVALID;
        } else if (guesses.contains(moveChar)) {
            return MoveType.USED;
        }
        return MoveType.NORMAL;
    }

    //returns a hidden letter in the current puzzle
    private char getHint() {
        List<Character> remaining = new ArrayList<>();
        for (int i = 0; i < gameState.length; i++) {
            if (gameState[i] == '_') {
                remaining.add(currentAnswer[i]);
            }
        }
        Collections.shuffle(remaining);
        return Character.toLowerCase(remaining.get(0));
    }

    // searches the current puzzle for the input char. deducts a life if nothing is found
    private void guessChar(char moveChar, MoveType type) {
        this.guesses.add(moveChar);
        int found = findAndRevealChar(moveChar);
        if (found == 0) {
            this.mistakes.add(moveChar);
            this.remainingLives--;
        }
        if (remainingLives == 0) {
            this.setFinished(true);
            output = OUTPUT_STRINGS[3] + "\n"
                    + this.gameStatus();
        } else if (this.isPuzzleSolved()) {
            if (this.hasNextPuzzle()) {
                currentPuzzleIndex++;
                this.loadPuzzle();
                output = OUTPUT_STRINGS[5] + "\n"
                        + game.getAnswer(currentPuzzleIndex - 1) + "\n\n"
                        + OUTPUT_STRINGS[6] + "\n"
                        + this.gameStatus();
            } else {
                this.setFinished(true);
                output = OUTPUT_STRINGS[4] + "\n"
                        + this.gameStatus();
            }
        } else if (type == MoveType.HINT) {     //if a hint was used
            output = OUTPUT_STRINGS[10] + found + " '" + moveChar + "'" + OUTPUT_STRINGS[11] + "\n\n"
                    + this.gameStatus();
        } else if (found > 0) {     // if the guessed letter was found
            output = "" + found + " '" + moveChar + "'" + OUTPUT_STRINGS[11] + "\n\n"
                    + this.gameStatus();
        } else {    // if nothing was found
            output = "'" + moveChar + "'" + OUTPUT_STRINGS[12] + "\n\n"
                    + this.gameStatus();
        }
    }

    // searches the current puzzle for the input char (not case sensitive)
    // uncovers any that are found, and returns the amount of uncovered letters.
    private int findAndRevealChar(char c) {
        int found = 0;
        if (c >= '0' && c <= '9') {  // 0-9
            for (int i = 0; i < this.currentAnswer.length; i++) {
                if (this.currentAnswer[i] == c) {
                    this.gameState[i] = this.currentAnswer[i];
                    found++;
                }
            }
        } else if (c >= 'a' && c <= 'z') {  // a-z
            for (int i = 0; i < this.currentAnswer.length; i++) {
                if (this.currentAnswer[i] == c | this.currentAnswer[i] == c - 32) { //treat 'a' and 'A' as the same
                    this.gameState[i] = this.currentAnswer[i];
                    found++;
                }
            }
        }
        return found;
    }

    private boolean isPuzzleSolved() {
        for (int i = 0; i < currentAnswer.length; i++) {
            if (currentAnswer[i] != gameState[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean hasNextPuzzle() {
        return currentPuzzleIndex + 1 < this.template.getNumPuzzles();
    }

    private void loadPuzzle() {
        mistakes.clear();
        guesses.clear();
        String answerString = game.getAnswer(currentPuzzleIndex);
        currentAnswer = answerString.toCharArray();
        String gameStateString = answerString.replaceAll("[a-zA-Z0-9]", "_");
        gameState = gameStateString.toCharArray();
    }

    private String gameStatus() {
        return "(" + (currentPuzzleIndex + 1) + "/" + this.template.getNumPuzzles() + ") "
                + game.getPrompt(currentPuzzleIndex) + "\n"
                + "\n"
                + String.valueOf(gameState) + "\n"
                + "\n"
                + OUTPUT_STRINGS[7] + mistakes.toString() + "\n"
                + OUTPUT_STRINGS[8] + remainingLives + "\n"
                + OUTPUT_STRINGS[9] + remainingHints;
    }
}
