package system.use_cases.builders.interactive_builders;

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
            , "What is the answer to the puzzle?"
            , "What is the prompt for the puzzle?"
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
        builder.setOwnerId(creatorId);
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
    public Game build(String id) throws InsufficientInputException {
        builder.setGameId(id);
        try {
            currentGame = builder.toHangmanGame();
        } catch (CreationInProgressException e) {
            throw new InsufficientInputException();
        }
        return currentGame;
    }

    @Override
    public void makeDesignChoice(String designChoice) {
        currentDesignQuestionText = update(designChoice);
    }

    private String update(String designChoice) {
        switch (currentDesignQuestion) {
            case TITLE:
                return handleTitleDesignQuestion(designChoice);
            case PUBLIC:
                return handlePublicDesignQuestion(designChoice);
            case PUZZLE_ANSWER:
                return handleAnswerDesignQuestion(designChoice);
            case PUZZLE_PROMPT:
                return handlePromptQuestion(designChoice);
        }
        return "Design question not recognized. Some error occurred.";
    }

    private String handleTitleDesignQuestion(String designChoice) {
        if (designChoice == null | designChoice.equals("")) {
            builder.setTitle("HangmanGame " + currentGame.getID());
        } else {
            builder.setTitle(designChoice);
        }
        currentDesignQuestion = DesignQuestion.PUBLIC;
        return DESIGN_QUESTION_TEXT[1];
    }

    private String handlePublicDesignQuestion(String designChoice) {
        if (designChoice.equals("yes")) {
            builder.setIsPublic(true);
        } else if (designChoice.equals("no")) {
            builder.setIsPublic(false);
        } else {
          //throw some error
        }
        currentDesignQuestion = DesignQuestion.PUZZLE_ANSWER;
        return DESIGN_QUESTION_TEXT[2];
    }

    private String handleAnswerDesignQuestion(String designChoice) {
        try {
            builder.addAnswer(currentPuzzleIndex, designChoice);
        } catch (InsufficientInputException e) {
            e.printStackTrace();
        }
        currentDesignQuestion = DesignQuestion.PUZZLE_PROMPT;
        return DESIGN_QUESTION_TEXT[3];
    }

    private String handlePromptQuestion(String designChoice) {
        try {
            builder.addPrompt(currentPuzzleIndex, designChoice);
        } catch (InsufficientInputException e) {
            e.printStackTrace();
        }

        currentPuzzleIndex++;
        if (currentPuzzleIndex < chosenTemplate.getNumPuzzles()) {
            currentDesignQuestion = DesignQuestion.PUZZLE_ANSWER;
            return DESIGN_QUESTION_TEXT[2];
        } else {
            currentDesignQuestion = DesignQuestion.CONFIRMATION;
            return DESIGN_QUESTION_TEXT[4];
        }
    }
}
