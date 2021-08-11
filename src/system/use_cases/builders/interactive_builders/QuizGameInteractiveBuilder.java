package system.use_cases.builders.interactive_builders;

import shared.constants.GameAccessLevel;
import shared.exceptions.use_case_exceptions.InsufficientInputException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.entities.game.quiz.QuizAnswer;
import system.entities.game.quiz.QuizGame;
import system.entities.game.quiz.QuizQuestion;
import system.entities.template.QuizTemplate;

import java.util.Set;

public class QuizGameInteractiveBuilder extends GameInteractiveBuilder {
    int numQuestions;
    int numAnswerEachQ;
    int numScoreCategories;

    DesignSubject currentDesignSubject;
    String currentDesignQuestion;

    QuizGame currentGame;
    QuizTemplate chosenTemplate;
    QuizQuestion currentQuizQuestion;
    QuizAnswer currentQuizAnswer;
    String currentCategory;

    public enum DesignSubject {
        TITLE, QUESTION_NUM, ANSWER_NUM, CATEGORIES_NUM, CATEGORY_NAME, CATEGORY_END_MESSAGE,
        MAX_ATTEMPTS, QUIZ_QUESTION, QUIZ_ANSWER, CORRECT_ANSWER, CORRECT_ANSWERS, CATEGORY_SCORE, IS_PUBLIC,
        CONFIRMATION, NULL
    }

    public QuizGameInteractiveBuilder(String creatorID, QuizTemplate template) {
        super(creatorID);
        currentGame = new QuizGame("", creatorID);
        currentGame.setTemplateID(template.getID());
        currentDesignSubject = DesignSubject.TITLE;
        currentDesignQuestion = "Does your quiz have a title? If yes, enter the title. If no, answer 'no'.";
        chosenTemplate = template;
        numQuestions = 0;
        numScoreCategories = 1;
    }

    @Override
    public void makeDesignChoice(String designChoice) throws InvalidInputException {
        currentDesignQuestion = update(designChoice);
    }

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
            case CORRECT_ANSWER:
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
        try {
            String[] choices = designChoice.split(" ");
            for (String choice: choices) {
                    int answerIndex = Integer.parseInt(choice) - 1;
                    if (answerIndex >= numAnswerEachQ || answerIndex < 0)
                        throw new InvalidInputException();
                currentQuizQuestion.getAnswer(answerIndex).setCategoryScore("General", 1.0);
                }
            return handleIsEnoughQuestions();
        } catch (NumberFormatException e) {
            throw new InvalidInputException();
        }
    }

    private String handleIsPublicDesignChoice(String designChoice) throws InvalidInputException{
        if (designChoice.equals("yes") || designChoice.equals("no")) {
            if (designChoice.equals("yes")){
                currentGame.setGameAccessLevel(GameAccessLevel.PUBLIC);
            }

            currentDesignSubject = DesignSubject.CONFIRMATION;
            return "Quiz is ready to build. Do you want to proceed? (yes/no)";
        } else {
            throw new InvalidInputException();
        }
    }


    private String handleCorrectAnswerDesignChoice(String designChoice) throws InvalidInputException {
        try {
            int correctAnsIndex = Integer.parseInt(designChoice);
            if (correctAnsIndex > numAnswerEachQ || correctAnsIndex < 1){
                throw new InvalidInputException();
            } else {
                currentQuizQuestion.getAnswer(correctAnsIndex - 1).setCategoryScore("General", 1.0);
                return handleIsEnoughQuestions();
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException();
        }
    }


    private String handleConfirmationDesignChoice(String designChoice) {
        if (designChoice.equals("yes")) {
            readyToBuild = true;
            currentDesignSubject = DesignSubject.NULL;
            return "Game successfully built.";
        }
        else {
            // abort the current game, make a new one
            currentGame = new QuizGame("", currentGame.getCreatorName());
            currentDesignSubject =  DesignSubject.TITLE;
            return "Please enter the title: ";
        }
    }

    private String handleQuizAnswerDesignChoice(String designChoice) {

        currentQuizAnswer = new QuizAnswer(designChoice);
        currentQuizQuestion.addAnswer(currentQuizAnswer);

        if (chosenTemplate.hasMultipleScoreCategories()) {
            updateCurrentScoreCategory();
            currentDesignSubject = DesignSubject.CATEGORY_SCORE;
            return "How much score would this answer give for category " + "\"" + currentCategory + "\"? ";
        } else {
            currentQuizAnswer.setCategoryScore("General", 1.0);

            if (currentQuizQuestion.getAnswerNum() < numAnswerEachQ) {
                currentDesignSubject = DesignSubject.QUIZ_ANSWER;
                return "Enter answer " + (currentQuizQuestion.getAnswerNum() + 1) + ": ";
            } else if (chosenTemplate.isMultipleChoice() && chosenTemplate.isChooseAllThatApply()) {
                currentDesignSubject = DesignSubject.CORRECT_ANSWERS;
                return "Enter the indices of the correct answers, separated by space: ";
            } else if (chosenTemplate.isMultipleChoice()) {
                currentDesignSubject = DesignSubject.CORRECT_ANSWER;
                return "Enter the index of the correct answer";
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
            return "Enter question " + (currentGame.getNumQuestions() + 1) + ": ";
        }
        else {
            currentDesignSubject = DesignSubject.IS_PUBLIC;
            return "Do you want to make this game public?";
        }
    }

    private String handleCategoryScoreDesignChoice(String designChoice) {
        try {
            currentQuizAnswer.setCategoryScore(currentCategory, Double.parseDouble(designChoice));
            updateCurrentScoreCategory();
            if (currentCategory == null) {
                if (currentQuizQuestion.getAnswerNum() < numAnswerEachQ) {
                    currentDesignSubject = DesignSubject.QUIZ_ANSWER;
                    return "Enter answer " + (currentQuizQuestion.getAnswerNum() + 1) + ": ";
                }
                else {
                    return handleIsEnoughQuestions();
                }
            }
            else {
                currentDesignSubject = DesignSubject.CATEGORY_SCORE;
                return "How much score would this answer give for category " + "\"" + currentCategory + "\"? ";
            }
        }
        catch (NumberFormatException e) {
            return "Invalid input for score, please make sure score is a double. Re-enter the number:";
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
        return "Enter first answer: ";
    }

    private String handleMaxAttemptDesignChoice(String designChoice) {
        currentGame.setMaxAttempts(Integer.parseInt(designChoice));
        currentDesignSubject = DesignSubject.QUIZ_QUESTION;
        return "Enter the first question: ";
    }

    private String handleCategoryEndMessageDesignChoice(String designChoice) {

        currentGame.setEndingMessage(currentCategory, designChoice);
        if (numScoreCategories > currentGame.getNumScoreCategories()) {
            currentDesignSubject = DesignSubject.CATEGORY_NAME;
            return "Enter the next category name: ";
        }
        else {
            currentDesignSubject = DesignSubject.MAX_ATTEMPTS;
            return "Enter the maximum number of attempts for each question:";
        }
    }

    private String handleCategoryNameDesignChoice(String designChoice) {
        currentGame.addScoreCategory(designChoice);
        if (chosenTemplate.hasCustomEndingMessage()) {
            currentCategory = designChoice;
            currentDesignSubject = DesignSubject.CATEGORY_END_MESSAGE;
            return "What is the ending message when this category has the highest score?";
        }
        currentGame.setEndingMessage(designChoice, "Category with highest score is: " + designChoice + ".");
        if (numScoreCategories > currentGame.getNumScoreCategories()) {
            currentDesignSubject = DesignSubject.CATEGORY_NAME;
            return "Enter the next category name: ";
        }
        else {
            currentDesignSubject = DesignSubject.MAX_ATTEMPTS;
            return "Enter the maximum number of attempts for each question:";
        }
    }

    private String handleCategoryNumDesignChoice(String designChoice) {
        numScoreCategories = Integer.parseInt(designChoice);
        currentDesignSubject = DesignSubject.CATEGORY_NAME;
        return "Enter the first category name";
    }

    private String handleAnswerNumDesignChoice(String designChoice) {
        numAnswerEachQ = Integer.parseInt(designChoice);
        if (chosenTemplate.hasMultipleScoreCategories()) {
            currentDesignSubject = DesignSubject.CATEGORIES_NUM;
            return "Enter the number of score categories of your quiz: ";
        }
        else {
            currentGame.addScoreCategory("General");
            currentDesignSubject = DesignSubject.MAX_ATTEMPTS;
            return "Enter the maximum number of attempts for each question:";
        }
    }

    private String handleQuestionNumDesignChoice(String designChoice) throws InvalidInputException {
        try {
            numQuestions = Integer.parseInt(designChoice);
            if (chosenTemplate.isMultipleChoice()) {
                currentDesignSubject = DesignSubject.ANSWER_NUM;
                return "Enter the number of answers for each question:";
            }

            else { // The case of Exact answer quiz
                numAnswerEachQ = 1;
                currentGame.addScoreCategory("General");
                currentDesignSubject = DesignSubject.MAX_ATTEMPTS;
                return "Enter the maximum number of attempts for each question: ";
            }
        }
        catch (NumberFormatException e) {
            throw new InvalidInputException();
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
        return "How many questions does your quiz have?";
    }


    @Override
    public QuizGame build(String id) throws InsufficientInputException {
        if (!isReadyToBuild()) {
            throw new InsufficientInputException();
        }
        currentGame.setID(id);
        currentGame.setOwnerId(this.getCreatorID());
        currentGame.setTemplateID(chosenTemplate.getID());
        return currentGame;
    }

}
