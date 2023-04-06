package system.entities.game.quiz;

import java.util.HashMap;
import java.util.Map;

/** QuizAnswer Class
 *
 */
public class QuizAnswer {
    private final String text;
    private final HashMap<String, Double> scoresRewardsByCategory;
    // Rep invariant:
    //  each key in scoresByCategory is in scoreCategories of the QuizGame class.

    /**
     * QuizAnswer Constructor
     * @param text the QuizAnswer text
     * @param scoresRewardsByCategory the scores associated with each category for QuizAnswer
     */
    public QuizAnswer(String text, HashMap<String, Double> scoresRewardsByCategory) {
        this.text = text;
        this.scoresRewardsByCategory = scoresRewardsByCategory;
    }

    /**
     * QuizAnswer Constructor
     * @param text the QuizAnswer text
     */
    public QuizAnswer(String text) {
        this.text = text;
        this.scoresRewardsByCategory = new HashMap<>();
    }

    /**
     *
     * @return copy of scores associated to each category
     */
    public Map<String, Double> getScoresRewardsByCategory() {
        Map<String, Double> copy = new HashMap<>();
        copy.putAll(this.scoresRewardsByCategory);
        return copy;
    }

    /**
     * Set a category and score associated with it
     * @param category the category of QuizAnswer
     * @param score the score associated with category of QuizAnswer
     */
    public void setCategoryScore(String category, Double score) {
        scoresRewardsByCategory.put(category, score);
    }

    /**
     *
     * @return the amount of score categories
     */
    public int getNumScoreCategories() {
        return scoresRewardsByCategory.size();
    }

    /**
     *
     * @return Copy of QuizAnswer Object
     */
    public QuizAnswer copy() {
        HashMap<String, Double> scoresCopy = new HashMap<>();
        scoresCopy.putAll(this.scoresRewardsByCategory);
        return new QuizAnswer(this.text, scoresCopy);
    }

    /**
     *
     * @return the QuizAnswer text
     */
    public String toString() {
        return text;
    }
}
