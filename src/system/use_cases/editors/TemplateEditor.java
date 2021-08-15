package system.use_cases.editors;

import com.google.gson.Gson;
import com.sun.istack.internal.NotNull;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import shared.exceptions.use_case_exceptions.NoSuchAttributeException;
import system.entities.template.Template;

import java.util.HashMap;
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
    public Map<String, String> getAttributeMap() {
        Gson gson = new Gson();
        String json = gson.toJson(getTemplate());
        Map<String, String> attrs = gson.fromJson(json, HashMap.class);
        attrs.remove("id");
        return attrs;
    }
}
