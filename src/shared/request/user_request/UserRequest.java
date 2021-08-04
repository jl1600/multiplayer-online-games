package shared.request.user_request;

import shared.request.Request;

/**
 * UserRequest Class
 */
public abstract class UserRequest extends Request {
    /**
     * UserRequest Constructor
     * @param sessionID of the session
     */
    public UserRequest(String sessionID) {
        super(sessionID);
    }
}
