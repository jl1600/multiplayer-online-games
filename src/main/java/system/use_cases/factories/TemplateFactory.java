package system.use_cases.factories;

import shared.constants.GameGenre;
import system.entities.template.HangmanTemplate;
import system.entities.template.QuizTemplate;
import system.entities.template.Template;

/**
 * TemplateFactory Class
 */
public class TemplateFactory {
    /**
     * choose which template to get based on parameters
     * @param type the genre of the game
     * @return  appropriate template based on parameter
     */
    public Template getTemplate(GameGenre type) {
        switch (type) {
            case QUIZ:
                return new QuizTemplate();
            case HANGMAN:
                return new HangmanTemplate();
            default:
                return null;
        }
    }
}
