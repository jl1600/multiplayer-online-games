package shared.DTOs.sockets;

import shared.constants.MatchStatus;

import java.util.Map;

/**
 * MatchOutput Class
 */
public class MatchOutput {
    /**
     * the current match status
     */
    public MatchStatus status;
    /**
     * the updated text content
     */
    public String textContent;
    /**
     * the current number of players
     */
    public int numPlayers;
    /**
     * A mapping of username to game specific stats
     */
    public Map<String, String> playerStats;
}
