package system.use_cases.managers;

import shared.constants.OnlineStatus;
import shared.constants.UserRole;
import shared.exceptions.entities_exception.IDAlreadySetException;
import shared.exceptions.entities_exception.UnaccountedUserRoleException;
import shared.exceptions.use_case_exceptions.*;
import system.entities.User;
import system.gateways.UserDataGateway;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class UserManager {
    private final HashMap<String, User> users;     // userId to User entity
    private final IdManager idManager;
    private final HashMap<String, String> userIds; // username to userId
    private final UserDataGateway gateway;

    public UserManager(UserDataGateway gateway) throws IOException {
        users = new HashMap<>();
        userIds = new HashMap<>();
        this.gateway = gateway;

         for (User user: this.gateway.getAllUsers()) {
            String userId = user.getUserId();
            users.put(userId, user);
            userIds.put(user.getUsername(), userId);
         }
        idManager = new IdManager(gateway.getUserCount() + 1);
    }

    private String getUserId(String username) throws InvalidUsernameException {
        if (!userIds.containsKey(username))
            throw new InvalidUsernameException();

        return userIds.get(username);
    }

    private boolean isPasswordIncorrect(String userId, String password) throws InvalidUserIDException {
        if (!users.containsKey(userId)) throw new InvalidUserIDException();

        return !getUser(userId).isPassword(password);
    }

    /**
     * @param userId the id of the user to retrieve
     * @return the user with the specified id
     * @throws InvalidUserIDException if no user has the specified id
     */
    public User getUser(String userId) throws InvalidUserIDException {
        if (!users.containsKey(userId))
            throw new InvalidUserIDException();

        return users.get(userId);
    }

    /**
     * @param userId the id of the user
     * @return the role of the user with the specified id
     * @throws InvalidUserIDException if no user has the specified id
     */
    public UserRole getUserRole(String userId) throws InvalidUserIDException {
        if (!users.containsKey(userId))
            throw new InvalidUserIDException();

        return users.get(userId).getRole();
    }

    /**
     * Creates a user entity with the specified username, password, user role, and assigns
     * them a unique id. The username must not be taken already.
     * The user is stored within this class and in the database
     * @param username username of the user to create
     * @param password password of the user to create
     * @param role role of the user to create. Must be one of ADMIN and MEMBER.
     *             Trial users should be created using this.createTrialUser
     * @throws DuplicateUsernameException if the username is already taken
     * @throws IOException if the database is not found
     */
    public void createUser(String username, String password, UserRole role)
            throws DuplicateUsernameException, IOException, UnaccountedUserRoleException {
        if (role.equals(UserRole.TRIAL))
            throw new UnaccountedUserRoleException();
        if (users.containsKey(username))
            throw new DuplicateUsernameException();

        String userId = idManager.getNextId();
        User user = new User(userId, username, password, role);
        userIds.put(username, userId);
        users.put(userId, user);
        gateway.addUser(user);
    }

    /**
     * Creates a trial user and assigns them a unique id.
     * The user is stored within the class but not in the database
     * @return id of the trial user created
     */
    public String createTrialUser() {
        String userId = idManager.getNextId();
        String username = "TrialUser" + userId;

        User user = new User(userId, username, null, UserRole.TRIAL);

        userIds.put(username, userId);
        users.put(userId, user);

        return userId;
    }

    /**
     * Logs in the user with the specified username if the specified password matches the user's password
     * @param username username of the user to log in
     * @param password password of the user to log in
     * @return id of the user logged in
     * @throws InvalidUsernameException if no user has the specified username
     * @throws IncorrectPasswordException if the specified password does not match the user's password
     */
    public String login(String username, String password)
            throws InvalidUsernameException, IncorrectPasswordException, InvalidUserIDException {
        if (!userIds.containsKey(username))
            throw new InvalidUsernameException();

        String userId = getUserId(username);
        if (isPasswordIncorrect(userId, password)) throw new IncorrectPasswordException();

        getUser(userId).setOnlineStatus(OnlineStatus.ONLINE);
        return userId;
    }

    /**
     * Logs out the user with the specified id
     * @param userId id of the user to log out
     * @throws InvalidUserIDException if no user has the specified userId
     */
    public void logout(String userId) throws InvalidUserIDException {
        if (!users.containsKey(userId))
            throw new InvalidUserIDException();
        getUser(userId).setOnlineStatus(OnlineStatus.OFFLINE);
    }

    /**
     * Changes the password of the user with the specified id.
     * This changes the data within this class and in the database
     * @param userId id of the user
     * @param oldPassword current password of the user to confirm the action
     * @param newPassword the password to change into
     * @throws InvalidUserIDException if no user has the specified userId
     * @throws IncorrectPasswordException if oldPassword does not match the user's current password
     * @throws IOException if the database is not found
     */
    public void editPassword(String userId, String oldPassword, String newPassword) throws
            IncorrectPasswordException, IOException, InvalidUserIDException {
        if (isPasswordIncorrect(userId, oldPassword)) throw new IncorrectPasswordException();

        User user = getUser(userId);
        user.setPassword(newPassword);

        gateway.updateUser(user);
    }

    /**
     * Changes the username of the user with the specified id.
     * The username must not be taken already
     * This changes the data within this class and in the database
     * @param userId id of the user
     * @param newUsername the username to change into
     * @throws IDAlreadySetException if the username is already taken
     * @throws InvalidUserIDException if no user has the specified userId
     * @throws IOException if the database is not found
     */
    public void editUsername(String userId, String newUsername) throws
            IDAlreadySetException, IOException, InvalidUserIDException {
        if (!users.containsKey(userId))
            throw new InvalidUserIDException();
        if (users.containsKey(newUsername))
            throw new IDAlreadySetException();

        User user = getUser(userId);
        userIds.remove(user.getUsername());
        userIds.put(newUsername, userId);
        user.setUsername(newUsername);

        gateway.updateUser(user);
    }

    /**
     * Changes a trial user to a normal user with the specified username and password
     * and keeps their game creations.
     * The username must not be taken already.
     * This stores the created user in the database
     * @param userId id of the trial user
     * @param username username of the user to create
     * @param password password of the user to create
     * @throws InvalidUserIDException if no user has the specified userId
     * @throws DuplicateUsernameException if the username is already taken
     * @throws IOException if the database is not found
     * @throws UnaccountedUserRoleException if the user specified user is not a trial user
     */
    public void promoteTrialUser(String userId, String username, String password) throws
            InvalidUserIDException, DuplicateUsernameException, IOException, UnaccountedUserRoleException {
        if (userIds.containsKey(userId))
            throw new InvalidUserIDException();

        User user = getUser(userId);
        if (user.getRole() != UserRole.TRIAL)
            throw new UnaccountedUserRoleException();

        user.setUsername(username);
        user.setPassword(password);
        user.trialToNormal();
        gateway.addUser(user);
    }

    /**
     * Delete the user with the specified id from this class and the database
     * @param userId id of the user to delete
     * @param password password of the user to delete to confirm the action
     * @throws InvalidUserIDException if no user has the specified userId
     * @throws IncorrectPasswordException if the specified password does not match the user's password
     * @throws IOException if the database is not found
     */
    public void deleteUser(String userId, String password)
            throws IncorrectPasswordException, IOException, InvalidUserIDException {

        //not trial check password, trial just delete
        if (users.get(userId).getRole().equals(UserRole.TRIAL)){
            String username = users.get(userId).getUsername();
            users.remove(userId);
            userIds.remove(username);

        } else {
            if (isPasswordIncorrect(userId, password)) {
                throw new IncorrectPasswordException();
            }

            String username = users.get(userId).getUsername();
            users.remove(userId);
            userIds.remove(username);
            gateway.deleteUser(userId);
        }


    }

    /**
     * Add a new game ID to the set of games created by this user.
     * @param userID The ID of the user.
     * @param gameID The Id of the game.
     * */
    public void addOwnedGameID(String userID, String gameID) throws InvalidUserIDException, IOException {
        if (!users.containsKey(userID))
            throw new InvalidUserIDException();

        User user = users.get(userID);
        user.addGameID(gameID);

        if (user.getRole() != UserRole.TRIAL) {
            gateway.updateUser(user);
        }
    }

    public void removeOwnedGameID(String userID, String gameID) throws InvalidUserIDException, IOException {
        if (!users.containsKey(userID))
            throw new InvalidUserIDException();

        User user = users.get(userID);
        user.removeGameID(gameID);

        if (user.getRole() != UserRole.TRIAL) {
            gateway.updateUser(user);
        }
    }

    /**
     * Returns the set of all game IDs created by this user.
     * @param userID The ID of the user.
     * */
    public Set<String> getOwnedGamesID(String userID) throws InvalidUserIDException {
        if (!users.containsKey(userID))
            throw new InvalidUserIDException();

        return users.get(userID).getGameCreationSet();
    }

    /**
     * @return all users entity within this class
     */
    public ArrayList<User> getAllUsers() {
        return (ArrayList<User>) users.values();
    }
}