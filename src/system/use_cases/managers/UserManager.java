package system.use_cases.managers;

import java.util.*;

import shared.constants.IDType;
import shared.constants.OnlineStatus;
import shared.constants.UserRole;
import shared.exceptions.entities_exception.IDAlreadySetException;
import shared.exceptions.entities_exception.UnaccountedEnumException;
import shared.exceptions.use_case_exceptions.*;
import system.entities.User;
import system.gateways.UserDataGateway;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserManager {
    private final Map<String, User> users;     // userId to User entity
    private final Map<String, String> userIds; // username to userId
    private final Map<String, Map<String, Date>> tempPasswords; //  userId to temporary passwords

    private final IdManager idManager;
    private final UserDataGateway gateway;

    public UserManager(UserDataGateway gateway) throws IOException, InvalidIDException {
        users = new HashMap<>();
        userIds = new HashMap<>();
        this.gateway = gateway;
        tempPasswords = new HashMap<>();

        Date currentTime = Calendar.getInstance().getTime();

         for (User user: this.gateway.getAllUsers()) {
            String userId = user.getUserId();
            //unban the user who have gone over their last ban date
            if (user.getOnlineStatus().equals(OnlineStatus.BANNED)){
                if (user.getLastBanDate().before(currentTime)){//current time has gone over last ban date
                    user.setOnlineStatus(OnlineStatus.OFFLINE);
                    this.gateway.updateUser(user);
                }
            }

            users.put(userId, user);
            userIds.put(user.getUsername(), userId);
         }

        idManager = new IdManager(gateway.getUserCount() + 1);
    }

    public String getUserId(String username) throws InvalidUsernameException {
        if (!userIds.containsKey(username))
            throw new InvalidUsernameException();

        return userIds.get(username);
    }

    public String getEmail(String userID) throws InvalidIDException {
        if (!users.containsKey(userID))
            throw new InvalidIDException(IDType.USER);

        return getUser(userID).getEmail();
    }

    private boolean isPasswordIncorrect(String userId, String password) throws InvalidIDException {
        if (!users.containsKey(userId)) throw new InvalidIDException(IDType.USER);

        return !getUser(userId).isPassword(password);
    }

    /**
     * @param userId the id of the user to retrieve
     * @return the user with the specified id
     * @throws InvalidIDException if no user has the specified id
     */
    public User getUser(String userId) throws InvalidIDException {
        if (!users.containsKey(userId))
            throw new InvalidIDException(IDType.USER);

        return users.get(userId);
    }

    /**
     * @param userId the id of the user
     * @return the role of the user with the specified id
     * @throws InvalidIDException if no user has the specified id
     */
    public UserRole getUserRole(String userId) throws InvalidIDException {
        if (!users.containsKey(userId))
            throw new InvalidIDException(IDType.USER);

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
     */
    public void createUser(String username, String password, String email, UserRole role)
            throws DuplicateUsernameException, UnaccountedEnumException, WeakPasswordException, InvalidEmailException {
        if (role.equals(UserRole.TRIAL))
            throw new UnaccountedEnumException();
        if (userIds.containsKey(username))
            throw new DuplicateUsernameException();
        if (!isPasswordString(password))
            throw new WeakPasswordException();
        if (!isValidEmail(email))
            throw new InvalidEmailException();

        String userId = idManager.getNextId();
        Date currentTime = Calendar.getInstance().getTime();

        User user = new User(userId, username, password, email, role, currentTime);
        userIds.put(username, userId);
        users.put(userId, user);
        System.out.println("Trying to add the user to gateway");
        try {
            gateway.addUser(user);
        } catch (IOException e) {
            throw new RuntimeException("Fatal Error: Database malfunction.");
        }
    }

    /**
     * Create a temporary password that lasts 24 hours and returns it.
     *
     * @param username The username of the user who is requesting the temporary password.
     * @param email The email corresponding to the user.
     * @return The temporary password.
     *
     * @throws InvalidUsernameException When the username is invalid.
     * @throws InvalidEmailException When the email doesn't match.
     * */

    public String createTempPassword(String username, String email) throws InvalidUsernameException, InvalidEmailException {
        if (!userIds.containsKey(username))
            throw new InvalidUsernameException();

        String uid = userIds.get(username);

        if (!users.get(uid).getEmail().equals(email))
            throw new InvalidEmailException();

        String tempPassword = generateRandomPassword();
        Calendar expireDate = Calendar.getInstance();
        expireDate.add(Calendar.HOUR, 24);
        Map<String, Date> passAndDate = new HashMap<>();
        passAndDate.put(tempPassword, expireDate.getTime());
        tempPasswords.put(uid, passAndDate);
        return tempPassword;
    }

    public boolean isValidEmail(String email){
        // The following email regex pattern is inspired by https://stackoverflow.com/a/16625335/10254049
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$");
        final Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    private String generateRandomPassword(){
        String alphabetUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String alphabetLower = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChar = "$&+,:;=?@#|";
        StringBuilder randomPassword = new StringBuilder();
        for(int i = 0; i < 10; i++) {
            if (i == 0){
                int randomUpper = (int)(Math.random() * 26);
                randomPassword.append(alphabetUpper.charAt(randomUpper));
            } else if (i == 1){
                int randomLower = (int)(Math.random() * 26);
                randomPassword.append(alphabetLower.charAt(randomLower));
            } else if (i == 2) {
                int randomNumber = (int)(Math.random() * 9);
                randomPassword.append(numbers.charAt(randomNumber));
            } else if (i == 3) {
                int randomSpecial = (int)(Math.random() * 11);
                randomPassword.append(specialChar.charAt(randomSpecial));
            } else{
                int randomLower = (int)(Math.random() * 26);
                randomPassword.append(alphabetLower.charAt(randomLower));
            }
        }
        return randomPassword.toString();
    }

    private boolean isPasswordString(String password){
        boolean hasUpperChar = password.matches(".*[A-Z].*");
        boolean hasLowerChar = password.matches(".*[a-z].*");
        boolean hasNumbers = password.matches(".*\\d+.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_~?,.<>/;:].*");
        boolean isLong = password.length() >= 6;

        return hasUpperChar && hasLowerChar && hasNumbers && hasSpecialChar && isLong;
    }

    /**
     * Creates a trial user and assigns them a unique id.
     * The user is stored within the class but not in the database
     * @return id of the trial user created
     */
    public String createTrialUser() {
        String userId = idManager.getNextId();
        String username = "TrialUser" + userId;

        Date currentTime = Calendar.getInstance().getTime();

        User user = new User(userId, username, null, null, UserRole.TRIAL, currentTime);

        userIds.put(username, userId);
        users.put(userId, user);

        try {
            gateway.incrementUserCount();
        } catch (IOException e) {
            throw new RuntimeException("Fatal Error: Database malfunction.");
        }

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
            throws InvalidUsernameException, BannedUserException, IncorrectPasswordException,
             ExpiredUserException {

        if (!userIds.containsKey(username)) throw new InvalidUsernameException();
        String userId = getUserId(username);

        try {
            if (isPasswordIncorrect(userId, password) && isTempPasswordIncorrect(userId, password))
                throw new IncorrectPasswordException();
            if (isBanned(userId)) throw new BannedUserException();
            if (getUserRole(userId) == UserRole.TEMP && isExpiredUser(userId)) {
                throw new ExpiredUserException();
            }
            getUser(userId).setOnlineStatus(OnlineStatus.ONLINE);
        } catch (InvalidIDException e1) {
            throw new RuntimeException("System failure: The database ID associated with this user is invalid.");
        }
        return userId;
    }

    private boolean isTempPasswordIncorrect(String userId, String password) {
        if (!tempPasswords.containsKey(userId) || !tempPasswords.get(userId).containsKey(password))
            return true;
        else if (tempPasswords.get(userId).get(password).before(Calendar.getInstance().getTime())) {
            tempPasswords.remove(userId);
            return true;
        }
        return false;
    }

    private boolean isBanned(String userId) throws InvalidIDException {
        Date currentTime = Calendar.getInstance().getTime();

        if (getUser(userId).getOnlineStatus().equals(OnlineStatus.BANNED)){//if banned go in bracket
            //if the last ban date hasn't arrived yet
            //online status will be set in login()
            return getUser(userId).getLastBanDate().after(currentTime);
        }
        return false;
    }

    /**
     *
     * @throws InvalidIDException when the user is not banned or there is no such user.
     */
    public Date getBanLiftingDate(String userID) throws InvalidIDException {
        if (!users.containsKey(userID) || users.get(userID).getOnlineStatus() != OnlineStatus.BANNED)
            throw new InvalidIDException(IDType.USER);
        return users.get(userID).getLastBanDate();
    }

    private boolean isExpiredUser(String userId) throws InvalidIDException {
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime.getTime() > getUser(userId).getRegisterDate().getTime() + TimeUnit.MINUTES.toMillis(30);
    }

    /**
     * Logs out the user with the specified id
     * @param userId id of the user to log out
     * @throws InvalidIDException if no user has the specified userId
     */
    public void logout(String userId) throws InvalidIDException, IOException {
        if (!users.containsKey(userId))
            throw new InvalidIDException(IDType.USER);
        if (getUser(userId).getOnlineStatus() != OnlineStatus.BANNED){
            getUser(userId).setOnlineStatus(OnlineStatus.OFFLINE);
        }

        if (getUserRole(userId).equals(UserRole.TRIAL)){
            String username = users.get(userId).getUsername();
            users.remove(userId);
            userIds.remove(username);
        } else {
            gateway.updateUser(getUser(userId));
        }

    }

    public String getUsername(String userId) throws InvalidIDException {
        if (!users.containsKey(userId))
            throw new InvalidIDException(IDType.USER);
        return users.get(userId).getUsername();
    }

    /**
     * Changes the password of the user with the specified id.
     * This changes the data within this class and in the database
     * @param userId id of the user
     * @param oldPassword current password of the user to confirm the action
     * @param newPassword the password to change into
     * @throws InvalidIDException if no user has the specified userId
     * @throws IncorrectPasswordException if oldPassword does not match the user's current password
     * @throws WeakPasswordException if the password is too weak
     */
    public void editPassword(String userId, String oldPassword, String newPassword) throws
            IncorrectPasswordException, InvalidIDException, WeakPasswordException {
        if (isPasswordIncorrect(userId, oldPassword) && isTempPasswordIncorrect(userId, newPassword)){
            throw new IncorrectPasswordException();
        }
        if (!isPasswordString(newPassword)){
            throw new WeakPasswordException();
        }
        getUser(userId).setPassword(newPassword);

        try {
            gateway.updateUser(getUser(userId));
        } catch (IOException e) {
            throw new RuntimeException("Fatal error: Can't connect to the database.");
        }
    }


    /**
     * Changes a trial user to a normal user with the specified username and password
     * and keeps their game creations.
     * The username must not be taken already.
     * This stores the created user in the database
     * @param userId id of the trial user
     * @param username username of the user to create
     * @param password password of the user to create
     * @throws InvalidIDException if no user has the specified userId
     * @throws DuplicateUsernameException if the username is already taken
     * @throws UnaccountedEnumException if the user specified user is not a trial user
     */
    public void promoteTrialUser(String userId, String username, String email, UserRole role, String password) throws
            InvalidIDException, DuplicateUsernameException, UnaccountedEnumException {
        if (userIds.containsKey(username)) throw new DuplicateUsernameException();

        User user = getUser(userId);
        if (user.getRole() != UserRole.TRIAL) throw new UnaccountedEnumException();

        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(role);
        try {
            userIds.put(username, userId);
            gateway.addUser(user);
        } catch (IOException e) {
            throw new RuntimeException("System failure: Can't connect to the database");
        }
    }

    /**
     * Changes the username of the user with the specified id.
     * The username must not be taken already
     * This changes the data within this class and in the database
     * @param userId id of the user
     * @param newUsername the username to change into
     * @throws IDAlreadySetException if the username is already taken
     * @throws InvalidIDException if no user has the specified userId
     */
    public void editUsername(String userId, String newUsername) throws InvalidIDException, DuplicateUsernameException {
        if (!users.containsKey(userId))
            throw new InvalidIDException(IDType.USER);
        if (userIds.containsKey(newUsername) && !newUsername.equals(getUser(userId).getUsername()))
            throw new DuplicateUsernameException();

        User user = getUser(userId);
        userIds.remove(user.getUsername());
        userIds.put(newUsername, userId);
        user.setUsername(newUsername);

        try {
            gateway.updateUser(user);
        } catch (IOException e) {
            throw new RuntimeException("System failure: Can't connect to the database.");
        }
    }


    public void editEmail(String userId, String newEmail) throws IOException, InvalidIDException,
            InvalidEmailException {
        if (!users.containsKey(userId))
            throw new InvalidIDException(IDType.USER);
        if (!isValidEmail(newEmail))
            throw new InvalidEmailException();

        User user = getUser(userId);
        user.setEmail(newEmail);
        gateway.updateUser(user);
    }

    /**
     * Delete the user with the specified id from this class and the database
     * @param userId id of the user to delete
     * @throws InvalidIDException if no user has the specified userId
     */
    public void deleteUser(String userId) throws InvalidIDException {
            String username = users.get(userId).getUsername();
            users.remove(userId);
            userIds.remove(username);
        try {
            gateway.deleteUser(userId);
        } catch (IOException e) {
            throw new RuntimeException("Cannot connect to the database");
        }
    }

    /**
     * Add a new game ID to the set of games created by this user.
     * @param userID The ID of the user.
     * @param gameID The Id of the game.
     * */
    public void addOwnedGameID(String userID, String gameID) throws InvalidIDException, IOException {
        if (!users.containsKey(userID))
            throw new InvalidIDException(IDType.USER);

        User user = users.get(userID);
        user.addGameID(gameID);

        if (user.getRole() != UserRole.TRIAL) {
            gateway.updateUser(user);
        }
    }

    /**
     * Returns the set of all game IDs created by this user.
     * @param userID The ID of the user.
     * */
    public Set<String> getOwnedGamesID(String userID) throws InvalidIDException {
        if (!users.containsKey(userID))
            throw new InvalidIDException(IDType.USER);

        return users.get(userID).getGameCreationSet();
    }


    /**
     * Returns a set of user IDs that correspond to friends of this user.
     *
     * @param userID the ID of the user.
     * @throws InvalidIDException If there is no such user with the given ID.
     * */
    public Set<String> getFriendList(String userID) throws InvalidIDException {
        if (!users.containsKey(userID))
            throw new InvalidIDException(IDType.USER);

        return users.get(userID).getFriendList();
    }

    /**
     * Returns a set of user IDs that correspond to pendingfriends of this user.
     *
     * @param userID the ID of the user.
     * @throws InvalidIDException If there is no such user with the given ID.
     * */
    public Set<String> getPendingFriendList(String userID) throws InvalidIDException {
        if (!users.containsKey(userID))
            throw new InvalidIDException(IDType.USER);

        return users.get(userID).getPendingFriendList();
    }

    /**
     * Add an User ID to the list of pending friend of the owner.
     *
     * @param ownerID The owner of the list of pending friends
     * @param subjectID The user that is to be added.
     * */
    public void addPendingFriend(String ownerID, String subjectID) throws InvalidIDException, IOException {
        if (!users.containsKey(subjectID) || !users.containsKey(ownerID))
            throw new InvalidIDException(IDType.USER);

        if (!users.get(ownerID).getPendingFriendList().contains(subjectID)){//to avoid duplicate sends
            users.get(ownerID).addPendingFriend(subjectID);
            gateway.updateUser(users.get(ownerID));
        }

    }

    /**
     * Returns all user IDs in the system.
     * */
    public Set<String> getAllUserIDs() {
        return new HashSet<>(users.keySet());
    }

    /**
     * Remove the subjectID from the pending friend list of the user with ownerID.
     *
     * @param ownerID The owner of the list of pending friends
     * @param subjectID The user that is to be removed.
     * */
    public void removePendingFriend(String ownerID, String subjectID) throws InvalidIDException, IOException {
        if (!users.containsKey(subjectID))
            throw new InvalidIDException(IDType.USER);
        if (!users.containsKey(ownerID))
            throw new InvalidIDException(IDType.USER);
        users.get(ownerID).removePendingFriend(subjectID);
        gateway.updateUser(users.get(ownerID));
    }

    /**
     * Add the subject to the friend list of the owner.
     *
     * @param ownerID The ID of the owner of the list
     * @param subjectID The userID to be added.
     * @throws InvalidIDException when either of the IDs is invalid
     * */
    public void addFriend(String ownerID, String subjectID) throws InvalidIDException, IOException {
        if (!users.containsKey(subjectID))
            throw new InvalidIDException(IDType.USER);
        if (!users.containsKey(ownerID))
            throw new InvalidIDException(IDType.USER);
        users.get(ownerID).addFriend(subjectID);
        gateway.updateUser(users.get(ownerID));

    }

    /**
     * Remove the subject from the friend list of the owner.
     *
     * @param ownerID The ID of the owner of the list
     * @param subjectID The userID to be added.
     * @throws InvalidIDException when either of the IDs is invalid
     * */
    public void removeFriend(String ownerID, String subjectID) throws InvalidIDException, IOException {
        if (!users.containsKey(subjectID))
            throw new InvalidIDException(IDType.USER);
        if (!users.containsKey(ownerID))
            throw new InvalidIDException(IDType.USER);
        users.get(ownerID).removeFriend(subjectID);
        gateway.updateUser(users.get(ownerID));

    }

    /**
     * Ban an user for a duration.
     *
     * @param subjectID The id of the person that is to be banned.
     * @param duration The duration of the ban, in days.
     * */
    public void banUser(String subjectID, int duration) throws InvalidIDException, IOException{

        if (!users.containsKey(subjectID))
            throw new InvalidIDException(IDType.USER);

        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_YEAR, duration);
        getUser(subjectID).setLastBanDate(date.getTime());
        getUser(subjectID).setOnlineStatus(OnlineStatus.BANNED);

        gateway.updateUser(users.get(subjectID));
    }
}
