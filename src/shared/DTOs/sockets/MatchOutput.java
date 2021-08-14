package shared.DTOs.sockets;

import shared.constants.MatchStatus;

import java.util.Map;

public class MatchOutput {
    public MatchStatus status;
    public String textContent;
    public int numPlayers;
    // A mapping of username to game-specific stats
    public Map<String, String> playerStats;
}
