package system.gateways;

import shared.exceptions.entities_exception.IDAlreadySetException;
import shared.exceptions.entities_exception.UnknownGameTypeException;
import shared.exceptions.use_case_exceptions.InvalidGameIDException;
import shared.exceptions.use_case_exceptions.InvalidIDException;
import system.entities.game.Game;
import system.entities.game.hangman.HangmanGame;
import system.entities.game.quiz.QuizGame;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GameDataMapper implements GameDataGateway {

    QuizGameDataMapper quizMapper = new QuizGameDataMapper();
    HangmanGameDataMapper hangMapper = new HangmanGameDataMapper();

    @Override
    public void addGame(Game game) throws IOException, UnknownGameTypeException {
        if (game instanceof QuizGame) {
            quizMapper.addGame((QuizGame) game);
        } else if (game instanceof HangmanGame) {
            hangMapper.addGame((HangmanGame) game);
        } else {
            throw new UnknownGameTypeException();
        }
    }

    @Override
    public void updateGame(Game game) throws InvalidGameIDException, IOException, UnknownGameTypeException {
        if (game instanceof QuizGame) {
            quizMapper.updateGame((QuizGame) game);
        } else if (game instanceof HangmanGame) {
            hangMapper.updateGame((HangmanGame) game);
        } else {
            throw new UnknownGameTypeException();
        }
    }

    @Override
    public void deleteGame(Game game) throws InvalidGameIDException, IOException, UnknownGameTypeException {
        if (game instanceof QuizGame) {
            quizMapper.deleteGame((QuizGame) game);
        } else if (game instanceof HangmanGame) {
            hangMapper.deleteGame((HangmanGame) game);
        } else {
            throw new UnknownGameTypeException();
        }
    }

    @Override
    public Set<Game> getAllGames() throws IOException, IDAlreadySetException {
        HashSet<Game> result = new HashSet<>();
        result.addAll(quizMapper.getAllGames());
        result.addAll(hangMapper.getAllGames());
        return result;
    }

    @Override
    public int getGameCount() throws IOException {
        int result = 0;
        result += quizMapper.getGameCount();
        return result;
    }
}
