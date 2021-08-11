package system.use_cases.factories;

import shared.constants.GameGenre;
import system.use_cases.builders.interactive_builders.HangmanTemplateInteractiveBuilder;
import system.use_cases.builders.interactive_builders.QuizTemplateInteractiveBuilder;
import system.use_cases.builders.interactive_builders.TemplateInteractiveBuilder;

public class TemplateBuilderFactory {

    public TemplateInteractiveBuilder getTemplateBuilder(GameGenre gameGenre) {
        if (gameGenre == GameGenre.QUIZ) {
            return new QuizTemplateInteractiveBuilder();
        } else if (gameGenre == GameGenre.HANGMAN) {
            return new HangmanTemplateInteractiveBuilder();
        } else {
            return null;
        }
    }
}
