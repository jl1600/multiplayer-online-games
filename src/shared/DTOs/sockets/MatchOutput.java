package shared.DTOs.sockets;

import shared.constants.MatchStatus;

import java.util.Map;

public class MatchOutput {
    public MatchStatus status;
    public String textContent;
    // A mapping of player ID to player moves
    public Map<String, String> lastTurnMoves;
}
