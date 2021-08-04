package system.use_cases.builders.interactive_builders;

import shared.exceptions.use_case_exceptions.InsufficientInputException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.entities.template.QuizTemplate;
import system.entities.template.Template;

public class QuizTemplateInteractiveBuilder extends TemplateInteractiveBuilder {

    QuizTemplate currentTemplate;
    String currentDesignQuestion;

    static class TemplateDesignQuestions {

        static final String NAME = "Enter the name of your template";
        static final String MULTIPLE_CHOICE = "Is your quiz multiple choice? (yes/no)";
        static final String CHOOSE_ALL_THAT_APPLY = "Is your quiz choose all that apply? (yes/no)";
        static final String MULTIPLE_SCORE_CATEGORIES = "Does your quiz have multiple categories for score? (yes/no)";
        static final String SCORE_WEIGHT = "Do your score categories have different weights? (yes/no)" ;
        static final String HAS_ENDING_MESSAGE = "Does your quiz have custom ending messages? (yes/no)";
        static final String CONFIRMATION = "Ready to create new template. Are you sure that you want proceed? (yes/no)";
        static final String NULL = "";
    }

    public QuizTemplateInteractiveBuilder() {
        currentTemplate = new QuizTemplate();
        currentDesignQuestion = TemplateDesignQuestions.NAME;
    }

    @Override
    public void makeDesignChoice(String designChoice) throws InvalidInputException {

        switch(currentDesignQuestion) {

            case TemplateDesignQuestions.NAME:
                handleNameDesignChoice(designChoice);
                break;
            case TemplateDesignQuestions.MULTIPLE_CHOICE:
                handleMultipleChoiceDesignChoice(designChoice);
                break;
            case TemplateDesignQuestions.CHOOSE_ALL_THAT_APPLY:
                handleChooseAllThatApplyDesignChoice(designChoice);
                break;
            case TemplateDesignQuestions.MULTIPLE_SCORE_CATEGORIES:
                handleMultipleScoreCategoriesDesignChoice(designChoice);
                break;
            case TemplateDesignQuestions.SCORE_WEIGHT:
                handleScoreWeightDesignChoice(designChoice);
                break;
            case TemplateDesignQuestions.HAS_ENDING_MESSAGE:
                handleHasEndingMessageDesignChoice(designChoice);
                break;
            case TemplateDesignQuestions.CONFIRMATION:
                handleConfirmationDesignChoice(designChoice);
                break;
            default:
                break;
        }

    }

    private void handleNameDesignChoice(String designChoice){
        currentTemplate.setTitle(designChoice);
        currentDesignQuestion = TemplateDesignQuestions.MULTIPLE_CHOICE;
    }

    private void handleMultipleChoiceDesignChoice(String designChoice) throws InvalidInputException{
        if (!(designChoice.equals("yes") || designChoice.equals("no")))
            throw new InvalidInputException();
        currentTemplate.setMultipleChoice(designChoice.equals("yes"));
        if (currentTemplate.isMultipleChoice())
            currentDesignQuestion = TemplateDesignQuestions.CHOOSE_ALL_THAT_APPLY;
        else currentDesignQuestion = TemplateDesignQuestions.CONFIRMATION;
    }

    private void handleChooseAllThatApplyDesignChoice(String designChoice) throws InvalidInputException{
        if (!(designChoice.equals("yes") || designChoice.equals("no")))
            throw new InvalidInputException();
        currentTemplate.setChooseAllThatApply(designChoice.equals("yes"));
        currentDesignQuestion = TemplateDesignQuestions.MULTIPLE_SCORE_CATEGORIES;
    }

    private void handleMultipleScoreCategoriesDesignChoice(String designChoice) throws InvalidInputException {
        if (designChoice.equals("yes"))
            currentDesignQuestion = TemplateDesignQuestions.HAS_ENDING_MESSAGE;
        else if (designChoice.equals("no")) {
            currentDesignQuestion = TemplateDesignQuestions.HAS_ENDING_MESSAGE;
            currentTemplate.setHasScoreWeight(false);
        } else {
            throw new InvalidInputException();
        }
        currentTemplate.setHasMultipleScoreCategories(designChoice.equals("yes"));
    }

    private void handleScoreWeightDesignChoice(String designChoice) throws InvalidInputException{
        if (!(designChoice.equals("yes") || designChoice.equals("no")))
            throw new InvalidInputException();
        currentTemplate.setHasScoreWeight(designChoice.equals("yes"));
        currentDesignQuestion = TemplateDesignQuestions.HAS_ENDING_MESSAGE;
    }

    private void handleHasEndingMessageDesignChoice(String designChoice) throws InvalidInputException{
        if (!(designChoice.equals("yes") || designChoice.equals("no")))
            throw new InvalidInputException();
        currentTemplate.setHasCustomEndingMessage(designChoice.equals("yes"));
        currentDesignQuestion = TemplateDesignQuestions.CONFIRMATION;
    }

    private void handleConfirmationDesignChoice(String designChoice) throws InvalidInputException{
        if (!(designChoice.equals("yes") || designChoice.equals("no")))
            throw new InvalidInputException();
        if (designChoice.equals("yes")){
            readyToBuild = true;
            currentDesignQuestion = TemplateDesignQuestions.NULL;
        }
        else {
            // abort the current template, make a new one
            currentTemplate = new QuizTemplate();
            currentDesignQuestion = TemplateDesignQuestions.NAME;
        }
    }

    @Override
    public String getDesignQuestion(){
        return currentDesignQuestion;
    }

    @Override
    public Template build(String id) throws InsufficientInputException {
        if (!isReadyToBuild()) {
            throw new InsufficientInputException();
        }
        currentTemplate.setID(id);
        return currentTemplate;
    }



}
