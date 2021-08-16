package shared.DTOs.Requests;

import shared.constants.GameGenre;

import java.util.Map;

public class CreateTemplateRequestBody {
    public Map<String, String> attrMap;
    public GameGenre genre;
}
