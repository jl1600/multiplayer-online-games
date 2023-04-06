package shared.constants;

/**
 * GameAccessLevel Constants
 */
public enum GameAccessLevel {
    /**
     * public game accessible by everyone
     */
    PUBLIC,
    /**
     * private game accessible by creator and admin
     */
    PRIVATE,
    /**
     * private game accessible by creator and friend and admin
     */
    FRIEND,
    /**
     * deleted game accessible by admin
     */
    DELETED,
}
