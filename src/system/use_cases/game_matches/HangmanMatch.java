package system.use_cases.game_matches;

import shared.exceptions.use_case_exceptions.*;
import system.entities.game.Game;
import system.entities.game.hangman.HangmanGame;
import system.entities.template.HangmanTemplate;
import system.use_cases.builders.normal_builders.HangmanGameBuilder;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class HangmanMatch extends GameMatch {

    private final HangmanGame game;
    private final HangmanTemplate template;

    private final ArrayList<Character> mistakes;
    private final ArrayList<Character> guesses;
    private final char[] puzzle;
    private final char[] gameState;
    private final String prompt;
    private int remainingLives;
    private final int remainingHints;
    private String output;


    public HangmanMatch(String matchID, String userID, HangmanGame game, HangmanTemplate template) throws InvalidInputException {
        super(matchID, userID);
        this.template = template;
        this.game = game;
        this.remainingLives = template.getNumLives();
        this.remainingHints = template.getNumHints();
        this.guesses = new ArrayList<>();
        this.mistakes = new ArrayList<>();

        this.prompt = game.getPuzzle(0).get(1);

        String puzzleString = game.getPuzzle(0).get(0);
        this.puzzle = puzzleString.toCharArray();
        String gameStateString = puzzleString.replaceAll("[a-zA-Z0-9]", "_");
        this.gameState = gameStateString.toCharArray();

        output = prompt
                + "\n" + String.valueOf(gameState)
                + "\nguesses: " + mistakes.toString()
                + "\nlives: " + remainingLives
                + "\nhints: " + remainingHints;
    }


    @Override
    public String getTextContent(String playerID) throws InvalidUserIDException {
        return output;
    }

    @Override
    public Game getGame() {
        return this.game;
    }

    @Override
    public void addPlayer(String playerID) throws DuplicateUserIDException {

    }

    @Override
    public void playMove(String PlayerID, String move) throws InvalidUserIDException, InvalidInputException {
        char moveChar = Character.toLowerCase(move.charAt(0));
        String move1 = move.substring(0, 1);

        if (!Pattern.matches("[a-zA-Z0-9]", move1)) {
            return;
        }

        if (this.guesses.contains(moveChar)) { // if the letter has already been guessed
            return;
        }

        guessChar(moveChar);

        if (this.remainingLives == 0) {
            this.setFinished(true);
            output = "YOU LOSE!!!"
                    + "\n" + String.valueOf(gameState)
                    + "\nguesses: " + mistakes.toString()
                    + "\nlives: " + remainingLives
                    + "\nhints: " + remainingHints;
        } else if (this.isSolved()) {
            this.setFinished(true);
            output = "YOU WIN!!!"
                    + "\n" + String.valueOf(gameState)
                    + "\nguesses: " + mistakes.toString()
                    + "\nlives: " + remainingLives
                    + "\nhints: " + remainingHints;
        } else {
            output = prompt
                    + "\n" + String.valueOf(gameState)
                    + "\nguesses: " + mistakes.toString()
                    + "\nlives: " + remainingLives
                    + "\nhints: " + remainingHints;
        }
    }

    private void guessChar(char c) {
        this.guesses.add(c);
        boolean found = false;
        if (c >= '0' && c <= '9') {  // 0-9
            for (int i = 0; i < this.puzzle.length; i++) {
                if (this.puzzle[i] == c) {
                    this.gameState[i] = this.puzzle[i];
                    found = true;
                }
            }
        } else if (c >= 'a' && c <= 'z') {  // a-z
            for (int i = 0; i < this.puzzle.length; i++) {
                if (this.puzzle[i] == c | this.puzzle[i] == c - 32) { //treat 'a' and 'A' as the same
                    this.gameState[i] = this.puzzle[i];
                    found = true;
                }
            }
        }
        if (!found) {
            this.mistakes.add(c);
            this.remainingLives--;
        }
    }

    private boolean isSolved() {
        for (int i = 0; i < puzzle.length; i++) {
            if (puzzle[i] != gameState[i]) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws InsufficientInputException, CreationInProgressException, InvalidInputException, InvalidUserIDException {

        HangmanGameBuilder builder = new HangmanGameBuilder();
        builder.setGameId("hangmanGameId3432141");
        builder.setTemplateId("templ8-3421");
        builder.setOwnerId("zach01111");
        builder.setIsPublic(true);
        builder.addPuzzle("Star Wars - Episode 1: The Phantom Menace", "The one with JarJar");
        builder.setTitle("Zach's Starwars Hangman Game");
        HangmanGame game = builder.toHangmanGame();

        HangmanTemplate t = new HangmanTemplate();
        t.setNumHints(1);
        t.setNumLives(3);
        t.setNumPuzzles(1);

        HangmanMatch hm = new HangmanMatch("1", "1", game, t);

        String[] test1 = {
                "a", "b", "c", "r", "ab", "a", "$"
                , "s", "t", "e", "i", "t", "n", "o", "p", "A", "z", "q"
        };


        System.out.println(hm.getTextContent("1"));
        System.out.println();
        int i = 0;
        while (!hm.isFinished()) {
            String str = test1[i];
            System.out.println("move: " + str);
            hm.playMove("1", str);
            System.out.println(hm.getTextContent("1"));
            System.out.println();
            i++;
        }

    }
}
