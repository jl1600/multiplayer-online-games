package system.use_cases.builders.interactive_builders;

import shared.exceptions.use_case_exceptions.InsufficientInputException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.entities.template.HangmanTemplate;
import system.entities.template.Template;
import java.util.regex.Pattern;

public class HangmanTemplateInteractiveBuilder extends TemplateInteractiveBuilder {

    HangmanTemplate currentTemplate;
    DesignQuestion currentDesignQuestion;
    String currentDesignQuestionText;

    private enum DesignQuestion {
        TITLE, NUM_PUZZLES, NUM_LIVES, NUM_HINTS, CONFIRMATION
    }

    private final String[] DESIGN_QUESTION_TEXT = {
            "Does your game have a title? If yes enter it, otherwise press enter."
            , "How many puzzles does your hangman game have? Enter a number."
            , "How many lives will players get in total? Enter a number."
            , "How many hints will players get in total? Enter a number."
            , "Confirm you would like to create this template. Enter yes/no."
    };

    public HangmanTemplateInteractiveBuilder() {
        currentTemplate = new HangmanTemplate();
        currentDesignQuestion = DesignQuestion.TITLE;
        currentDesignQuestionText = DESIGN_QUESTION_TEXT[0];
    }

    @Override
    public String getDesignQuestion() {
        return currentDesignQuestionText;
    }

    @Override
    public void makeDesignChoice(String designChoice) {
        switch (currentDesignQuestion) {
            case TITLE:
                handleTitleDesignChoice(designChoice);
                break;
            case NUM_PUZZLES:
                handlePuzzlesDesignChoice(designChoice);
                break;
            case NUM_HINTS:
                handleHintsDesignChoice(designChoice);
                break;
            case NUM_LIVES:
                handleLivesDesignChoice(designChoice);
                break;
            case CONFIRMATION:
                handleConfirmationDesignChoice(designChoice);
                break;
        }
    }

    @Override
    public Template build(String id) throws InsufficientInputException {
        currentTemplate.setID(id);
        if (!isReadyToBuild()) {
            throw new InsufficientInputException();
        }
        return currentTemplate;
    }

    private void handleTitleDesignChoice(String designChoice) {
        if (designChoice.equals("")) {
            currentTemplate.setTitle("HangmanGame Template");
        } else {
            currentTemplate.setTitle(designChoice);
        }
        currentDesignQuestion = DesignQuestion.NUM_PUZZLES;
        currentDesignQuestionText = DESIGN_QUESTION_TEXT[1];
    }

    private void handlePuzzlesDesignChoice(String designChoice) {
        if (Pattern.matches("[0-9]*", designChoice)) {
            int value = Integer.parseInt(designChoice);
            try {
                currentTemplate.setNumPuzzles(value);
                currentDesignQuestion = DesignQuestion.NUM_LIVES;
                currentDesignQuestionText = DESIGN_QUESTION_TEXT[2];
            } catch (InsufficientInputException e) {
                currentDesignQuestionText = "Invalid value entered. " + DESIGN_QUESTION_TEXT[1];
            }
        } else {
            currentDesignQuestionText = "Invalid value entered. " + DESIGN_QUESTION_TEXT[1];
        }
    }

    private void handleLivesDesignChoice(String designChoice) {
        if (Pattern.matches("[0-9]*", designChoice)) {
            int value = Integer.parseInt(designChoice);
            try {
                currentTemplate.setNumLives(value);
                currentDesignQuestion = DesignQuestion.NUM_HINTS;
                currentDesignQuestionText = DESIGN_QUESTION_TEXT[3];
            } catch (InsufficientInputException e) {
                currentDesignQuestionText = "Invalid value entered. " + DESIGN_QUESTION_TEXT[2];
            }
        } else {
            currentDesignQuestionText = "Invalid value entered. " + DESIGN_QUESTION_TEXT[2];
        }
    }

    private void handleHintsDesignChoice(String designChoice) {
        if (Pattern.matches("[0-9]*", designChoice)) {
            int value = Integer.parseInt(designChoice);
            try {
                currentTemplate.setNumHints(value);
                currentDesignQuestion = DesignQuestion.CONFIRMATION;
                currentDesignQuestionText = DESIGN_QUESTION_TEXT[4];
            } catch (InsufficientInputException e) {
                currentDesignQuestionText = "Invalid value entered. " + DESIGN_QUESTION_TEXT[3];
            }
        } else {
            currentDesignQuestionText = "Invalid value entered. " + DESIGN_QUESTION_TEXT[3];
        }
    }

    private void handleConfirmationDesignChoice(String designChoice) {
        if (designChoice.equals("yes")) {
            this.readyToBuild = true;
        } else if (designChoice.equals("no")) {
            currentTemplate = new HangmanTemplate();
            currentDesignQuestion = DesignQuestion.TITLE;
            currentDesignQuestionText = DESIGN_QUESTION_TEXT[0];
        } else {
            currentDesignQuestionText = "Invalid input. " + DESIGN_QUESTION_TEXT[4];
        }
    }


}

