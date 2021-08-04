package shared.response.misc;

import shared.response.Response;

/**
 * ErrorMessageResponse Class
 */
public class ErrorMessageResponse extends Response {
    private final String message;

    /**
     * ErrorMessageResponse Constructor
     * @param sessionID of the session
     * @param message of the response
     */
    public ErrorMessageResponse(String sessionID, String message) {
        super(sessionID);
        this.message = message;
    }

    /**
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return message;
    }

    /**
     *
     * @return the message
     */
    public String toString() {
        return message;
    }
}
