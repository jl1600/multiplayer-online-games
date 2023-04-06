package system.use_cases.editors;

import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.entities.template.QuizTemplate;
import system.entities.template.Template;

/**
 * QuizTemplateEditor Class
 */
public class QuizTemplateEditor extends TemplateEditor {

    QuizTemplate template;

    /**
     * Constructor of QuizTemplateEditor
     * @param template the template to be edited
     */
    public QuizTemplateEditor(QuizTemplate template) {
        this.template = new QuizTemplate(template);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Template getTemplate() {
        return this.template;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void editAttribute(String attributeName, String value)
            throws InvalidInputException {
        switch (attributeName)
        {
            case "title":   // String has to match the actual instance variable name.
                editTitle(value);
                break;
            case "multipleChoice":
                editIsMultipleChoice(value);
                break;
            case "chooseAllThatApply":
                editIsChooseAllThatApply(value);
                break;
            case "hasMultipleScoreCategories":
                editHasMultipleScoreCategories(value);
                break;
            case "hasCustomEndingMessage":
                editHasCustomEndingMessage(value);
                break;
            case "hasScoreWeight":
                break;
            default:
                throw new InvalidInputException();
        }
    }

    private void editHasCustomEndingMessage(String value) throws InvalidInputException {
        if (!value.equals("true") && !value.equals("false"))
            throw new InvalidInputException();
        template.setHasCustomEndingMessage(Boolean.parseBoolean(value));
    }

    private void editHasMultipleScoreCategories(String value) throws InvalidInputException {
        if (!value.equals("true") && !value.equals("false"))
            throw new InvalidInputException();
        template.setHasMultipleScoreCategories(Boolean.parseBoolean(value));
    }

    private void editIsChooseAllThatApply(String value) throws InvalidInputException {
        if (!value.equals("true") && !value.equals("false"))
            throw new InvalidInputException();
        template.setChooseAllThatApply(Boolean.parseBoolean(value));
    }

    private void editTitle(String value) {
        template.setTitle(value);
    }

    private void editIsMultipleChoice(String value) throws InvalidInputException {
        if (!value.equals("true") && !value.equals("false"))
            throw new InvalidInputException();
        template.setMultipleChoice(Boolean.parseBoolean(value));
    }
}
