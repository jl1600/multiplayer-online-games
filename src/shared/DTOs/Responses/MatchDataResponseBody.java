package shared.DTOs.Responses;

import shared.constants.GameGenre;

public class MatchDataResponseBody {
    public String gameTitle;
    public String matchId;
    public String hostName;
    public GameGenre genre;
    public int numPlayers;
    public int maxPlayers;
}
