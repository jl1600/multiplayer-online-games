package system.use_cases.builders.interactive_builders;

import shared.exceptions.use_case_exceptions.InsufficientInputException;
import system.entities.template.Template;
import system.use_cases.builders.interactive_builders.InteractiveBuilder;

public abstract class TemplateInteractiveBuilder extends InteractiveBuilder {

    /**
     * Return the Template object that this builder has been building.
     * */
    public abstract Template build(String id) throws InsufficientInputException;
}
