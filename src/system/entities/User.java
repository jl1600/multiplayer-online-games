package system.entities;

import shared.constants.OnlineStatus;
import shared.constants.UserRole;
import shared.exceptions.entities_exception.UnaccountedUserRoleException;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/** Represents a User.
 *
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
     *
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
     * Changes a trial user to a normal user
     * @throws UnaccountedUserRoleException if the user is not a trial user
     */
    public void trialToNormal() throws UnaccountedUserRoleException {
        if (role == UserRole.TRIAL) {
            role = UserRole.MEMBER;
        } else {
            throw new UnaccountedUserRoleException();
        }
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() { return email;}

    public void setEmail(String email) {this.email = email;}

    public void removeGameID(String gameID) {
        gameCreations.remove(gameID);
    }

    public Date getRegisterDate(){return registerDate;}

    public Set<String> getFriendList() {
        return friendList;
    }

    public void addFriend(String senderID) {
        this.friendList.add(senderID);
    }
    public void removeFriend(String friendID) {
        this.friendList.remove(friendID);
    }

    public Set<String> getPendingFriendList() {
        return pendingFriendList;
    }

    public void addPendingFriend(String senderID) {
        this.pendingFriendList.add(senderID);
    }
    public void removePendingFriend(String friendID) {
        this.pendingFriendList.remove(friendID);
    }

    public Date getLastBanDate() {
        return lastBanDate;
    }
    public void setLastBanDate(Date lastBanDate) {
        this.lastBanDate = lastBanDate;
    }
}
