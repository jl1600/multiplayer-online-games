package system.use_cases.factories;

import system.entities.game.Game;
import system.use_cases.game_matches.GameMatch;
import system.entities.game.quiz.QuizGame;
import system.use_cases.game_matches.QuizGameMatch;
import system.entities.template.QuizTemplate;
import system.entities.template.Template;

public class GameMatchFactory {
    public GameMatch getGameMatch(String matchID, String userID, Game game, Template template) {
        if (template instanceof QuizTemplate && game instanceof QuizGame) {
            return new QuizGameMatch(matchID, userID, (QuizGame) game, (QuizTemplate) template);
        }
        else return null;
    }
}
