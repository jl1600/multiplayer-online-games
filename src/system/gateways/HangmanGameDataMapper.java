package system.gateways;

import shared.exceptions.entities_exception.IDAlreadySetException;
import shared.exceptions.use_case_exceptions.*;
import system.entities.game.hangman.HangmanGame;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class HangmanGameDataMapper {
    String subfolder = "hangman/";

    public void addGame(HangmanGame game) throws IOException {

    }

    public void updateGame(HangmanGame game) throws InvalidGameIDException, IOException {

    }

    public void deleteGame(HangmanGame game) throws IOException, InvalidGameIDException {

    }

    public Set<HangmanGame> getAllGames() throws IOException, IDAlreadySetException {
        return new HashSet<>();
    }

    public int getGameCount() throws IOException {
        return 0;
    }
}