package system.use_cases.factories;

import shared.constants.GameGenre;
import system.entities.template.HangmanTemplate;
import system.entities.template.QuizTemplate;
import system.entities.template.Template;

public class TemplateFactory {
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
