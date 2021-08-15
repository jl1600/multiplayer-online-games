package system.use_cases.builders;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import shared.constants.GameAccessLevel;
import shared.exceptions.use_case_exceptions.InsufficientInputException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.entities.game.hangman.HangmanGame;
import system.entities.template.HangmanTemplate;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HangmanGameInteractiveBuilder extends GameInteractiveBuilder {

    int numPuzzles = 0;
    int numLives = 0;
    int numHints = 0;

    private enum DesignTopic {
        TITLE, NUM_PUZZLES, NUM_LIVES, NUM_HINTS, PUZZLE_ANSWER, PUZZLE_PROMPT, PUBLIC, CONFIRMATION
    }

    private HangmanGame currentGame;
    private final HangmanTemplate chosenTemplate;
    private DesignTopic currentDesignTopic;
    private final Map<DesignTopic, String> designQuestions;

    private int currentPuzzleIndex;

    public HangmanGameInteractiveBuilder(String creatorId, HangmanTemplate template) {
        super(creatorId);
        chosenTemplate = template;
        currentGame = new HangmanGame();
        currentGame.setOwnerId(this.getCreatorID());
        currentGame.setTemplateId(chosenTemplate.getID());
        currentPuzzleIndex = 0;
        currentDesignTopic = DesignTopic.TITLE;
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader("hangman_design_questions.JSON"));
            designQuestions = gson.fromJson(reader, HashMap.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Fatal: Can't find the configuration file for hangman design questions.");
        }

    }

    @Override
    public String getDesignQuestion() {
        return designQuestions.get(currentDesignTopic);
    }


    @Override
    public void makeDesignChoice(String designChoice) throws InvalidInputException {
        switch (currentDesignTopic) {
            case TITLE:
                handleTitle(designChoice);
                break;
            case NUM_PUZZLES:
                handleNumPuzzles(designChoice);
                break;
            case NUM_LIVES:
                handleNumLives(designChoice);
                break;
            case NUM_HINTS:
                handleNumHints(designChoice);
                break;
            case PUZZLE_ANSWER:
                handleAnswer(designChoice);
                break;
            case PUZZLE_PROMPT:
                handlePrompt(designChoice);
                break;
            case PUBLIC:
                handlePublic(designChoice);
                break;
            case CONFIRMATION:
                handleConfirmation(designChoice);
                break;
        }
    }

    private void handleTitle(String designChoice) {
        if (designChoice.equals("")) {
            currentGame.setTitle("Unnamed Hangman Game");
        } else {
            currentGame.setTitle(designChoice);
        }
        currentDesignTopic = DesignTopic.NUM_PUZZLES;
    }

    private void handlePublic(String designChoice) throws InvalidInputException {
        if (designChoice.equals("yes")) {
            currentGame.setGameAccessLevel(GameAccessLevel.PUBLIC);
        } else if (designChoice.equals("no")) {
            currentGame.setGameAccessLevel(GameAccessLevel.PRIVATE);
        } else {
            throw new InvalidInputException();
        }
        currentDesignTopic = DesignTopic.CONFIRMATION;
    }

    private void handleAnswer(String designChoice) throws InvalidInputException {
        currentGame.addAnswer(currentPuzzleIndex, designChoice);
        currentDesignTopic = DesignTopic.PUZZLE_PROMPT;
    }

    private void handlePrompt(String designChoice) throws InvalidInputException {
        currentGame.addPrompt(currentPuzzleIndex, designChoice);
        currentPuzzleIndex++;
        if (currentPuzzleIndex < currentGame.getNumPuzzles()) {
            currentDesignTopic = DesignTopic.PUZZLE_ANSWER;
        } else {
            currentDesignTopic = DesignTopic.PUBLIC;
        }
    }

    private void handleConfirmation(String designChoice) throws InvalidInputException {
        if (designChoice.equals("yes")) {
            this.readyToBuild = true;
        } else if (designChoice.equals("no")) {
            currentGame = new HangmanGame();
            currentGame.setOwnerId(this.getCreatorID());
            currentGame.setTemplateId(chosenTemplate.getID());
            currentPuzzleIndex = 0;
            currentDesignTopic = DesignTopic.TITLE;
        } else {
            throw new InvalidInputException();
        }
    }

    private void handleNumPuzzles(String designChoice) throws InvalidInputException {
        if (Pattern.matches("[0-9]*", designChoice)) {
            int value = Integer.parseInt(designChoice);
            currentGame.setNumPuzzles(value);
            currentDesignTopic = DesignTopic.NUM_LIVES;
        } else {
            throw new InvalidInputException();
        }
    }

    private void handleNumLives(String designChoice) throws InvalidInputException {
        if (Pattern.matches("[0-9]*", designChoice)) {
            int value = Integer.parseInt(designChoice);
            currentGame.setNumLives(value);
            if (chosenTemplate.haveHints())
                currentDesignTopic = DesignTopic.NUM_HINTS;
            else {
                currentGame.setNumHints(0);
                currentDesignTopic = DesignTopic.PUZZLE_ANSWER;
            }
        } else {
            throw new InvalidInputException();
        }
    }

    private void handleNumHints(String designChoice) throws InvalidInputException {
        if (Pattern.matches("[0-9]*", designChoice)) {
            int value = Integer.parseInt(designChoice);
            currentGame.setNumHints(value);
            currentDesignTopic = DesignTopic.PUZZLE_ANSWER;
        } else {
            throw new InvalidInputException();
        }
    }

    @Override
    public HangmanGame build(String id) throws InsufficientInputException {

        if (!isReadyToBuild())
            throw new InsufficientInputException();

        currentGame.setID(id);
        return currentGame;
    }
}
