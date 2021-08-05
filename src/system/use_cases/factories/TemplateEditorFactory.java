package system.use_cases.factories;

import system.entities.template.QuizTemplate;
import system.entities.template.Template;
import system.use_cases.editors.QuizTemplateEditor;
import system.use_cases.editors.TemplateEditor;

public class TemplateEditorFactory {
    public TemplateEditor getTemplateEditor(Template template) {
        if (template instanceof QuizTemplate) {
            return new QuizTemplateEditor((QuizTemplate) template);
        }
        else {
            return null;
        }
    }
}