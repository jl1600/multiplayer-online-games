package system.entities;

import shared.constants.OnlineStatus;
import shared.constants.UserRole;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * User Class
 */
public class User {
    private final String userId;
    private String username;
    private String password;
    private String email;
    private UserRole role;
    private OnlineStatus onlineStatus;
    private final Set<String> gameCreations;
    private final Date registerDate;
    private final Set<String> friendList;
    private final Set<String> pendingFriendList;
    private Date lastBanDate; // a word play on the last ban date and the last given ban date

    /**
     * Constructor of User
     * @param userId the string identifier of this user for within system program communication
     * @param username the username that user use to login and identify themselves
     * @param password the password user use to prove their identity
     * @param role the user role that determines the appropriate action to take and rules to follow for this user
     */
    public User(String userId, String username, String password, String email, UserRole role, Date registerDate) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.onlineStatus = OnlineStatus.OFFLINE;
        this.gameCreations = new HashSet<>();
        this.registerDate = registerDate;
        this.friendList = new HashSet<>();
        this.pendingFriendList = new HashSet<>();
        this.lastBanDate = registerDate;
    }

    /**
     * @return id of the user
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username new username for the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param password the password to check the user's password against
     * @return whether or not the specified password matches the user's password
     */
    public boolean isPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * @param password new password for the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return role of the user
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * Set the role of this user.
     * */
    public void setRole(UserRole role) {
        this.role = role;
    }
    /**
     * @param id id of the game to add to the user's set of game creations
     */
    public void addGameID(String id) {
        gameCreations.add(id);
    }

    /**
     * @return all ids of the games created by the user
     */
    public Set<String> getGameCreationSet() {
        return new HashSet<>(gameCreations);
    }

    /**
     * @return online status of the user
     */
    public OnlineStatus getOnlineStatus() {
        return onlineStatus;
    }

    /**
     * @param status online status of the user
     */
    public void setOnlineStatus(OnlineStatus status) {
        onlineStatus = status;
    }


    /**
     * @return this user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @return this user's email
     */
    public String getEmail() { return email;}

    /**
     * @param email inputted email
     */
    public void setEmail(String email) {this.email = email;}

    /**
     *
     * @param gameID the game id wish to be removed
     */
    public void removeGameID(String gameID) {
        gameCreations.remove(gameID);
    }

    /**
     *
     * @return this user's register date
     */
    public Date getRegisterDate(){return registerDate;}

    /**
     *
     * @return this user's friend list
     */
    public Set<String> getFriendList() {
        return friendList;
    }

    /**
     *
     * @param senderID the id of the sender who will now be added to this user's friend list
     */
    public void addFriend(String senderID) {
        this.friendList.add(senderID);
    }

    /**
     *
     * @param friendID the id which will be removed from this user's friend list
     */
    public void removeFriend(String friendID) {
        this.friendList.remove(friendID);
    }
    /**
     *
     * @return this user's pending friend list
     */
    public Set<String> getPendingFriendList() {
        return pendingFriendList;
    }

    /**
     *
     * @param senderID the id that will be added to this user's pending friend list
     */
    public void addPendingFriend(String senderID) {
        this.pendingFriendList.add(senderID);
    }

    /**
     *
     * @param senderID the id that will be removed from this user's friend list
     */
    public void removePendingFriend(String senderID) {
        this.pendingFriendList.remove(senderID);
    }

    /**
     *
     * @return the last ban date of this user
     */
    public Date getLastBanDate() {
        return lastBanDate;
    }

    /**
     *
     * @param lastBanDate the desired last ban date
     */
    public void setLastBanDate(Date lastBanDate) {
        this.lastBanDate = lastBanDate;
    }
}
