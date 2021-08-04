package system.use_cases.factories;

import shared.constants.GameGenre;
import system.use_cases.builders.interactive_builders.QuizTemplateInteractiveBuilder;
import system.use_cases.builders.interactive_builders.TemplateInteractiveBuilder;

public class TemplateBuilderFactory {

    public TemplateInteractiveBuilder getTemplateBuilder(GameGenre gameGenre) {
        if (gameGenre == GameGenre.QUIZ) {
            return (TemplateInteractiveBuilder) new QuizTemplateInteractiveBuilder();
        }
        else {
            return null;
        }
    }
}
