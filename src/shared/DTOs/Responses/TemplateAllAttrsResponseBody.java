package shared.DTOs.Responses;

import java.util.Map;

/**
 * TemplateAllAttrsResponseBody
 */
public class TemplateAllAttrsResponseBody {
    /**
     * the id of the template
     */
    public String templateID;
    /**
     * the template attribute values
     */
    public Map<String, String> attrMap;
}
