package system.gateways;

import shared.exceptions.entities_exception.UnaccountedEnumException;
import shared.exceptions.use_case_exceptions.InvalidIDException;
import system.entities.game.Game;

import java.io.IOException;
import java.util.Set;

/**
 * GameDataGateway Interface
 */
public interface GameDataGateway {

    /**
     * Adds the input game to the database and increments the game count by 1.
     *
     * @param game the game to add
     * @throws IOException if there is a problem saving to the database
     */
    void addGame(Game game) throws IOException, UnaccountedEnumException;

    /**
     * Updates the input game in the database.
     *
     * @param game the game to update
     * @throws IOException if there is a problem reading or writing to the database
     */
    void updateGame(Game game) throws InvalidIDException, IOException, UnaccountedEnumException;

    /**
     * Deletes the input Game from the database.
     *
     * @param gameID the gameId of the object to delete
     * @throws IOException              if there is a problem deleting the file
     * @throws InvalidIDException   if the input Game does not exist in the database
     */
    void deleteGame(String gameID) throws IOException, InvalidIDException;

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
