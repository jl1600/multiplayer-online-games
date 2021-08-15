package system.use_cases.builders.interactive_builders;

import shared.constants.GameAccessLevel;
import shared.exceptions.use_case_exceptions.CreationInProgressException;
import shared.exceptions.use_case_exceptions.InsufficientInputException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.entities.game.Game;
import system.entities.game.hangman.HangmanGame;
import system.entities.template.HangmanTemplate;
import system.use_cases.builders.normal_builders.HangmanGameBuilder;

public class HangmanGameInteractiveBuilder extends GameInteractiveBuilder {
    private enum DesignQuestion {
        TITLE, PUBLIC, PUZZLE_ANSWER, PUZZLE_PROMPT, CONFIRMATION
    }

    private final String[] DESIGN_QUESTION_TEXT = {
            "Does your game have a title? If yes enter it, otherwise press enter."
            , "Will the game be public? enter yes/no."
            , "What is the answer to the next puzzle?"
            , "What is the prompt for that puzzle?"
            , "Confirm you would like to create this game, enter yes/no."
    };

    private HangmanGame currentGame;
    private HangmanGameBuilder builder;
    private HangmanTemplate chosenTemplate;
    private DesignQuestion currentDesignQuestion;
    private String currentDesignQuestionText;

    private int currentPuzzleIndex;

    public HangmanGameInteractiveBuilder(String creatorId, HangmanTemplate template) {
        super(creatorId);
        chosenTemplate = template;

        builder = new HangmanGameBuilder();
        builder.setOwnerId(this.getCreatorID());
        builder.setTemplateId(template.getID());

        currentPuzzleIndex = 0;

        currentDesignQuestion = DesignQuestion.TITLE;
        currentDesignQuestionText = DESIGN_QUESTION_TEXT[0];
    }

    @Override
    public String getDesignQuestion() {
        return currentDesignQuestionText;
    }


    @Override
    public void makeDesignChoice(String designChoice) throws InvalidInputException {
        switch (currentDesignQuestion) {
            case TITLE:
                handleTitleDesignQuestion(designChoice);
                break;
            case PUBLIC:
                handlePublicDesignQuestion(designChoice);
                break;
            case PUZZLE_ANSWER:
                handleAnswerDesignQuestion(designChoice);
                break;
            case PUZZLE_PROMPT:
                handlePromptQuestion(designChoice);
                break;
            case CONFIRMATION:
                handleConfirmationQuestion(designChoice);
                break;
        }
    }

    private void handleTitleDesignQuestion(String designChoice) {
        if (designChoice.equals("")) {
            builder.setTitle("HangmanGame");
        } else {
            builder.setTitle(designChoice);
        }
        currentDesignQuestion = DesignQuestion.PUBLIC;
        currentDesignQuestionText = DESIGN_QUESTION_TEXT[1];
    }

    private void handlePublicDesignQuestion(String designChoice) throws InvalidInputException {
        if (designChoice.equals("yes")) {
            builder.setGameAccessLevel(GameAccessLevel.PUBLIC);
        } else if (designChoice.equals("no")) {
            builder.setGameAccessLevel(GameAccessLevel.PRIVATE);
        } else {
            throw new InvalidInputException();
        }
        currentDesignQuestion = DesignQuestion.PUZZLE_ANSWER;
        currentDesignQuestionText = DESIGN_QUESTION_TEXT[2];
    }

    private void handleAnswerDesignQuestion(String designChoice) throws InvalidInputException {
        builder.addAnswer(currentPuzzleIndex, designChoice);
        System.out.println(currentDesignQuestionText);
        currentDesignQuestion = DesignQuestion.PUZZLE_PROMPT;
        currentDesignQuestionText = DESIGN_QUESTION_TEXT[3];
    }

    private void handlePromptQuestion(String designChoice) throws InvalidInputException {
        builder.addPrompt(currentPuzzleIndex, designChoice);
        currentPuzzleIndex++;
        if (currentPuzzleIndex < chosenTemplate.getNumPuzzles()) {
            currentDesignQuestion = DesignQuestion.PUZZLE_ANSWER;
            currentDesignQuestionText = DESIGN_QUESTION_TEXT[2];
        } else {
            currentDesignQuestion = DesignQuestion.CONFIRMATION;
            currentDesignQuestionText = DESIGN_QUESTION_TEXT[4];
        }
    }

    private void handleConfirmationQuestion(String designChoice) throws InvalidInputException {
        if (designChoice.equals("yes")) {
            this.readyToBuild = true;
        } else if (designChoice.equals("no")) {
            builder = new HangmanGameBuilder();
            builder.setOwnerId(this.getCreatorID());
            builder.setTemplateId(chosenTemplate.getID());
            currentPuzzleIndex = 0;
            currentDesignQuestion = DesignQuestion.TITLE;
            currentDesignQuestionText = DESIGN_QUESTION_TEXT[0];
        } else {
            throw new InvalidInputException();
        }
    }

    @Override
    public HangmanGame build(String id) throws InsufficientInputException {
        builder.setGameId(id);
        try {
            currentGame = builder.toHangmanGame();
        } catch (CreationInProgressException e) {
            System.out.println("in progress");
            throw new InsufficientInputException();
        }
        return currentGame;
    }
}
