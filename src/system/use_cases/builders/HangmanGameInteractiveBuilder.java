package system.use_cases.builders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import shared.constants.GameAccessLevel;
import shared.exceptions.use_case_exceptions.NotReadyException;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.entities.game.hangman.HangmanGame;
import system.entities.template.HangmanTemplate;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * HangmanGameInteractiveBuilder Class
 */
public class HangmanGameInteractiveBuilder extends GameInteractiveBuilder {

    private enum DesignTopic {
        TITLE, NUM_PUZZLES, NUM_LIVES, NUM_HINTS, PUZZLE_ANSWER, PUZZLE_PROMPT, PUBLIC, CONFIRMATION
    }

    private HangmanGame currentGame;
    private final HangmanTemplate chosenTemplate;
    private DesignTopic currentDesignTopic;
    private final Map<DesignTopic, String> designQuestions;

    private int currentPuzzleIndex;

    /**
     * Constructor of HangmanGameInteractiveBuilder
     * @param creatorId the id of the creator user
     * @param template the template this game will use
     */
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
            JsonReader reader = new JsonReader(new FileReader(
                    "src/system/configuration_files/hangman_design_questions.json"));
            Type type = new TypeToken<Map<DesignTopic, String>>(){}.getType();
            designQuestions = gson.fromJson(reader, type);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Fatal: Can't find the configuration file for hangman design questions.");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDesignQuestion() {
        return designQuestions.get(currentDesignTopic);
    }


    /**
     * {@inheritDoc}
     */
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
        if (designChoice.equals("no")) {
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
            if (chosenTemplate.hasHints())
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

    /**
     * {@inheritDoc}
     */
    @Override
    public HangmanGame build(String id) throws NotReadyException {

        if (!isReadyToBuild())
            throw new NotReadyException();

        currentGame.setID(id);
        return currentGame;
    }
}
