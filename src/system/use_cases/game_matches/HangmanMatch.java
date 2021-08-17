package system.use_cases.game_matches;

import java.lang.String;

import shared.constants.IDType;
import shared.constants.MatchStatus;
import shared.exceptions.use_case_exceptions.*;
import system.entities.game.hangman.HangmanGame;
import system.entities.template.HangmanTemplate;

import java.util.*;
import java.util.regex.Pattern;

/**
 * HangmanMatch Class
 */
public class HangmanMatch extends GameMatch {

    private final static int PLAYER_LIMIT = 4; //maximum allowed players
    private HangmanGame game;
    private HangmanTemplate template;

    private int currentPuzzleIndex;
    private char[] currentAnswer;
    private char[] gameState;
    private List<Character> mistakes;
    private List<Character> guesses;
    private String output;

    private List<String> players;
    private List<String> livingPlayers;
    private Map<String, String> playerNames;
    private Map<String, Integer> scores;
    private Map<String, Integer> remainingLives;
    private Map<String, Integer> remainingHints;
    private String activePlayerId;

    private enum MoveType {HINT, INVALID, USED, NORMAL}

    /**
     * Constructor of HangmanMatch
     * @param matchID the current match id
     * @param userID the host user id
     * @param username the host user name
     * @param game the game that is being used
     * @param template the template ths is being used
     */
    public HangmanMatch(String matchID, String userID, String username, HangmanGame game, HangmanTemplate template) {
        super(matchID, userID, username, PLAYER_LIMIT);
        this.template = template;
        this.game = game;
        this.guesses = new ArrayList<>();
        this.mistakes = new ArrayList<>();

        players = new ArrayList<>();
        playerNames = new HashMap<>();
        scores = new HashMap<>();
        remainingLives = new HashMap<>();
        remainingHints = new HashMap<>();

        try {
            addPlayer(userID, username);
        } catch (DuplicateUserIDException | MaxPlayerReachedException e) {
            e.printStackTrace();
        }

        this.currentPuzzleIndex = 0;
        this.loadPuzzle();
        output = this.simpleOutput();
        setChanged();
        notifyObservers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTextContent() {
        if (getStatus() == MatchStatus.PREPARING) {
            return "Waiting for the host to start the match...";
        }
        return this.output;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getAllPlayerStats() {
        Map<String, String> playerStatusMap = new HashMap<>();
        StringBuilder s = new StringBuilder();
        for (String playerId : players) {
            if (playerId.equals(activePlayerId)) {
                s.append("Active Player! ");
            } else if (!livingPlayers.contains(playerId)) {
                s.append("Eliminated. ");
            } else {
                s.append("Waiting... ");
            }
            s.append("score: ");
            s.append(scores.get(playerId));
            s.append(", lives: ");
            s.append(remainingLives.get(playerId));
            s.append(", hints: ");
            s.append(remainingHints.get(playerId));
            playerStatusMap.put(playerNames.get(playerId), s.toString());
            s.setLength(0);
        }
        return playerStatusMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPlayerCount() {
        return players.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGameId() {
        return game.getID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startMatch() {
        if (getStatus() == MatchStatus.PREPARING) {
            livingPlayers = new ArrayList<>();
            livingPlayers.addAll(players);
            activePlayerId = players.get(0);
            setStatus(MatchStatus.ONGOING);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPlayer(String playerID, String playerName) throws DuplicateUserIDException, MaxPlayerReachedException {
        if (players.contains(playerID)) {
            throw new DuplicateUserIDException();
        } else if (getPlayerCount() >= getPlayerLimit()) {
            throw new MaxPlayerReachedException();
        } else {
            players.add(playerID);
            playerNames.put(playerID, playerName);
            scores.put(playerID, 0);
            remainingLives.put(playerID, game.getNumLives());
            remainingHints.put(playerID, game.getNumHints());
            setChanged();
            notifyObservers();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePlayer(String playerID) throws InvalidIDException {
        if (players.contains(playerID)) {
            players.remove(playerID);
            setChanged();
            notifyObservers();
        } else {
            throw new InvalidIDException(IDType.USER);
        }
    }

    /**
     * Advances the game state according to the input move.
     * <p>
     * The first alphanumeric character of the input is taken as the guessed letter.
     * "hint" is a special phrase that will consume a remaining hint and reveal an alphanumeric
     * character in the Answer.
     *
     * {@inheritDoc}
     */
    @Override
    public void playMove(String PlayerID, String move) {
        if (getStatus() == MatchStatus.PREPARING) {
            setChanged();
            notifyObservers();
            return;
        }

        if (activePlayerId.equals(PlayerID)) {
            char moveChar = Character.toLowerCase(move.charAt(0));
            switch (parseMove(move)) {
                case INVALID:
                    generateOutput(playerNames.get(activePlayerId) + "'s guess '" + moveChar + "' is invalid. Try again.");
                    return;
                case USED:
                    generateOutput(playerNames.get(activePlayerId) + "'s guess '" + moveChar + "' already guessed. Try again.");
                    return;
                case HINT:
                    if (remainingHints.get(PlayerID) > 0) {
                        remainingHints.put(PlayerID, remainingHints.get(PlayerID) - 1);
                        guessChar(getHint(), PlayerID, MoveType.HINT);
                    } else {
                        generateOutput(playerNames.get(activePlayerId) + " has no more hints. Try again.");
                    }
                    return;
                case NORMAL:
                    guessChar(moveChar, PlayerID, MoveType.NORMAL);
                    return;
            }
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

    // searches the current puzzle for the input char. deducts a life if nothing is found
    // also updates the next active player
    private void guessChar(char moveChar, String playerID, MoveType type) {
        guesses.add(moveChar);
        int found = findAndRevealChar(moveChar);

        if (type == MoveType.HINT) {    //if a hint was used
            handleHintCase(found, moveChar);
        } else if (found > 0) {    // if the guessed letter was found
            handleGuessFoundCase(found, moveChar);
        } else {    // if nothing was found
            handleGuessNotFoundCase(moveChar);
        }
    }

    private void handleGuessNotFoundCase(char moveChar) {
        mistakes.add(moveChar);
        remainingLives.put(activePlayerId, remainingLives.get(activePlayerId) - 1);
        if (remainingLives.get(activePlayerId) == 0) {
            livingPlayers.remove(activePlayerId);
            if (livingPlayers.isEmpty()) {
                generateOutput(playerNames.get(activePlayerId) + " guessed '" + moveChar + "' and missed. Everybody's dead!\n" + getEndingMessage());
                setStatus(MatchStatus.FINISHED);
            } else {
                generateOutput(playerNames.get(activePlayerId) + "'s guess '" + moveChar + "' not found. Player eliminated!");
                nextActivePlayer(activePlayerId);
            }
        } else {
            generateOutput(playerNames.get(activePlayerId) + "'s guess '" + moveChar + "' not found. They lose 1 life.");
            nextActivePlayer(activePlayerId);
        }
    }

    private void handleGuessFoundCase(int found, char moveChar) {
        scores.put(activePlayerId, scores.get(activePlayerId) + found);
        if (this.isPuzzleSolved()) {
            if (this.hasNextPuzzle()) {
                currentPuzzleIndex++;
                this.loadPuzzle();
                generateOutput(playerNames.get(activePlayerId) + " guessed '" + moveChar + "' and solved the puzzle!");
                nextActivePlayer(activePlayerId);
            } else {
                generateOutput(playerNames.get(activePlayerId) + " guessed '" + moveChar + "' and solved the last puzzle!\n" + getEndingMessage());
                setStatus(MatchStatus.FINISHED);
            }
        } else {
            generateOutput(playerNames.get(activePlayerId) + " guessed '" + moveChar + "' and earned + " + found + " points!");
            nextActivePlayer(activePlayerId);
        }
    }

    private void handleHintCase(int found, char moveChar) {
        if (this.isPuzzleSolved()) {
            if (this.hasNextPuzzle()) {
                currentPuzzleIndex++;
                this.loadPuzzle();
                generateOutput(playerNames.get(activePlayerId) + " used a hint! Uncovered" + found + " '" + moveChar + "'. Go again.");
            } else {
                generateOutput(playerNames.get(activePlayerId) + " used a hint! Uncovered" + found + " '" + moveChar + "'. Last puzzle solved!\n" + getEndingMessage());
                setStatus(MatchStatus.FINISHED);
            }
        } else {
            generateOutput(playerNames.get(activePlayerId) + " used a hint! Uncovered " + found + " '" + moveChar + "'. Go again.");
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

    private boolean isPuzzleSolved() {
        for (int i = 0; i < currentAnswer.length; i++) {
            if (currentAnswer[i] != gameState[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean hasNextPuzzle() {
        return currentPuzzleIndex + 1 < game.getNumPuzzles();
    }

    private void loadPuzzle() {
        mistakes.clear();
        guesses.clear();
        String answerString = game.getAnswer(currentPuzzleIndex);
        currentAnswer = answerString.toCharArray();
        String gameStateString = answerString.replaceAll("[a-zA-Z0-9]", "_");
        gameState = gameStateString.toCharArray();
    }

    private String simpleOutput() {
        return "Puzzle " + (currentPuzzleIndex + 1) + " of " + game.getNumPuzzles() + ".\n"
                + game.getPrompt(currentPuzzleIndex) + "\n"
                + "\n"
                + String.valueOf(gameState) + "\n"
                + "\n"
                + "misses: " + mistakes.toString() + "\n";
    }

    private void generateOutput(String situation) {
        output = situation + "\n\n" + simpleOutput();
        setChanged();
        notifyObservers();
    }

    private void nextActivePlayer(String playerId) {
        if (livingPlayers.isEmpty()) {
            throw new RuntimeException();
        }

        int currentPlayerIndex = players.indexOf(playerId);
        int nextPlayerIndex;

        if (currentPlayerIndex < players.size() - 1) {
            nextPlayerIndex = currentPlayerIndex + 1;
        } else {
            nextPlayerIndex = 0;
        }

        if (livingPlayers.contains(players.get(nextPlayerIndex))) {
            activePlayerId = players.get(nextPlayerIndex);
            setChanged();
            notifyObservers();
        } else {
            nextActivePlayer(players.get(nextPlayerIndex));
        }
    }

    private String getEndingMessage() {
        int highest = 0;
        StringBuilder winner = new StringBuilder();
        for (String player : players) {
            int score = scores.get(player);
            score += remainingHints.get(player);
            score += remainingLives.get(player);
            if (score > highest) {
                highest = score;
                winner.setLength(0);
                winner.append(playerNames.get(player));
            } else if (score == highest) {
                winner.append(", ");
                winner.append(playerNames.get(player));
            }
        }
        winner.append(" wins with a total score ");
        winner.append(highest);
        winner.append("!");
        return winner.toString();
    }
}
