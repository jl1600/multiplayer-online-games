package system.gateways;

import shared.exceptions.entities_exception.UnknownGameTypeException;
import shared.exceptions.use_case_exceptions.InvalidGameIDException;
import system.entities.game.Game;

import java.io.IOException;
import java.util.Set;

public interface GameDataGateway {

    /**
     * Adds the input game to the database and increments the game count by 1.
     *
     * @param game the game to add
     * @throws IOException if there is a problem saving to the database
     */
    void addGame(Game game) throws IOException, UnknownGameTypeException;

    /**
     * Updates the input game in the database.
     *
     * @param game the game to update
     * @throws IOException if there is a problem reading or writing to the database
     */
    void updateGame(Game game) throws InvalidGameIDException, IOException, UnknownGameTypeException;

    /**
     * Deletes the input Game from the database.
     *
     * @param gameID the gameId of the object to delete
     * @throws IOException              if there is a problem deleting the file
     * @throws InvalidGameIDException   if the input Game does not exist in the database
     */
    void deleteGame(String gameID) throws IOException, InvalidGameIDException;

    /**
     * Returns a set of all Games in the database
     *
     * @return a set of all Games in the database
     * @throws IOException if there is a problem reading from the database
     */
    Set<Game> getAllGames() throws IOException;


    /**
     * Get the count of Game objects ever created by the program.
     * <p>
     * This number does not decrease when a game is deleted.
     *
     * @return the count of stored games
     * @throws IOException if there is a problem reading from the database
     */
    int getGameCount() throws IOException;
}
