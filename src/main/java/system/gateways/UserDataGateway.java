package system.gateways;

import shared.exceptions.use_case_exceptions.InvalidIDException;
import system.entities.User;

import java.io.*;
import java.util.Set;

/**
 * UserDataGateway Interface
 */
public interface UserDataGateway {

    /**
     * Adds the input user to the database and increments the user count by 1.
     *
     * @param user the user to add
     * @throws IOException if there is a problem saving to the database
     */
    void addUser(User user) throws IOException;

    /**
     * Updates the input user in the database.
     *
     * @param user user to update.
     * @throws IOException            if the database is not found
     * @throws InvalidIDException if the user does not exist
     */
    void updateUser(User user) throws IOException, InvalidIDException;

    /**
     * Deletes the user with the specified userId from the database.
     *
     * @param userId userId of the user to delete
     * @throws IOException if there is a problem deleting the file
     */
    void deleteUser(String userId) throws IOException;

    /**
     * Returns a set of all users in the database.
     *
     * @return a set of all users in the database
     * @throws IOException if there is a problem reading from the database
     */
    Set<User> getAllUsers() throws IOException;

    /**
     * Returns the total number of users ever created by the program.
     * <p>
     * This number does not decrease when a user is deleted.
     *
     * @return the total number of users ever created
     * @throws IOException if there is a problem reading from the database
     */
    int getUserCount() throws IOException;

    /**
     * Increases the number of users created by 1
     * @throws IOException if there is a problem reading from the database
     */
    void incrementUserCount() throws IOException;
}
