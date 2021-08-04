package shared.request.game_request;

/**
 * MakeGameDesignChoiceRequest Class
 */
public class MakeGameDesignChoiceRequest extends GameRequest {

    private final String userInput;

    /**
     * MakeGameDesignChoiceRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     * @param userInput of the user
     */
    public MakeGameDesignChoiceRequest(String sessionID, String senderID, String userInput) {
        super(sessionID, senderID);
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
