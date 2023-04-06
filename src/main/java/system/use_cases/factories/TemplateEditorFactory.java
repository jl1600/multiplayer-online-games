package system.use_cases.factories;

import system.entities.template.HangmanTemplate;
import system.entities.template.QuizTemplate;
import system.entities.template.Template;
import system.use_cases.editors.HangmanTemplateEditor;
import system.use_cases.editors.QuizTemplateEditor;
import system.use_cases.editors.TemplateEditor;

/**
 * TemplateEditorFactory Class
 */
public class TemplateEditorFactory {
    /**
     * choose which template editor to get based on parameters
     * @param template the template that will be edited
     * @return appropriate editor based on what kind of template the parameter is
     */
    public TemplateEditor getTemplateEditor(Template template) {
        if (template instanceof QuizTemplate) {
            return new QuizTemplateEditor((QuizTemplate) template);
        } else if (template instanceof HangmanTemplate) {
            return new HangmanTemplateEditor((HangmanTemplate) template);
        } else {
            return null;
        }
    }
}
