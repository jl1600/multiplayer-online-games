package system.entities.game.quiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import shared.constants.GameGenre;
import system.entities.game.Game;

/** QuizGame Class
 *
 */
public class QuizGame extends Game {

    private final String creatorName;
    private Integer maxAttempts;
    private final ArrayList<QuizQuestion> questions;

    /**
     * Score Category Class
     */
    public static class ScoreCategory {
        String name;
        Double weight;
        Double minAcceptable;
        String endingMessage;

        /**
         * Score Category Constructor
         * @param name Score Category Name
         */
        public ScoreCategory(String name) {
            this.name = name;
            this.weight = 1.0;
            this.minAcceptable = 0.0;
            this.endingMessage = "Quiz Game Finished.";
        }

        /**
         *
         * @return Score Category ending message
         */
        public String getEndingMessage() {
            return endingMessage;
        }

        /**
         *
         * @return Score Category name
         */
        public String toString() {
            return name;
        }
    }

    private final HashMap<String, ScoreCategory> scoreCategories;

    /**
     * Quiz Game Constructor
     */
    public QuizGame() {
        this(null, null);
    }

    @Override
    public GameGenre getGenre() {
        return GameGenre.QUIZ;
    }

    /**
     * Quiz Game Constructor
     * @param title Title of the game
     * @param creatorName Name of User who created Quiz Game
     */
    public QuizGame(String title, String creatorName) {
        this.maxAttempts = 1;
        this.scoreCategories = new HashMap<>();
        this.questions = new ArrayList<>();
        this.creatorName = creatorName;
        setTitle(title);
    }

    /**
     * Set maximum attempts
     * @param maxAttempts maximum attempt for the Quiz Game
     */
    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    /**
     * Add new score category
     * @param category Category that needs to be added to score category
     */
    public void addScoreCategory(String category) {
        this.scoreCategories.put(category, new ScoreCategory(category));
    }

    /**
     * Sets ending message for the Quiz Game
     * @param scoreCategory Score Category that message needs to be assigned to
     * @param message Message that needs to be assigned
     */
    public void setEndingMessage(String scoreCategory, String message) {
            this.scoreCategories.get(scoreCategory).endingMessage = message;
    }


    /**
     * Add question for the Quiz Game
     * @param question that needs to be added to the Quiz Game
     */
    public void addQuestion(QuizQuestion question) {
        this.questions.add(question);
    }

    /**
     * @param index of question
     * @return return question at index
     */
    public QuizQuestion getQuestion(int index) {
        return questions.get(index);
    }

    /**
     * @return the Scores
     */
    public Set<String> getScoreCategories() {
        return this.scoreCategories.keySet();
    }

    /**
     * @return the name of the Quiz creator
     */
    public String getCreatorName() {
        return creatorName;
    }

    /**
     * @return the count of maximum attempts
     */
    public int getMaxAttempts() {
        return this.maxAttempts;
    }

    /**
     *
     * @return the count of Quiz questions
     */
    public int getNumQuestions() {
        return questions.size();
    }

    /**
     * @return the count of categories
     */
    public int getNumScoreCategories() {
        return scoreCategories.size();
    }

    /**
     * Returns the ending message that corresponds to the given score category name.
     * */
    public String getEndingMessage(String scoreCategory) {
        return scoreCategories.get(scoreCategory).getEndingMessage();
    }



}
