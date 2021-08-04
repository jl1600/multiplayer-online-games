package shared.request.game_request;

/**
 * JoinGameMatchRequest Class
 */
public class JoinGameMatchRequest extends GameRequest {
    private final String designChoice;

    /**
     * JoinGameMatchRequest Constructor
     * @param sessionID of the session
     * @param senderID of the sender
     * @param designChoice of the match
     */
    public JoinGameMatchRequest(String sessionID, String senderID, String designChoice) {
        super(sessionID, senderID);
        this.designChoice = designChoice;
    }

    /**
     *
     * @return the designChoice
     */
    public String getDesignChoice() {
        return designChoice;
    }
}
