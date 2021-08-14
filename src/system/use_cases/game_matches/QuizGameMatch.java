package system.use_cases.game_matches;

import shared.constants.MatchStatus;
import shared.exceptions.use_case_exceptions.DuplicateUserIDException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;
import shared.exceptions.use_case_exceptions.MaxPlayerReachedException;
import system.entities.game.quiz.QuizGame;
import system.entities.template.QuizTemplate;

import java.util.HashMap;
import java.util.Map;

public class QuizGameMatch extends GameMatch {

    private final Map<String, PlayerStat> playerStats;
    private final QuizGame game;
    private final QuizTemplate template;
    private int numMovedPlayers = 0;
    private int currQuestionIndex;

    private class PlayerStat {

        private final String username;
        private final Map<String, Double> scoresByCategory;
        private String lastInput;
        private int numAttempted = 0;

        public PlayerStat(String username) {
            this.username = username;
            this.scoresByCategory = new HashMap<>();
            for (String cat: game.getScoreCategories()) {
                scoresByCategory.put(cat, 0.0);
            }
            lastInput = null;
        }

        public void clearLastTurn() {
            lastInput = null;
            numAttempted = 0;
        }


        public void increaseScores(Map<String, Double> valuesToAdd) {
            for (String category: valuesToAdd.keySet()) {
                increaseScore(category, valuesToAdd.get(category));
            }
        }

        public void increaseScore(String category, Double valueToAdd) {
            scoresByCategory.put(category, scoresByCategory.get(category) + valueToAdd);
        }

        public void increaseScoreByAnswer(int answerIndex) {
            this.increaseScores(game.getQuestion(currQuestionIndex).getAnswerScoreRewards(answerIndex));
        }


        public String getCategoryWithHighestScore() {
            Double max = -1.0;
            String highestCat = null;
            for (String category: scoresByCategory.keySet()) {
                if (scoresByCategory.get(category) > max) {
                    max = scoresByCategory.get(category);
                    highestCat = category;
                }
            }
            return highestCat;
        }

        public Double getHighestScore() {
            Double max = -1.0;
            for (String category: scoresByCategory.keySet()) {
                if (scoresByCategory.get(category) > max) {
                    max = scoresByCategory.get(category);
                }
            }
            return max;
        }
    }


    public QuizGameMatch(String matchID, String userID, String username, QuizGame game, QuizTemplate template){
        super(matchID, userID, username, 10); // temporary player limit
        this.game = game;
        this.template = template;
        this.playerStats = new HashMap<>();
        try {
            addPlayer(userID, username);
        } catch (DuplicateUserIDException | MaxPlayerReachedException e) {
            throw new RuntimeException("Failed to add host as a new player. This should never happen");
        }
    }

    @Override
    public void addPlayer(String userID, String username) throws DuplicateUserIDException, MaxPlayerReachedException {
        if (playerStats.containsKey(userID)) {
            throw new DuplicateUserIDException();
        } else if (getPlayerCount() >= getPlayerLimit()) {
            throw new MaxPlayerReachedException();
        }
        System.out.println("Trying to add player");
        playerStats.put(userID, new PlayerStat(username));
        setChanged();
        notifyObservers();
    }

    @Override
    public void removePlayer(String playerID) throws InvalidUserIDException {
        if (playerStats.containsKey(playerID)) {
            this.playerStats.remove(playerID);
            setChanged();
            notifyObservers();
        }
        else throw  new InvalidUserIDException();
    }

    private boolean containPlayer(String playerID) {
        return playerStats.containsKey(playerID);
    }

    @Override
    public String getTextContent()  {

        if (getStatus() == MatchStatus.PREPARING) {
            return "";
        }

        if (getStatus() == MatchStatus.FINISHED) {
            return getEndingContent();
        }
        String currQuestion = game.getQuestion(currQuestionIndex).toString();

        if (template.isMultipleChoice() && template.isChooseAllThatApply()) {
                return currQuestion + "\nEnter all answers that apply, " +
                        "separated by space: ";
        } else if (template.isMultipleChoice() && template.hasMultipleScoreCategories()) {
            return currQuestion + "\nEnter the number corresponding to your choice:";
        } else { // One correct answer multiple choice or Exact answer
            String lastRes = "";
            if (currQuestionIndex > 0) {
                int correctAnswerIndex = game.getQuestion(currQuestionIndex - 1).getCorrectAnswerIndex();
                if (correctAnswerIndex != -1)
                    lastRes = "Correct answer: " +
                            game.getQuestion(currQuestionIndex - 1).getAnswer(correctAnswerIndex);
            }
            if (!template.isMultipleChoice())
                return lastRes + "\n" + game.getQuestion(currQuestionIndex).getQuestionData() + "\nEnter exact answer";
            return lastRes + "\n" + currQuestion;
        }
    }

    private String getEndingContent() {
        StringBuilder result = new StringBuilder();
        if (template.isMultipleChoice() && template.hasMultipleScoreCategories() && !template.isChooseAllThatApply()) {
            for (PlayerStat player: playerStats.values()) {
                result.append("\n").append(player.username).
                        append(": ").append(game.getEndingMessage(player.getCategoryWithHighestScore()));
            }
            return result.toString();
        } else if (template.isChooseAllThatApply()) {
            result.append("Quiz ended. Player scores are:");
            for (PlayerStat player: playerStats.values()) {
                result.append("\n").append(player.username).
                        append(": ").
                        append(player.getHighestScore());
            }
            return  result.toString();
        } else { // Simple Multiple choice or simple exact answer.
            int correctAnswerIndex = game.getQuestion(currQuestionIndex).getCorrectAnswerIndex();
            if (correctAnswerIndex != -1) {
                result.append("Correct answer: ").
                        append(game.getQuestion(currQuestionIndex).getAnswer(correctAnswerIndex)).
                        append("\n");
            }
            result.append("Quiz ended. Player scores are:");
            for (PlayerStat player : playerStats.values()) {
                result.append("\n").append(player.username).
                        append(": ").
                        append(player.getHighestScore()).
                        append("/").append(game.getNumQuestions());
            }
            return result.toString();
        }
    }

    @Override
    public Map<String, String> getPlayersLastMove(){
        Map<String, String> playersMove = new HashMap<>();
        for (PlayerStat player: playerStats.values()) {
            if (numMovedPlayers == getPlayerCount())
                playersMove.put(player.username, player.lastInput);
            else {
                playersMove.put(player.username, player.lastInput != null?"Answer hidden":"Waiting for player input...");
            }
        }
        return playersMove;
    }

    @Override
    public int getPlayerCount() {
        return playerStats.size();
    }

    @Override
    public String getGameId() {
        return game.getID();
    }

    @Override
    public void startMatch() {
        if (getStatus() == MatchStatus.PREPARING) {
            setStatus(MatchStatus.ONGOING);
            this.currQuestionIndex = 0;
            setChanged();
            notifyObservers();
        }
    }


    private void nextTurn() {
        if (currQuestionIndex < game.getNumQuestions() - 1) {
            currQuestionIndex += 1;
        } else {
            setStatus(MatchStatus.FINISHED);
        }
        setChanged();
        notifyObservers();
        numMovedPlayers = 0;
        for(PlayerStat player: playerStats.values()) {
            player.clearLastTurn();
        }
    }


    @Override
    public void playMove(String playerID, String move) throws InvalidUserIDException, InvalidInputException {

        if (!containPlayer(playerID)) {
            throw new InvalidUserIDException();
        }
        PlayerStat player = playerStats.get(playerID);
        if (player.numAttempted >= game.getMaxAttempts())
            return;

        if (template.isMultipleChoice()) {
            handleMultipleChoiceMove(playerID, move);
        }
        else {
            handleExactAnswerMove(playerID, move);
        }
        player.numAttempted++;
        player.lastInput = move;
        if (player.numAttempted == 1){
            numMovedPlayers ++;
        }
        setChanged();
        notifyObservers();
        if (numMovedPlayers == getPlayerCount())
            nextTurn();
    }


    private void handleExactAnswerMove(String playerID, String move) {
        if (move.equals(game.getQuestion(currQuestionIndex).getAnswer(0).toString())) {
            playerStats.get(playerID).increaseScoreByAnswer(0);
        }
    }

    private void handleMultipleChoiceMove(String playerID, String move) throws InvalidInputException {
        if (template.isChooseAllThatApply()) {
            String[] choices = move.split(" ");
            for (String choice: choices) {
                try {
                    int answerIndex = Integer.parseInt(choice) - 1;
                    if (answerIndex >= game.getQuestion(currQuestionIndex).getAnswerNum() || answerIndex < 0)
                        throw new InvalidInputException();
                    playerStats.get(playerID).increaseScoreByAnswer(answerIndex);
                }
                catch (NumberFormatException e) {
                    throw new InvalidInputException();
                }
            }
        }
        else {
            try {
                int answerIndex = Integer.parseInt(move) - 1;
                if (answerIndex >= game.getQuestion(currQuestionIndex).getAnswerNum() || answerIndex < 0)
                    throw new InvalidInputException();
                playerStats.get(playerID).increaseScoreByAnswer(answerIndex);
            }
            catch (NumberFormatException e) {
                throw new InvalidInputException();
            }
        }
    }
}
