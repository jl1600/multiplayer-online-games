package system.use_cases.game_matches;

import shared.constants.MatchStatus;
import shared.exceptions.use_case_exceptions.DuplicateUserIDException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;
import system.entities.game.Game;
import system.entities.game.quiz.QuizGame;
import system.entities.template.QuizTemplate;

import java.util.HashMap;
import java.util.Set;

public class QuizGameMatch extends GameMatch {

    private class PlayerStat {

        private final HashMap<String, Double> scoresByCategory;

        public PlayerStat() {
            this.scoresByCategory = new HashMap<>();
            for (String cat: game.getScoreCategories()) {
                scoresByCategory.put(cat, 0.0);
            }
        }

        public HashMap<String, Double> getScores() {
            return scoresByCategory;
        }

        public void increaseScores(HashMap<String, Double> valuesToAdd) {
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

        public Double getScoreByCategory(String category) {
            return scoresByCategory.get(category);
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

    private final HashMap<String, PlayerStat> playerStats;
    private final QuizGame game;
    private final QuizTemplate template;
    private int currQuestionIndex;

    public QuizGameMatch(String matchID, String userID, QuizGame game, QuizTemplate template){
        super(matchID, userID, 10); // temporary player limit
        this.game = game;
        this.template = template;
        this.playerStats = new HashMap<>();
        initialize();
    }

    private void initialize() {
        this.currQuestionIndex = 0;
        try {
            this.addPlayer(getHostID());
        }
        catch (DuplicateUserIDException e) {
            e.printStackTrace();
        }
    }

    public void addPlayer(String userID) throws DuplicateUserIDException {
        if (playerStats.containsKey(userID)) {
            throw new DuplicateUserIDException();
        }
        playerStats.put(userID, new PlayerStat());
    }

    @Override
    public void removePlayer(String playerID) throws InvalidUserIDException {

    }

    private boolean containPlayer(String playerID) {
        return playerStats.containsKey(playerID);
    }

    @Override
    public String getTextContent(String playerID) throws InvalidUserIDException {

        if (!containPlayer(playerID)) {
            throw new InvalidUserIDException();
        }

        if (!(getStatus() == MatchStatus.FINISHED) && template.isMultipleChoice()) {
            if (template.isChooseAllThatApply())
                return game.getQuestion(currQuestionIndex).toString() + "\n Enter all answers that apply, " +
                        "separated by space: ";
            return game.getQuestion(currQuestionIndex).toString();
        }
        else if (!(getStatus() == MatchStatus.FINISHED)) {
            return "\nNumber of correct answers so far: " +
                    playerStats.get(playerID).getHighestScore().toString() + "\n\nQuestion " +
                    (currQuestionIndex + 1) + ": " +
                    game.getQuestion(currQuestionIndex).getQuestionData() + "\n Enter exact answer: ";
        }
        else if ((getStatus() == MatchStatus.FINISHED) && template.isMultipleChoice() && template.hasMultipleScoreCategories()){
            return "\n" + game.getEndingMessage(playerStats.get(playerID).getCategoryWithHighestScore());
        }
        else {
            return "\n Total Score: " + playerStats.get(playerID).getHighestScore();
        }
    }

    @Override
    public String getPlayerStats(String playerID) throws InvalidUserIDException {
        return null;
    }

    @Override
    public Set<String> getAllPlayerIds() {
        return null;
    }

    @Override
    public int getPlayerCount() {
        return 0;
    }

    @Override
    public String getGameId() {
        return null;
    }


    @Override
    public void playMove(String playerID, String move) throws InvalidUserIDException, InvalidInputException {

        if (!containPlayer(playerID)) {
            throw new InvalidUserIDException();
        }

        if (template.isMultipleChoice()) {
            handleMultipleChoiceMove(playerID, move);
        }
        else {
            handleExactAnswerMove(playerID, move);
        }
        goNextQuestion();
    }

    private void goNextQuestion() {
        if (currQuestionIndex < game.getNumQuestions() - 1) {
            currQuestionIndex += 1;
        }
        else {
            setFinishedStatus();
        }
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
