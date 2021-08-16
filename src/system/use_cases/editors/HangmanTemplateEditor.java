package system.use_cases.editors;


import shared.exceptions.use_case_exceptions.InvalidInputException;
import shared.exceptions.use_case_exceptions.NoSuchAttributeException;
import system.entities.template.HangmanTemplate;
import system.entities.template.Template;


public class HangmanTemplateEditor extends TemplateEditor {

    HangmanTemplate template;

    public HangmanTemplateEditor(HangmanTemplate template) {
        this.template = new HangmanTemplate(template);
    }

    @Override
    public Template getTemplate() {
        return this.template;
    }

    @Override
    public void editAttribute(String attributeName, String value) throws NoSuchAttributeException, InvalidInputException {
        switch (attributeName) {
            case "title": // String has to match the actual instance variable name.
                editTitle(value);
                break;
            case "hasHints":
                editHasHints(value);
                break;
            default:
                throw new NoSuchAttributeException();
        }

    }

    private void editHasHints(String value) throws InvalidInputException {
        if (!value.equals("true") && !value.equals("false"))
            throw new InvalidInputException();
        template.setHasHints(Boolean.parseBoolean(value));
    }

    private void editTitle(String value) {
        template.setTitle(value);
    }

}


