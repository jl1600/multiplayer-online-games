package system.use_cases.editors;

import com.sun.istack.internal.NotNull;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import shared.exceptions.use_case_exceptions.NoSuchAttributeException;
import system.entities.template.QuizTemplate;
import system.entities.template.Template;

import java.util.HashMap;
import java.util.Map;


public class QuizTemplateEditor extends TemplateEditor {

    QuizTemplate template;

    public QuizTemplateEditor(QuizTemplate template) {
        this.template = new QuizTemplate(template);
    }

    @Override
    public Template getTemplate() {
        return this.template;
    }

    @Override
    public void editAttribute(@NotNull String attributeName,@NotNull String value)
            throws InvalidInputException, NoSuchAttributeException {
        switch (attributeName)
        {
            case "Title":
                editTitle(value);
                break;
            case "Is multiple choice":
                editIsMultipleChoice(value);
                break;
            case "Is choose all that apply":
                editIsChooseAllThatApply(value);
                break;
            case "Has multiple score categories":
                editHasMultipleScoreCategories(value);
                break;
            case "Each category has a custom ending message":
                editHasCustomEndingMessage(value);
                break;
            default:
                throw new NoSuchAttributeException();
        }
    }

    private void editHasCustomEndingMessage(@NotNull String value) throws InvalidInputException {
        if (!value.equals("true") && !value.equals("false"))
            throw new InvalidInputException();
        template.setHasCustomEndingMessage(Boolean.parseBoolean(value));
    }

    private void editHasMultipleScoreCategories(@NotNull String value) throws InvalidInputException {
        if (!value.equals("true") && !value.equals("false"))
            throw new InvalidInputException();
        template.setHasMultipleScoreCategories(Boolean.parseBoolean(value));
    }

    private void editIsChooseAllThatApply(@NotNull  String value) throws InvalidInputException {
        if (!value.equals("true") && !value.equals("false"))
            throw new InvalidInputException();
        template.setChooseAllThatApply(Boolean.parseBoolean(value));
    }

    private void editTitle(@NotNull String value) {
        template.setTitle(value);
    }

    private void editIsMultipleChoice(@NotNull String value) throws InvalidInputException {
        if (!value.equals("true") && !value.equals("false"))
            throw new InvalidInputException();
        template.setMultipleChoice(Boolean.parseBoolean(value));
    }


    @Override
    public Map<String, String> getAttributeMap() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("Title", template.getTitle());
        attributes.put("Is multiple choice", Boolean.toString(template.isMultipleChoice()));
        attributes.put("Is choose all that apply", Boolean.toString(template.isChooseAllThatApply()));
        attributes.put("Has multiple score categories", Boolean.toString(template.hasMultipleScoreCategories()));
        attributes.put("Each category has a custom ending message", Boolean.toString(template.hasCustomEndingMessage()));
        return attributes;
    }
}
