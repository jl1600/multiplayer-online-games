package shared.response;

import shared.DataPackage;

/**
 * Response Class
 */
public abstract class Response extends DataPackage {
    /**
     * Response Constructor
     * @param sessionID of the session
     */
    public Response(String sessionID)
    {
        super(sessionID);
    }
}