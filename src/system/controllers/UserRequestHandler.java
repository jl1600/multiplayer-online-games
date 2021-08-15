package system.controllers;

import com.sun.net.httpserver.HttpExchange;
import shared.DTOs.Requests.*;
import shared.DTOs.Responses.GeneralUserInfoResponseBody;
import shared.DTOs.Responses.LoginResponseBody;
import shared.constants.UserRole;
import shared.exceptions.use_case_exceptions.*;
import system.use_cases.managers.UserManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

public class UserRequestHandler extends RequestHandler {

    /**
     * a user manager that can manipulate all user entities
     */
    private final UserManager userManager;

    /**
     * Constructor for UserRequestHandler class.
     * @param um user manager that contains all user entities and able to make change to them
     */
    public UserRequestHandler(UserManager um) {
        this.userManager = um;
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        String specification = exchange.getRequestURI().getPath().split("/")[2];
        switch (specification) {
            case "username":
                handleGetUsername(exchange);
                break;
            case "userid":
                handleGetUserID(exchange);
                break;
            case "friends":
                handleGetFriends(exchange);
                break;
            case "pending-friends":
                handleGetPendingFriends(exchange);
                break;
            case "all-members":
                handleGetAllMembers(exchange);
                break;

            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        String specification = exchange.getRequestURI().getPath().split("/")[2];
        switch (specification) {
            case "login":
                handleLogin(exchange);
                break;
            case "logout":
                handleLogout(exchange);
                break;
            case "trial":
                handleTrial(exchange);
                break;
            case "register":
                handleRegister(exchange);
                break;
            case "delete":
                handleDeleteUser(exchange);
                break;
            case "send-friend-request":
                handleSendFriendRequest(exchange);
                break;
            case "cancel-friend-request":
                handleCancelFriendRequest(exchange);
                break;
            case "decline-pending-friend":
                handleDeclinePendingFriend(exchange);
                break;
            case "accept-pending-friend":
                handleAcceptPendingFriend(exchange);
                break;
            case "remove-friend":
                handleRemoveFriend(exchange);
                break;
            case "edit-username":
                handleEditUsername(exchange);
                break;
            case "edit-password":
                handleEditPassword(exchange);
                break;
            case "suspend":
                handleBanUser(exchange);
                break;
            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }

    private void handleEditPassword(HttpExchange exchange) throws IOException {
        EditPasswordRequestBody body = gson.fromJson(getRequestBody(exchange), EditPasswordRequestBody.class);
        try {
            userManager.editPassword(body.userID,body.oldPassword,body.newPassword);
            sendResponse(exchange, 204, null);
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 400, "Invalid user ID.");
        }catch (IncorrectPasswordException e) {
            sendResponse(exchange, 403, "Incorrect password.");
        }
    }

    private void handleEditUsername(HttpExchange exchange) throws IOException {
        EditUsernameRequestBody body = gson.fromJson(getRequestBody(exchange), EditUsernameRequestBody.class);
        try {
            userManager.editUsername(body.userID,body.newUsername);
            sendResponse(exchange, 204, null);
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 400, "Invalid user ID.");
        } catch (DuplicateUsernameException e){
            sendResponse(exchange, 403, "Duplicate username.");
        }
    }

    private void handleRemoveFriend(HttpExchange exchange) throws IOException {
        FriendRequestBody body = gson.fromJson(getRequestBody(exchange), FriendRequestBody.class);
        try {
            userManager.removeFriend(body.senderID, body.receiverID);
            userManager.removeFriend(body.receiverID, body.senderID);
            sendResponse(exchange, 204, null);
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 400, "Invalid user ID.");
        }
    }

    private void handleCancelFriendRequest(HttpExchange exchange) throws IOException {
        FriendRequestBody body = gson.fromJson(getRequestBody(exchange), FriendRequestBody.class);
        try {
            userManager.removePendingFriend(body.receiverID, body.senderID);
            sendResponse(exchange, 204, null);
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 400, "Invalid user ID.");
        }
    }

    private void handleAcceptPendingFriend(HttpExchange exchange) throws IOException {
        FriendRequestBody body = gson.fromJson(getRequestBody(exchange), FriendRequestBody.class);
        try {
            userManager.removePendingFriend(body.senderID, body.receiverID);
            userManager.addFriend(body.senderID, body.receiverID);
            userManager.addFriend(body.receiverID, body.senderID);
            sendResponse(exchange, 204, null);
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 400, "Invalid user ID.");
        }
    }

    private void handleDeclinePendingFriend(HttpExchange exchange) throws IOException {
        FriendRequestBody body = gson.fromJson(getRequestBody(exchange), FriendRequestBody.class);
        try {
            userManager.removePendingFriend(body.senderID, body.receiverID);
            sendResponse(exchange, 204, null);
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 400, "Invalid user ID.");
        }
    }

    private void handleGetAllMembers(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            sendResponse(exchange, 200, gson.toJson(getAllMembers()));
        }
        else {
            String userID = getQueryArgFromGET(exchange);
            if (userID == null)
                return;
            try {
                sendResponse(exchange, 200, gson.toJson(getAllMembersExcludeFriendsOf(userID)));
            } catch (InvalidUserIDException e) {
                sendResponse(exchange, 400, "Invalid user ID.");
            }
        }
    }

    private Set<GeneralUserInfoResponseBody> getAllMembers() {
        Set<String> IDs = userManager.getAllUserIDs();
        Set<GeneralUserInfoResponseBody> allMem = new HashSet<>();
        for (String uid: IDs) {
            GeneralUserInfoResponseBody user = new GeneralUserInfoResponseBody();
            user.userID = uid;
            try {
                if (userManager.getUserRole(uid)!= UserRole.MEMBER)
                    continue;
                user.username = userManager.getUsername(uid);
            } catch (InvalidUserIDException e) {
                throw new RuntimeException("The user id got from the set of all user ids is invalid.");
            }
            allMem.add(user);
        }
        return allMem;
    }

    private Set<GeneralUserInfoResponseBody> getAllMembersExcludeFriendsOf(String targetUser) throws InvalidUserIDException {
        Set<String> AllIDs = userManager.getAllUserIDs();
        Set<String> friendIDs = userManager.getFriendList(targetUser);
        Set<GeneralUserInfoResponseBody> allMem = new HashSet<>();
        for (String uid: AllIDs) {
            if (friendIDs.contains(uid))
                continue;
            GeneralUserInfoResponseBody user = new GeneralUserInfoResponseBody();
            user.userID = uid;
            try {
                if (userManager.getUserRole(uid)!= UserRole.MEMBER || uid.equals(targetUser))
                    continue;
                user.username = userManager.getUsername(uid);
            } catch (InvalidUserIDException e) {
                throw new RuntimeException("The user id got from the set of all user ids is invalid.");
            }
            allMem.add(user);
        }
        return allMem;
    }

    private void handleSendFriendRequest(HttpExchange exchange) throws IOException {
        FriendRequestBody body = gson.fromJson(getRequestBody(exchange), FriendRequestBody.class);
        try {
            userManager.addPendingFriend(body.receiverID, body.senderID);
            sendResponse(exchange, 204, null);
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 400, "Invalid user ID.");
        }
    }

    private void handleDeleteUser(HttpExchange exchange) throws IOException {
        DeleteUserRequestBody body = gson.fromJson(getRequestBody(exchange), DeleteUserRequestBody.class);
        try {
            userManager.deleteUser(body.userID);
            sendResponse(exchange, 204, null);
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 400, "Invalid user ID.");
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        RegisterRequestBody body = gson.fromJson(getRequestBody(exchange), RegisterRequestBody.class);
        try {
            userManager.createUser(body.username, body.password, body.role);
            sendResponse(exchange, 204, null);
        } catch (DuplicateUsernameException e) {
            sendResponse(exchange, 403, "Duplicate username.");
        } catch (WeakPasswordException e){
            sendResponse(exchange, 400, "Password isn't strong enough.");
        }
    }

    private void handleTrial(HttpExchange exchange) throws IOException {
        String trialID = userManager.createTrialUser();
        sendResponse(exchange, 200, "{\"userID\":\"" + trialID+"\"}");
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        LoginRequestBody body = gson.fromJson(getRequestBody(exchange), LoginRequestBody.class);
        try {
            String userID = userManager.login(body.username, body.password);
            LoginResponseBody resBody = new LoginResponseBody();
            resBody.userID = userID;
            resBody.role = userManager.getUserRole(userID);
            sendResponse(exchange, 200, gson.toJson(resBody));
        } catch (InvalidUsernameException | IncorrectPasswordException | ExpiredUserException e) {
            sendResponse(exchange, 400, "User doesn't exist, is expired, or the password is incorrect.");
        } catch (InvalidUserIDException e) {
            throw new RuntimeException("Invalid user ID. This should never happen.");
        } catch (BannedUserException e) {
            try {
                sendResponse(exchange, 403, "This account has been suspended. Last suspension date: " +
                        userManager.getBanLiftingDate(userManager.getUserId(body.username)));
            } catch (InvalidUserIDException | InvalidUsernameException exc) {
                throw new RuntimeException("Fatal: Banned user has invalid user ID or username.");
            }
        }
    }

    private void handleLogout(HttpExchange exchange) throws IOException {
        LogoutRequestBody body = gson.fromJson(getRequestBody(exchange), LogoutRequestBody.class);
        try {
            userManager.logout(body.userID);
            sendResponse(exchange, 204, null);
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 404, "Invalid user ID.");
        }
    }

    private void handleGetFriends(HttpExchange exchange) throws IOException {
        String ownerID = getQueryArgFromGET(exchange);
        if (ownerID == null)
            return;
        //handle load friend list as response body
        Set<GeneralUserInfoResponseBody> dataSet = new HashSet<>();

        try{
            Set<String> allFriends = userManager.getFriendList(ownerID);
            for (String id : allFriends){
                GeneralUserInfoResponseBody frb = new GeneralUserInfoResponseBody();
                frb.userID = id;
                frb.username = userManager.getUsername(id);
                dataSet.add(frb);
            }

        } catch (InvalidUserIDException e) {
            throw new RuntimeException("A friend ID in the friend list is invalid. This should never happen.");
        }
        //send response
        sendResponse(exchange, 200, gson.toJson(dataSet));
    }

    private void handleGetPendingFriends(HttpExchange exchange) throws IOException {
        //handle get ownerID
        String ownerID = getQueryArgFromGET(exchange);
        if (ownerID == null)
            return;
        //handle load friend list as response body
        Set<GeneralUserInfoResponseBody> dataSet = new HashSet<>();

        try{
            Set<String> allFriends = userManager.getPendingFriendList(ownerID);
            for (String id : allFriends){
                GeneralUserInfoResponseBody frb = new GeneralUserInfoResponseBody();
                frb.userID = id;
                frb.username = userManager.getUsername(id);
                dataSet.add(frb);
            }
        } catch (InvalidUserIDException e) {
            throw new RuntimeException("A user in this pending list has an invalid ID. This means illegal datum.");
        }
        //send response
        sendResponse(exchange, 200, gson.toJson(dataSet));
    }

    private void handleGetUserID(HttpExchange exchange) throws IOException {
        String username;
        try {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                sendResponse(exchange, 400, "Missing Query.");
                return;
            }
            username = query.split("=")[1];
        } catch (MalformedURLException e) {
            sendResponse(exchange, 404, "Malformed URL.");
            return;
        }
        try {
            sendResponse(exchange, 200, "{\"userID\":\"" + userManager.getUserId(username)+"\"}");
        } catch (InvalidUsernameException e) {
            sendResponse(exchange, 400, "Invalid username.");
        }

    }

    private void handleGetUsername(HttpExchange exchange) throws IOException {
        String userID;
        try {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                sendResponse(exchange, 400, "Missing Query.");
                return;
            }
            userID = query.split("=")[1];
        } catch (MalformedURLException e) {
            sendResponse(exchange, 404, "Malformed URL.");
            return;
        }
        try {
            sendResponse(exchange, 200, "{\"username\":\"" + userManager.getUsername(userID)+"\"}");
        } catch (InvalidUserIDException e) {
            e.printStackTrace();
        }
    }

    private String getQueryArgFromGET(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                sendResponse(exchange, 400, "Missing Query.");
                return null;
            }
            return query.split("=")[1];
        } catch (MalformedURLException e) {
            sendResponse(exchange, 404, "Malformed URL.");
            return null;
        }
    }

    private void handleBanUser(HttpExchange exchange) throws IOException {
        BanUserRequestBody body = gson.fromJson(getRequestBody(exchange), BanUserRequestBody.class);
        try {
            if (!hasPermission(exchange, userManager.getUserRole(body.adminID), UserRole.ADMIN))
                return;
            userManager.banUser(body.adminID, body.userID, body.banLength);
            sendResponse(exchange, 204, null);
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 400, "Invalid user ID.");
        }
    }
}
