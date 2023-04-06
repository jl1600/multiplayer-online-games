package system.use_cases.editors;

import com.google.gson.Gson;
import com.sun.istack.internal.NotNull;
import shared.exceptions.use_case_exceptions.InvalidInputException;
import system.entities.template.Template;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Abstract TemplateEditor Class
 */
public abstract class TemplateEditor {

    /**
     * @return the template that this editor is holding.
     */
    public abstract Template getTemplate();

    /**
     * Change the attribute of the current template to the specified value.
     * @param attributeName The name of the attribute
     * @param value The string representation of the value
     * @throws InvalidInputException when parameters are illegal and passed a null value
     * */
    public abstract void editAttribute(@NotNull String attributeName, @NotNull String value)
            throws InvalidInputException;

    /**
     * @return a mapping of attribute names to string representations of attribute values.
     * */
    public Map<String, String> getAttributeMap() {
        Gson gson = new Gson();
        String json = gson.toJson(getTemplate());
        Type type = new TypeToken <Map<String, String>>(){}.getType();
        Map<String, String> attrs = gson.fromJson(json, type);
        attrs.remove("id");
        return attrs;
    }
}
