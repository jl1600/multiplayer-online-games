package system.use_cases.builders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import shared.constants.GameAccessLevel;
import shared.exceptions.use_case_exceptions.InvalidIDException;
import shared.exceptions.use_case_exceptions.NotReadyException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.entities.game.quiz.QuizAnswer;
import system.entities.game.quiz.QuizGame;
import system.entities.game.quiz.QuizQuestion;
import system.entities.template.QuizTemplate;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * QuizGameInteractiveBuilder Class
 */
public class QuizGameInteractiveBuilder extends GameInteractiveBuilder {
    int numQuestions;
    int numAnswerEachQ;
    int numScoreCategories;

    DesignSubject currentDesignSubject;

    QuizGame currentGame;
    QuizTemplate chosenTemplate;
    QuizQuestion currentQuizQuestion;
    QuizAnswer currentQuizAnswer;
    String currentCategory;
    String currentDesignQuestion;
    Map<DesignSubject, String> designQuestions;

    /**
     * Design subjects that the user can choose value of
     */
    public enum DesignSubject {
        TITLE, QUESTION_NUM, ANSWER_NUM, CATEGORIES_NUM, CATEGORY_NAME, CATEGORY_END_MESSAGE,
        MAX_ATTEMPTS, QUIZ_QUESTION, QUIZ_ANSWER, CORRECT_ANSWER_INDEX, CORRECT_ANSWERS, CATEGORY_SCORE, IS_PUBLIC,
        CONFIRMATION, NULL
    }

    /**
     * Constructor of QuizGameInteractiveBuilder
     * @param creatorID the creator of this game
     * @param template the template this game is using
     */
    public QuizGameInteractiveBuilder(String creatorID, QuizTemplate template) {
        super(creatorID);
        currentGame = new QuizGame("", creatorID);
        currentGame.setTemplateID(template.getID());
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(
                    "src/system/configuration_files/quiz_design_questions.json"));
            Type type = new TypeToken<Map<DesignSubject, String>>(){}.getType();
            designQuestions = gson.fromJson(reader, type);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Fatal: Can't find the configuration file for hangman design questions.");
        }
        currentDesignSubject = DesignSubject.TITLE;
        currentDesignQuestion = designQuestions.get(DesignSubject.TITLE);
        chosenTemplate = template;
        numQuestions = 0;
        numScoreCategories = 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeDesignChoice(String designChoice) throws InvalidInputException {
        currentDesignQuestion = update(designChoice);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDesignQuestion() {
        return currentDesignQuestion;
    }

    private String update(String designChoice) throws InvalidInputException {

        switch (currentDesignSubject) {

            case TITLE:
                return handleTitleDesignChoice(designChoice);
            case QUESTION_NUM:
                return handleQuestionNumDesignChoice(designChoice);
            case ANSWER_NUM:
                return handleAnswerNumDesignChoice(designChoice);
            case CATEGORIES_NUM:
                return handleCategoryNumDesignChoice(designChoice);
            case CATEGORY_NAME:
                return handleCategoryNameDesignChoice(designChoice);
            case MAX_ATTEMPTS:
                return handleMaxAttemptDesignChoice(designChoice);
            case QUIZ_QUESTION:
                return handleQuizQuestionDesignChoice(designChoice);
            case QUIZ_ANSWER:
                return handleQuizAnswerDesignChoice(designChoice);
            case CATEGORY_SCORE:
                return handleCategoryScoreDesignChoice(designChoice);
            case CORRECT_ANSWER_INDEX:
                return handleCorrectAnswerDesignChoice(designChoice);
            case CORRECT_ANSWERS:
                return handleCorrectAnswersDesignChoice(designChoice);
            case CATEGORY_END_MESSAGE:
                return handleCategoryEndMessageDesignChoice(designChoice);
            case IS_PUBLIC:
                return handleIsPublicDesignChoice(designChoice);
            case CONFIRMATION:
                return handleConfirmationDesignChoice(designChoice);
            default:
                return "Unaccounted design subject. Some Error occurred.";
        }

    }

    private String handleCorrectAnswersDesignChoice(String designChoice) throws InvalidInputException {
        String[] choices = designChoice.split(" ");
        for (String choice: choices) {
            int answerIndex = getQuantityFromStr(designChoice);
            currentQuizQuestion.getAnswer(answerIndex).setCategoryScore("General", 1.0);
        }
        return handleIsEnoughQuestions();
    }

    private String handleIsPublicDesignChoice(String designChoice) throws InvalidInputException{
        if (designChoice.equals("yes") || designChoice.equals("no")) {
            if (designChoice.equals("yes")){
                currentGame.setGameAccessLevel(GameAccessLevel.PUBLIC);
            }

            currentDesignSubject = DesignSubject.CONFIRMATION;
            return designQuestions.get(currentDesignSubject);
        } else {
            throw new InvalidInputException();
        }
    }


    private String handleCorrectAnswerDesignChoice(String designChoice) throws InvalidInputException {
        int correctAnsIndex = getQuantityFromStr(designChoice);
        if (correctAnsIndex > numAnswerEachQ || correctAnsIndex < 1){
            throw new InvalidInputException();
        } else {
            currentQuizQuestion.getAnswer(correctAnsIndex - 1).setCategoryScore("General", 1.0);
            return handleIsEnoughQuestions();
        }

    }


    private String handleConfirmationDesignChoice(String designChoice) {
        if (designChoice.equals("yes")) {
            readyToBuild = true;
            currentDesignSubject = DesignSubject.NULL;
        }
        else {
            // abort the current game, make a new one
            currentGame = new QuizGame("", currentGame.getCreatorName());
            currentDesignSubject =  DesignSubject.TITLE;
        }
        return designQuestions.get(currentDesignSubject);
    }

    private String handleQuizAnswerDesignChoice(String designChoice) {

        currentQuizAnswer = new QuizAnswer(designChoice);
        currentQuizQuestion.addAnswer(currentQuizAnswer);

        if (chosenTemplate.hasMultipleScoreCategories()) {
            updateCurrentScoreCategory();
            currentDesignSubject = DesignSubject.CATEGORY_SCORE;
            return designQuestions.get(currentDesignSubject) + "\"" + currentCategory + "\"? ";
        } else {
            currentQuizAnswer.setCategoryScore("General", 1.0);

            if (currentQuizQuestion.getAnswerNum() < numAnswerEachQ) {
                currentDesignSubject = DesignSubject.QUIZ_ANSWER;
                return designQuestions.get(currentDesignSubject) + (currentQuizQuestion.getAnswerNum() + 1) + ": ";
            } else if (chosenTemplate.isMultipleChoice() && chosenTemplate.isChooseAllThatApply()) {
                currentDesignSubject = DesignSubject.CORRECT_ANSWERS;
                return designQuestions.get(currentDesignSubject);
            } else if (chosenTemplate.isMultipleChoice()) {
                currentDesignSubject = DesignSubject.CORRECT_ANSWER_INDEX;
                return designQuestions.get(currentDesignSubject);
            }
            else {
                return handleIsEnoughQuestions();
            }
        }
    }
    // Helper
    private String handleIsEnoughQuestions() {
        if (currentGame.getNumQuestions() < numQuestions) {
            currentDesignSubject = DesignSubject.QUIZ_QUESTION;
            return designQuestions.get(currentDesignSubject) + (currentGame.getNumQuestions() + 1) + ": ";
        }
        else {
            currentDesignSubject = DesignSubject.IS_PUBLIC;
            return designQuestions.get(currentDesignSubject);
        }
    }

    private String handleCategoryScoreDesignChoice(String designChoice) throws InvalidInputException {
        try {
            currentQuizAnswer.setCategoryScore(currentCategory, Double.parseDouble(designChoice));
            updateCurrentScoreCategory();
            if (currentCategory == null) {
                if (currentQuizQuestion.getAnswerNum() < numAnswerEachQ) {
                    currentDesignSubject = DesignSubject.QUIZ_ANSWER;
                    return designQuestions.get(currentDesignSubject) + (currentQuizQuestion.getAnswerNum() + 1) + ": ";
                }
                else {
                    return handleIsEnoughQuestions();
                }
            }
            else {
                currentDesignSubject = DesignSubject.CATEGORY_SCORE;
                return designQuestions.get(currentDesignSubject) + "\"" + currentCategory + "\"? ";
            }
        }
        catch (NumberFormatException e) {
            throw new InvalidInputException();
        }
    }

    // Helper
    private void updateCurrentScoreCategory() {
        Set<String> categories = currentQuizAnswer.getScoresRewardsByCategory().keySet();
        for (String category: currentGame.getScoreCategories()) {
            if (!categories.contains(category)) {
                currentCategory = category;
                return;
            }
        }
        currentCategory = null;
    }

    private String handleQuizQuestionDesignChoice(String designChoice) {
        currentQuizQuestion = new QuizQuestion(designChoice);
        currentGame.addQuestion(currentQuizQuestion);
        currentDesignSubject = DesignSubject.QUIZ_ANSWER;
        return designQuestions.get(currentDesignSubject) + "1:";
    }

    private String handleMaxAttemptDesignChoice(String designChoice) throws InvalidInputException {
        int numAtt = getQuantityFromStr(designChoice);
        currentGame.setMaxAttempts(numAtt);
        currentDesignSubject = DesignSubject.QUIZ_QUESTION;
        return designQuestions.get(currentDesignSubject) + "1:";
    }

    private String handleCategoryEndMessageDesignChoice(String designChoice) {

        currentGame.setEndingMessage(currentCategory, designChoice);
        if (numScoreCategories > currentGame.getNumScoreCategories()) {
            currentDesignSubject = DesignSubject.CATEGORY_NAME;
            return designQuestions.get(currentDesignSubject) + (currentGame.getNumScoreCategories() + 1);
        }
        else {
            currentDesignSubject = DesignSubject.MAX_ATTEMPTS;
            return designQuestions.get(currentDesignSubject);
        }
    }

    private String handleCategoryNameDesignChoice(String designChoice) {
        currentGame.addScoreCategory(designChoice);
        if (chosenTemplate.hasCustomEndingMessage()) {
            currentCategory = designChoice;
            currentDesignSubject = DesignSubject.CATEGORY_END_MESSAGE;
            return designQuestions.get(currentDesignSubject);
        }
        currentGame.setEndingMessage(designChoice, "Category with highest score is: " + designChoice + ".");
        if (numScoreCategories > currentGame.getNumScoreCategories()) {
            currentDesignSubject = DesignSubject.CATEGORY_NAME;
            return designQuestions.get(currentDesignSubject) + (currentGame.getNumScoreCategories() + 1);
        }
        else {
            currentDesignSubject = DesignSubject.MAX_ATTEMPTS;
            return designQuestions.get(currentDesignSubject);
        }
    }

    private String handleCategoryNumDesignChoice(String designChoice) throws InvalidInputException {
        numScoreCategories = getQuantityFromStr(designChoice);
        currentDesignSubject = DesignSubject.CATEGORY_NAME;
        return designQuestions.get(currentDesignSubject) + "1:";
    }

    private String handleAnswerNumDesignChoice(String designChoice) throws InvalidInputException {
        numAnswerEachQ = getQuantityFromStr(designChoice);
        if (numAnswerEachQ < 1)
            throw new InvalidInputException();
        if (chosenTemplate.hasMultipleScoreCategories()) {
            currentDesignSubject = DesignSubject.CATEGORIES_NUM;
        } else {
            currentGame.addScoreCategory("General");
            currentDesignSubject = DesignSubject.MAX_ATTEMPTS;
        }
        return designQuestions.get(currentDesignSubject);
    }

    private String handleQuestionNumDesignChoice(String designChoice) throws InvalidInputException {
        numQuestions = getQuantityFromStr(designChoice);
        if (numQuestions < 1)
            throw new InvalidInputException();
        if (chosenTemplate.isMultipleChoice()) {
            currentDesignSubject = DesignSubject.ANSWER_NUM;
            return designQuestions.get(currentDesignSubject);
        }

        else { // The case of Exact answer quiz
            numAnswerEachQ = 1;
            currentGame.addScoreCategory("General");
            currentDesignSubject = DesignSubject.MAX_ATTEMPTS;
            return designQuestions.get(currentDesignSubject);
        }

    }


    private String handleTitleDesignChoice(String designChoice) {
        if (designChoice.equals("no")) {
            currentGame.setTitle("Untitled quiz");
        }
        else {
            currentGame.setTitle(designChoice);
        }
        currentDesignSubject = DesignSubject.QUESTION_NUM;
        return designQuestions.get(currentDesignSubject);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public QuizGame build(String id) throws NotReadyException {
        if (!isReadyToBuild()) {
            throw new NotReadyException();
        }
        currentGame.setID(id);
        currentGame.setOwnerId(this.getCreatorID());
        currentGame.setTemplateID(chosenTemplate.getID());
        return currentGame;
    }

    private int getQuantityFromStr(String input) throws InvalidInputException {
        try {
            int res = Integer.parseInt(input);
            if (res > 0)
                return res;
            else throw new InvalidInputException();
        } catch (NumberFormatException e) {
            throw new InvalidInputException();
        }
    }
}
