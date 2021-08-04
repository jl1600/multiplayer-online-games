package system.gateways;

import shared.exceptions.entities_exception.IDAlreadySetException;
import shared.exceptions.entities_exception.UnknownGameTypeException;
import shared.exceptions.use_case_exceptions.InvalidGameIDException;
import shared.exceptions.use_case_exceptions.InvalidIDException;
import shared.exceptions.use_case_exceptions.WrongGameTypeException;
import system.entities.game.Game;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public interface GameDataGateway {
    String path = System.getProperty("user.dir");
    String gameFolderPath = path + "/src/system/database/games/";
    File gameCountFile = new File(path + "/src/system/database/countFiles/game.txt");

    /**
     * Adds the input Game object to the database.
     *
     * @param game the Game object to save.
     * @throws IOException            if there is a problem writing to the file.
     */
    void addGame(Game game) throws IOException, UnknownGameTypeException;

    /**
     * Updates the input Game object in the database.
     *
     * @param game the Game object to update.
     * @throws InvalidIDException     if the game's ID is not found in the database.
     * @throws IOException            if there is a problem writing to the file.
     * @throws WrongGameTypeException if the input Game is of the wrong GameGenre.
     */
    void updateGame(Game game) throws InvalidGameIDException, IOException, UnknownGameTypeException;

    /**
     * Deletes the input Game from the database.
     *
     * @param game the Game object to delete.
     * @throws IOException if there is a problem deleting the file.
     * @throws UnknownGameTypeException if the input Game is not of a recognized type.
     * @throws InvalidIDException if the input Game does not exist in the database.
     */
    void deleteGame(Game game) throws IOException, UnknownGameTypeException, InvalidGameIDException;

    /**
     * Returns a set of all Games in the database
     *
     * @return a set of all Games in the database
     * @throws IOException           if there is a problem reading the file.
     * @throws IDAlreadySetException
     */
    Set<Game> getAllGames() throws IOException, IDAlreadySetException;


    /**
     * Get the count of Game objects stored in the database
     *
     * @return the count of stores Games.
     * @throws IOException if there is a problem reading from the database.
     */
    int getGameCount() throws IOException;
}
