package shared.request;

import shared.DataPackage;

/**
 * Request Class
 */
public abstract class Request extends DataPackage {
    /**
     * Request Constructor
     * @param sessionID of the session
     */
    public Request(String sessionID) {
        super(sessionID);
    }
}
