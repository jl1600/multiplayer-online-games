package system.use_cases.managers;

import java.util.*;
import shared.constants.OnlineStatus;
import shared.constants.UserRole;
import shared.exceptions.entities_exception.IDAlreadySetException;
import shared.exceptions.entities_exception.UnaccountedUserRoleException;
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

    public UserManager(UserDataGateway gateway) throws IOException, InvalidUserIDException {
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

    public String getEmail(String userID) throws InvalidUsernameException, InvalidUserIDException {
        if (!users.containsKey(userID))
            throw new InvalidUsernameException();

        return getUser(userID).getEmail();
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
     */
    public void createUser(String username, String password, String email, UserRole role)
            throws DuplicateUsernameException, UnaccountedUserRoleException, WeakPasswordException, InvalidEmailException {
        if (role.equals(UserRole.TRIAL))
            throw new UnaccountedUserRoleException();
        if (userIds.containsKey(username))
            throw new DuplicateUsernameException();
        if (!checkPasswordStrength(password))
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

    private boolean checkPasswordStrength(String password){
        boolean characters = false;
        boolean hasNumbers = false;
        boolean hasSpecialChar = false;
        boolean isLong = password.length() >= 6;

        Pattern characterPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z]).+$");
        Matcher characterMatcher = characterPattern.matcher(password);
        if (characterMatcher.find()){
            characters = true;
        }

        if (password.matches(".*\\d+.*")){
            hasNumbers = true;
        }

        Pattern specialRegex = Pattern.compile("[$&+,:;=?@#|!]");
        Matcher specialMatcher = specialRegex.matcher(password);
        if (specialMatcher.find()){
            hasSpecialChar = true;
        }

        System.out.println(password + characters + hasNumbers + hasSpecialChar + isLong);
        return characters && hasNumbers && hasSpecialChar && isLong;
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

        if (!userIds.containsKey(username))
            throw new InvalidUsernameException();

        String userId = getUserId(username);
        try {
            if (isPasswordIncorrect(userId, password) && isTempPasswordIncorrect(userId, password))
                throw new IncorrectPasswordException();
            if (isBanned(userId)) throw new BannedUserException();
            if (getUserRole(userId) == UserRole.TEMP){
                if (isExpiredUser(userId)){
                    throw new ExpiredUserException();
                }
            }
            getUser(userId).setOnlineStatus(OnlineStatus.ONLINE);
        } catch (InvalidUserIDException e1) {
            throw new RuntimeException("System failure: The database ID associated with this user is invalid.");
        }
        return userId;
    }

    private boolean isTempPasswordIncorrect(String userId, String password) {
        if (!tempPasswords.containsKey(userId) || !tempPasswords.get(userId).containsKey(password))
            return true;
        else if (tempPasswords.get(userId).get(password).after(Calendar.getInstance().getTime())) {
            tempPasswords.remove(userId);
            return true;
        }
        return false;
    }

    private boolean isBanned(String userId) throws InvalidUserIDException {
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
     * @throws InvalidUserIDException when the user is not banned or there is no such user.
     */
    public Date getBanLiftingDate(String userID) throws InvalidUserIDException {
        if (!users.containsKey(userID) || users.get(userID).getOnlineStatus() != OnlineStatus.BANNED)
            throw new InvalidUserIDException();
        return users.get(userID).getLastBanDate();
    }

    private boolean isExpiredUser(String userId) throws InvalidUserIDException {
        Date currentTime = Calendar.getInstance().getTime();
        //getTime return a long that is total millisec since Jan 1st 1970
        //add 30 days in millisec gives the expiration date
        // if current date > expiration date
        //return true (expired)
        return currentTime.getTime() > getUser(userId).getRegisterDate().getTime() + TimeUnit.MINUTES.toMillis(30);
    }

    /**
     * Logs out the user with the specified id
     * @param userId id of the user to log out
     * @throws InvalidUserIDException if no user has the specified userId
     */
    public void logout(String userId) throws InvalidUserIDException, IOException {
        if (!users.containsKey(userId))
            throw new InvalidUserIDException();
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

    public String getUsername(String userId) throws InvalidUserIDException {
        if (!users.containsKey(userId))
            throw new InvalidUserIDException();
        return users.get(userId).getUsername();
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
            IncorrectPasswordException, InvalidUserIDException {
        if (isPasswordIncorrect(userId, oldPassword) && isTempPasswordIncorrect(userId, newPassword)){
            throw new IncorrectPasswordException();
        }

        getUser(userId).setPassword(newPassword);

        try {
            gateway.updateUser(getUser(userId));
        } catch (IOException e) {
            throw new RuntimeException("Fatal error: Can't connect to the database.");
        }
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
    public void editUsername(String userId, String newUsername) throws InvalidUserIDException, DuplicateUsernameException {
        if (!users.containsKey(userId))
            throw new InvalidUserIDException();
        if (userIds.containsKey(newUsername) && !newUsername.equals(getUser(userId).getUsername()))
            throw new DuplicateUsernameException();

        User user = getUser(userId);
        userIds.remove(user.getUsername());
        userIds.put(newUsername, userId);
        user.setUsername(newUsername);

        try {
            gateway.updateUser(user);
        } catch (IOException e) {
            throw new RuntimeException("Fatal: Can't connect to the database.");
        }
    }


    public void editEmail(String userId, String newEmail) throws IOException, InvalidUserIDException {
        if (!users.containsKey(userId))
            throw new InvalidUserIDException();


        User user = getUser(userId);
        user.setEmail(newEmail);
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
        if (userIds.containsKey(username))
            throw new DuplicateUsernameException();

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
     * @throws InvalidUserIDException if no user has the specified userId
     */
    public void deleteUser(String userId) throws InvalidUserIDException {
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


    public Set<String> getFriendList(String userID) throws InvalidUserIDException {
        if (!users.containsKey(userID))
            throw new InvalidUserIDException();

        return users.get(userID).getFriendList();
    }


    public Set<String> getPendingFriendList(String userID) throws InvalidUserIDException {
        if (!users.containsKey(userID))
            throw new InvalidUserIDException();

        return users.get(userID).getPendingFriendList();
    }

    public void addPendingFriend(String ownerID, String subjectID) throws InvalidUserIDException, IOException {
        if (!users.containsKey(subjectID))
            throw new InvalidUserIDException();

        if (!users.containsKey(ownerID))
            throw new InvalidUserIDException();

        if (!users.get(ownerID).getPendingFriendList().contains(subjectID)){//to avoid duplicate sends
            users.get(ownerID).addPendingFriend(subjectID);
            gateway.updateUser(users.get(ownerID));
        }

    }

    public Set<String> getAllUserIDs() {
        return new HashSet<>(users.keySet());
    }

    public void removePendingFriend(String ownerID, String subjectID) throws InvalidUserIDException, IOException {
        if (!users.containsKey(subjectID))
            throw new InvalidUserIDException();
        if (!users.containsKey(ownerID))
            throw new InvalidUserIDException();
        users.get(ownerID).removePendingFriend(subjectID);
        gateway.updateUser(users.get(ownerID));
    }

    public void addFriend(String ownerID, String subjectID) throws InvalidUserIDException, IOException {
        if (!users.containsKey(subjectID))
            throw new InvalidUserIDException();
        if (!users.containsKey(ownerID))
            throw new InvalidUserIDException();
        users.get(ownerID).addFriend(subjectID);
        gateway.updateUser(users.get(ownerID));

    }

    public void removeFriend(String ownerID, String subjectID) throws InvalidUserIDException, IOException {
        if (!users.containsKey(subjectID))
            throw new InvalidUserIDException();
        if (!users.containsKey(ownerID))
            throw new InvalidUserIDException();
        users.get(ownerID).removeFriend(subjectID);
        gateway.updateUser(users.get(ownerID));

    }

    public void banUser(String adminID, String subjectID, int duration) throws InvalidUserIDException, IOException{

        if (!users.containsKey(subjectID) || !users.containsKey(adminID))
            throw new InvalidUserIDException();

        if (users.get(adminID).getRole() != UserRole.ADMIN)
            throw new InsufficientPrivilegeException();

        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_YEAR, duration);
        getUser(subjectID).setLastBanDate(date.getTime());
        getUser(subjectID).setOnlineStatus(OnlineStatus.BANNED);

        gateway.updateUser(users.get(subjectID));
    }
}
