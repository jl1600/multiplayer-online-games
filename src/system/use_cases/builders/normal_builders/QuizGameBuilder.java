package system.use_cases.builders.normal_builders;

import shared.constants.GameAccessLevel;
import shared.exceptions.use_case_exceptions.InsufficientInputException;
import system.entities.game.quiz.QuizGame;
import system.entities.game.quiz.QuizQuestion;

public class QuizGameBuilder {

    private QuizGame currentGame;
    private boolean hasGameId;
    private boolean hasTemplateId;
    private boolean hasOwnerId;
    private boolean hasTitle;
    private boolean hasGameAccessLevel;
    private boolean hasMaxAttempts;
    private boolean hasScoreCategories;
    private boolean hasQuestions;

    public QuizGameBuilder() {
        currentGame = new QuizGame();
        hasGameId = false;
        hasTemplateId = false;
        hasOwnerId = false;
        hasTitle = false;
        hasGameAccessLevel = false;
        hasMaxAttempts = false;
        hasScoreCategories = false;
        hasQuestions = false;
    }

    /**
     * Adds a new score category to the quiz
     *
     * @param category      The score category label
     * @param weight        Double weight of the score category
     * @param minAcceptable Double minimum acceptable score
     * @param finalMessage  The end message if this is the winning category
     */
    public void addScoreCategory(String category, Double weight, Double minAcceptable, String finalMessage) {
        this.currentGame.addScoreCategory(category, weight, minAcceptable, finalMessage);
        this.hasScoreCategories = true;
    }

    /**
     * Sets the game's GameId to the input Id
     *
     * @param gameId the desired GameId
     */
    public void setId(String gameId) {
        this.currentGame.setID(gameId);
        this.hasGameId = true;
    }

    /**
     * Sets the game's OwnerId to the input Id
     *
     * @param ownerId the desired OwnerId
     */
    public void setOwnerId(String ownerId) {
        this.currentGame.setOwnerId(ownerId);
        this.hasOwnerId = true;
    }

    /**
     * Changes the game's TemplateId to the input Id
     *
     * @param templateId the desired templateId
     */
    public void setTemplateId(String templateId) {
        this.currentGame.setTemplateId(templateId);
        this.hasTemplateId = true;
    }

    /**
     * Changes whether the quiz is public or private
     *
     * @param gameAccessLevel PUBLIC means public, PRIVATE means private
     */
    public void setGameAccessLevel(GameAccessLevel gameAccessLevel) {
        this.currentGame.setGameAccessLevel(gameAccessLevel);
        this.hasGameAccessLevel = true;
    }

    /**
     * Sets the current quiz Title.
     *
     * @param title the new quiz title.
     */
    public void setTitle(String title) {
        this.currentGame.setTitle(title);
        this.hasTitle = true;
    }

    /**
     * Adds the input question next in the quiz
     *
     * @param q the QuizQuestion to add
     */
    public void addQuestion(QuizQuestion q) {
        this.currentGame.addQuestion(q);
        this.hasQuestions = true;
    }

    /**
     * Sets the maximum number of answer attempts.
     *
     * @param maxAttempts the maximum number of attempts allowed.
     */
    public void setMaxAttempts(int maxAttempts) throws InsufficientInputException {
        if (maxAttempts > 0) {
            this.currentGame.setMaxAttempts(maxAttempts);
            this.hasMaxAttempts = true;
        } else {
            throw new InsufficientInputException();
        }
    }

    /**
     * Check if the built QuizGame has all required data to be built.
     * Requires a GameId, TemplateId, OwnerId, Title, isPublic, MaxAttempts,
     * minimum 1 ScoreCategory, and minimum 1 Question.
     *
     * @return true if ready, false if QuizGame is incomplete
     */
    public boolean isReadyToBuild() {
        return hasGameId &
                hasTemplateId &
                hasOwnerId &
                hasTitle &
                hasGameAccessLevel &
                hasMaxAttempts &
                hasScoreCategories &
                hasQuestions;
    }

    /**
     * Outputs the built QuizGame object if it is complete.
     *
     * Outputs the constructed QuizGame. Requires a GameId, TemplateId, OwnerId,
     * Title, isPublic, MaxAttempts, minimum 1 ScoreCategory, and minimum 1 Question.
     *
     * @return The constructed QuizGame.
     */
    public QuizGame toQuizGame() throws InsufficientInputException {
        if (!isReadyToBuild()) {
            throw new InsufficientInputException();
        }
        return this.currentGame;
    }
}
