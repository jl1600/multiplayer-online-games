package system.use_cases.editors;

import com.sun.istack.internal.NotNull;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import shared.exceptions.use_case_exceptions.NoSuchAttributeException;
import system.entities.template.Template;

import java.util.Map;

public abstract class TemplateEditor {

    /**
     * Returns the template that this editor is holding.
     * */
    public abstract Template getTemplate();

    /**
     * Change the attribute of the current template to the specified value.
     *
     * @param attributeName The name of the attribute
     * @param value The string representation of the value
     * */
    public abstract void editAttribute(@NotNull String attributeName, @NotNull String value)
            throws NoSuchAttributeException, InvalidInputException;

    /**
     * Returns a mapping of attribute names to string representations of attribute values.
     * */
    public abstract Map<String, String> getAttributeMap();
}
