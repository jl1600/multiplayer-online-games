package shared.request.template_request;

/**
 * MakeTemplateDesignChoiceRequest Class
 */
public class MakeTemplateDesignChoiceRequest extends TemplateRequest {

    private final String userInput;

    /**
     * MakeTemplateDesignChoiceRequest Constructor
     * @param sessionID of the session
     * @param sender of the sender
     * @param userInput of the user
     */
    public MakeTemplateDesignChoiceRequest(String sessionID, String sender, String userInput) {
        super(sessionID, sender);
        this.userInput = userInput;
    }

    /**
     *
     * @return the userInput
     */
    public String getUserInput() {
        return userInput;
    }
}
