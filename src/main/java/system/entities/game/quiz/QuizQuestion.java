package system.entities.game.quiz;

import java.util.ArrayList;
import java.util.Map;

/**
 * Quiz Question Class
 */
public class QuizQuestion {

    private String question;
    private final ArrayList<QuizAnswer> answerChoices;

    /**
     * Quiz Question Constructor
     * @param question Quiz Question
     * @param answerChoices Quiz Question Answers
     */
    public QuizQuestion(String question, ArrayList<QuizAnswer> answerChoices) {
        this.question = question;
        this.answerChoices = answerChoices;
    }

    /**
     * Quiz Question Constructor
     * @param question Quiz Question Answer
     */
    public QuizQuestion(String question) {
        this.question = question;
        this.answerChoices = new ArrayList<>();
    }

    /**
     * Add Answer to Quiz Question
     * @param answer that must be added to the answer choices
     */
    public void addAnswer(QuizAnswer answer) {
        answerChoices.add(answer);
    }

    /**
     * @return total amount of answer choices
     */
    public int getAnswerNum() {
        return answerChoices.size();
    }
    /**
     * Returns the index of the correct answer if answers in this question only have one score category: general.
     * Otherwise returns -1;
     * */
    public int getCorrectAnswerIndex() {
        if(answerChoices.get(0).getScoresRewardsByCategory().containsKey("General")) {
            for (int i = 0; i < answerChoices.size(); i++) {
                if (answerChoices.get(i).getScoresRewardsByCategory().get("General") == 1.0)
                    return i;
            }
        }
        return -1;
    }
    /**
     *
     * @param index of answer
     * @return answer at index
     */
    public QuizAnswer getAnswer(int index) {
        return answerChoices.get(index);
    }

    /**
     *
     * @return Question to Quiz Question
     */
    public String getQuestionData() {
        return this.question;
    }

    /**
     *
     * @return Copy of Answers to Quiz Question
     */
    public ArrayList<QuizAnswer> getAnswerData() {
        ArrayList<QuizAnswer> deepCopy = new ArrayList<>();
        for (QuizAnswer answerChoice : this.answerChoices) {
            deepCopy.add(answerChoice.copy());
        }
        return deepCopy;
    }

    /**
     * @return Copy of Quiz Question Object
     */
    public QuizQuestion copy() {
        ArrayList<QuizAnswer> deepCopy = new ArrayList<>();
        for (QuizAnswer answerChoice : this.answerChoices) {
            deepCopy.add(answerChoice.copy());
        }
        return new QuizQuestion(this.question, deepCopy) ;
    }

    /**
     *
     * @param answerIndex index of answer
     * @return the scores associated with each category for answer at index
     * @throws IndexOutOfBoundsException when answer index is more than the total answers
     */
    public Map<String, Double> getAnswerScoreRewards (int answerIndex) throws IndexOutOfBoundsException {
        return answerChoices.get(answerIndex).getScoresRewardsByCategory();
    }

    /**
     * @return String representation of Quiz Question and Answers
     */
    public String toString() {
        StringBuilder res = new StringBuilder(question);

        for (int i = 0; i < answerChoices.size(); i++) {
            res.append("\n");
            res.append(i + 1);
            res.append(". ");
            res.append(answerChoices.get(i));
        }
        return res.toString();
    }
}
