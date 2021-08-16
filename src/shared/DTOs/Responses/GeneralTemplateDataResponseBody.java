package shared.DTOs.Responses;

import shared.constants.GameGenre;

/**
 * GeneralTemplateDataResponseBody
 */
public class GeneralTemplateDataResponseBody {
    /**
     * the id of the template
     */
    public String templateID;
    /**
     * the title of the template
     */
    public String title;
    /**
     * the game genre of the template
     */
    public GameGenre gameGenre;
}
