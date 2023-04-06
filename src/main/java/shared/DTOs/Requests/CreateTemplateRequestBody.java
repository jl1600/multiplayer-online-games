package shared.DTOs.Requests;

import shared.constants.GameGenre;

import java.util.Map;
/**
 * CreateTemplateRequestBody Class
 * Content required to perform create template request
 */
public class CreateTemplateRequestBody {
    /**
     * the desired attributes values of the desired template
     */
    public Map<String, String> attrMap;
    /**
     * the genre of the game
     */
    public GameGenre genre;
}
