package system.use_cases.builders.normal_builders;

import shared.constants.GameAccessLevel;
import shared.exceptions.use_case_exceptions.CreationInProgressException;
import shared.exceptions.use_case_exceptions.InsufficientInputException;
import system.entities.game.hangman.HangmanGame;

public class HangmanGameBuilder {
    private HangmanGame currentGame;
    private boolean hasGameId;
    private boolean hasTemplateId;
    private boolean hasOwnerId;
    private boolean hasTitle;
    private boolean hasGameAccessLevel;
    private boolean hasPuzzle;

    public HangmanGameBuilder() {
        this.currentGame = new HangmanGame();
    }

    public void setGameId(String gameId) {
        this.currentGame.setID(gameId);
        this.hasGameId = true;
    }

    public void setTemplateId(String templateId) {
        this.currentGame.setTemplateId(templateId);
        this.hasTemplateId = true;
    }

    public void setOwnerId(String ownerId) {
        this.currentGame.setOwnerId(ownerId);
        this.hasOwnerId = true;
    }

    public void setTitle(String title) {
        this.currentGame.setTitle(title);
        this.hasTitle = true;
    }

    public void setGameAccessLevel(GameAccessLevel gameAccessLevel) {
        this.currentGame.setGameAccessLevel(gameAccessLevel);
        this.hasGameAccessLevel = true;
    }

    public void addPuzzle(String puzzle, String prompt) throws InsufficientInputException {
        this.currentGame.addPuzzle(puzzle, prompt);
        this.hasPuzzle = true;
    }

    public boolean isReadyToBuild() {
        return hasGameId &
                hasTemplateId &
                hasOwnerId &
                hasTitle &
                hasGameAccessLevel &
                hasPuzzle;
    }

    public HangmanGame toHangmanGame() throws CreationInProgressException {
        if (!isReadyToBuild()) {
            throw new CreationInProgressException();
        }
        return this.currentGame;
    }

}
