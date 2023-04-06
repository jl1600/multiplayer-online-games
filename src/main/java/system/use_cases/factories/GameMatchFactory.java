package system.use_cases.factories;

import system.entities.game.Game;
import system.entities.game.hangman.HangmanGame;
import system.entities.template.HangmanTemplate;
import system.use_cases.game_matches.GameMatch;
import system.entities.game.quiz.QuizGame;
import system.use_cases.game_matches.HangmanMatch;
import system.use_cases.game_matches.QuizGameMatch;
import system.entities.template.QuizTemplate;
import system.entities.template.Template;

/**
 * GameMatchFactory Class
 */
public class GameMatchFactory {

    /**
     * choose which gameMatch to get based on parameters
     * @param matchID the match's id
     * @param userID the host user's id
     * @param username the host user name
     * @param game the game this match uses
     * @param template the template this match uses
     * @return appropriate game match based on given template and matches
     */
    public GameMatch getGameMatch(String matchID, String userID, String username, Game game, Template template) {
        if (template instanceof QuizTemplate && game instanceof QuizGame) {
            return new QuizGameMatch(matchID, userID, username, (QuizGame) game, (QuizTemplate) template);
        } else if (template instanceof HangmanTemplate && game instanceof HangmanGame) {
            return new HangmanMatch(matchID, userID, username, (HangmanGame) game, (HangmanTemplate) template);
        } else return null;
    }
}
