package shared;

/**
 * DataPackage Class
 */
public abstract class DataPackage {
    protected final String sessionID;

    /**
     * DataPackage Constructor
     * @param sessionID of the session
     */
    public DataPackage(String sessionID) {
        this.sessionID = sessionID;
    }

    /**
     *
     * @return the sessionID
     */
    public String getSessionID () {
        return sessionID;
    }
}
