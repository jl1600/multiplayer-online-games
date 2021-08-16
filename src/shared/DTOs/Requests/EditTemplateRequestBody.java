package shared.DTOs.Requests;

import java.util.Map;

/**
 * EditTemplateRequestBody Class
 * the content used to perform edit template request
 */
public class EditTemplateRequestBody {
    /**
     * the desired template id
     */
    public String templateID;
    /**
     * the new attribute values
     */
    public Map<String, String> attrMap;
}
