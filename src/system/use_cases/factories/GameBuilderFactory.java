package system.use_cases.factories;

import system.entities.template.Template;
import system.entities.template.QuizTemplate;
import system.use_cases.builders.interactive_builders.GameInteractiveBuilder;
import system.use_cases.builders.interactive_builders.QuizGameInteractiveBuilder;

public class GameBuilderFactory {
    public GameInteractiveBuilder getGameBuilder(String creatorName, Template template) {
        if (template instanceof QuizTemplate) {
            return new QuizGameInteractiveBuilder(creatorName, (QuizTemplate) template);
        }
        else {
            return null;
        }
    }
}