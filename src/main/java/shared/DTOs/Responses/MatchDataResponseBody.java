package shared.DTOs.Responses;

import shared.constants.GameGenre;

/**
 *  MatchDataResponseBody Class
 */
public class MatchDataResponseBody {
    /**
     * the title of the game this match is using
     */
    public String gameTitle;
    /**
     * the matchID of this match
     */
    public String matchId;
    /**
     * the host (also the creator of this match)'s username
     */
    public String hostName;
    /**
     * the genre of the game this match is based upon
     */
    public GameGenre genre;
    /**
     * the current number of players in this match
     */
    public int numPlayers;
    /**
     * the max number of players for this match
     */
    public int maxPlayers;
}
