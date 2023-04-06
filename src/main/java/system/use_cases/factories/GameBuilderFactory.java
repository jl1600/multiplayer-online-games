package system.use_cases.factories;

import system.entities.template.HangmanTemplate;
import system.entities.template.Template;
import system.entities.template.QuizTemplate;
import system.use_cases.builders.GameInteractiveBuilder;
import system.use_cases.builders.HangmanGameInteractiveBuilder;
import system.use_cases.builders.QuizGameInteractiveBuilder;

/**
 * GameBuilderFactory Class
 */
public class GameBuilderFactory {
    /**
     * choose which game builder to get based on parameters
     * @param creatorName the name of the creator
     * @param template the template to be used
     * @return appropriate game builder
     */
    public GameInteractiveBuilder getGameBuilder(String creatorName, Template template) {
        if (template instanceof QuizTemplate) {
            return new QuizGameInteractiveBuilder(creatorName, (QuizTemplate) template);
        } else if (template instanceof HangmanTemplate) {
            return new HangmanGameInteractiveBuilder(creatorName,(HangmanTemplate) template);
        } else {
            return null;
        }
    }
}