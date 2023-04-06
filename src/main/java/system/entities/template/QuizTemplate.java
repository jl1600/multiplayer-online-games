package system.entities.template;


import shared.constants.GameGenre;

/**
 * Quiz Template Class
 */
public class QuizTemplate extends Template {
    private boolean multipleChoice;
    private boolean chooseAllThatApply;
    private boolean hasMultipleScoreCategories;
    private boolean hasScoreWeight;
    private boolean hasCustomEndingMessage;

    /**
     * Quiz Template Constructor
     */
    public QuizTemplate(){
        this.hasCustomEndingMessage = false;
        this.multipleChoice = true;
        this.chooseAllThatApply = false;
        this.hasMultipleScoreCategories = false;
        this.hasScoreWeight = false;
        setTitle("Unnamed Quiz Template");
    }

    @Override
    public GameGenre getGenre() {
        return GameGenre.QUIZ;
    }

    /**
     * Quiz Template Constructor
     * @param template quiz template to modify
     */
    public QuizTemplate(QuizTemplate template){
        super(template);
        this.hasCustomEndingMessage = template.hasCustomEndingMessage;
        this.multipleChoice = template.multipleChoice;
        this.chooseAllThatApply = template.chooseAllThatApply;
        this.hasMultipleScoreCategories = template.hasMultipleScoreCategories;
        this.hasScoreWeight = template.hasScoreWeight;
    }

    /**
     *
     * @return if quiz is multiple choice
     */
    public boolean isMultipleChoice() {
        return multipleChoice;
    }

    /**
     *
     * @return if quiz has choose all that apply
     */
    public boolean isChooseAllThatApply() {
        return chooseAllThatApply;
    }


    /**
     *
     * @return if quiz has multiple score categories
     */
    public boolean hasMultipleScoreCategories() {
        return hasMultipleScoreCategories;
    }

    /**
     *
     * @return if quiz has custom ending message
     */
    public boolean hasCustomEndingMessage() {
        return hasCustomEndingMessage;
    }

    /**
     * Set quiz to have the option of multiple choice
     * @param option indicating if quiz has multiple choice
     */
    public void setMultipleChoice(boolean option) {
        multipleChoice = option;
    }

    /**
     * Set quiz to have the option of choose all that apply
     * @param option indicating if quiz has choose all that apply
     */
    public void setChooseAllThatApply(boolean option) {
        chooseAllThatApply = option;
    }

    /**
     * Set quiz to have the option of multiple score categories
     * @param option indicating if quiz has multiple score categories
     */
    public void setHasMultipleScoreCategories(boolean option) {
        hasMultipleScoreCategories = option;
    }

    /**
     * Set quiz to have the option of score weight
     * @param option indicating if quiz has score weight
     */
    public void setHasScoreWeight(boolean option) {
        hasScoreWeight = option;
    }

    /**
     * Set quiz to have the option of custom ending message
     * @param option indicating if quiz has multiple choice
     */
    public void setHasCustomEndingMessage(boolean option) {
        hasCustomEndingMessage = option;
    }

}
