package system.controllers;

import shared.constants.UserRole;
import shared.exceptions.entities_exception.IDAlreadySetException;
import shared.exceptions.entities_exception.UnaccountedUserRoleException;
import shared.exceptions.use_case_exceptions.*;
import shared.request.Request;
import shared.request.user_request.*;
import shared.response.*;
import shared.response.misc.ErrorMessageResponse;
import shared.response.misc.SimpleTextResponse;
import shared.response.user.LoginResponse;
import shared.response.user.UserRoleResponse;
import system.use_cases.managers.UserManager;

import java.io.IOException;

public class UserRequestHandler implements RequestHandler {

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

    /**
     * @param request the request to handle
     * @return a response message regarding the success of the request
     */
    @Override
    public Response handleRequest(Request request) {
        if (request instanceof DeleteUserRequest) {
            return handleDeleteUserRequest((DeleteUserRequest) request);
        } else if (request instanceof EditPasswordRequest) {
            return handleEditPasswordRequest((EditPasswordRequest) request);
        } else if (request instanceof EditUsernameRequest) {
            return handleEditUsernameRequest((EditUsernameRequest) request);
        } else if (request instanceof LoginRequest) {
            return handleLoginRequest((LoginRequest) request);
        } else if (request instanceof LogoutRequest) {
            return handleLogoutRequest((LogoutRequest) request);
        } else if (request instanceof NewNormalUserRequest) {
            return handleNewNormalUserRequest((NewNormalUserRequest) request);
        } else if (request instanceof NewAdminUserRequest) {
            return handleNewAdminUserRequest((NewAdminUserRequest) request);
        } else if (request instanceof NewTrialUserRequest) {
            return handleNewTrialUserRequest((NewTrialUserRequest) request);
        } else if (request instanceof  NewTempUserRequest){
            return handleNewTempUserRequest((NewTempUserRequest) request);
        } else if (request instanceof PromoteTrialUserRequest) {
            return handlePromoteTrialUserRequest((PromoteTrialUserRequest) request);
        } else if (request instanceof GetUserRoleRequest) {
            return handleGetUserRoleRequest((GetUserRoleRequest) request);
        } else if (request instanceof BanUserRequest) {
            return handleBanUserRequest((BanUserRequest) request);
        } else {
            return new ErrorMessageResponse(request.getSessionID(), "Error: unidentified request");
        }
    }

    private Response handleNewTempUserRequest(NewTempUserRequest request) {
        String sessionID = request.getSessionID();
        String username = request.getUsername();
        String password = request.getPassword();

        try {
            userManager.createUser(username, password, UserRole.TEMP);
            return new SimpleTextResponse(request.getSessionID(), "Successfully created the account!");
        } catch (DuplicateUsernameException e) {
            return new ErrorMessageResponse(sessionID, "Error: Username already taken");
        } catch (IOException e){
            return new ErrorMessageResponse(sessionID, "Error: Invalid Database");
        } catch (UnaccountedUserRoleException e) {
            throw new RuntimeException("This will never happen because we are passing in UserRole.TEMP as the role parameter");
        }
    }

    /**
     * ask the userManager to get user role based on request info and pass response back to super
     * @param request a request that should contain appropriate information required to obtain user role
     * @return a response that contains the gotten user role or error explaining the user was not found
     */
    private Response handleGetUserRoleRequest(GetUserRoleRequest request) {
        String sessionID = request.getSessionID();
        try{
            return new UserRoleResponse(sessionID,
                    userManager.getUserRole(request.getUserId())
            );
        } catch (InvalidUserIDException e) {
            return new ErrorMessageResponse(sessionID, "Error: User does not exist");
        }

    }

    /**
     * ask the userManager to delete based on request info and pass response back to super
     * @param request a request that should contain appropriate information required to delete user
     * @return a response that contains success text or error with explanation
     */
    private Response handleDeleteUserRequest(DeleteUserRequest request) {
        String sessionID = request.getSessionID();

        try {
            userManager.deleteUser(request.getUserId(), request.getPassword());
            return new SimpleTextResponse(sessionID, "Deleted user");
        } catch (IncorrectPasswordException e) {
            return new ErrorMessageResponse(sessionID, "Error: Incorrect password");
        } catch (InvalidUserIDException e) {
            return new ErrorMessageResponse(sessionID, "Error: User does not exist");
        } catch (IOException e){
            return new ErrorMessageResponse(sessionID, "Error: Invalid Database");
        }
    }

    /**
     * ask the userManager to "edit password" based on request info and pass response back to super
     * @param request a request that should contain appropriate information required to edit password
     * @return a response that contains success text or error with explanation
     */
    private Response handleEditPasswordRequest(EditPasswordRequest request) {
        String sessionID = request.getSessionID();

        try {
            userManager.editPassword(request.getUserID(), request.getPassword(), request.getNewPassword());
            return new SimpleTextResponse(sessionID, "Password changed successfully");
        } catch (InvalidUserIDException e) {
            return new ErrorMessageResponse(sessionID, "Error: User does not exist");
        } catch (IncorrectPasswordException e) {
            return new ErrorMessageResponse(sessionID, "Error: Incorrect password");
        } catch (IOException e) {
            return new ErrorMessageResponse(sessionID, "Error: Invalid Database");
        }
    }

    /**
     * ask the userManager to edit username based on request info and pass response back to super
     * @param request a request that should contain appropriate information required to edit username
     * @return a response that contains success text or error with explanation
     */
    private Response handleEditUsernameRequest(EditUsernameRequest request){
        String sessionID = request.getSessionID();

        try {
            userManager.editUsername(request.getUserId(), request.getNewUsername());
            return new SimpleTextResponse(sessionID, "Username changed successfully");
        } catch (IDAlreadySetException e) {
            return new ErrorMessageResponse(sessionID, "Error: Username already taken");
        } catch (InvalidUserIDException e) {
            return new ErrorMessageResponse(sessionID, "Error: User does not exist");
        } catch (IOException e){
            return new ErrorMessageResponse(sessionID, "Error: Invalid Database");
        }
    }

    /**
     * ask the userManager to perform login based on request info and pass response back to super
     * @param request a request that should contain appropriate information required to perform login
     * @return a response that contains success text and loggedIn user id used for further
     * communication or return response of error with explanation
     */
    private Response handleLoginRequest(LoginRequest request) {
        String sessionID = request.getSessionID();

        try {
            String userId = userManager.login(request.getUsername(), request.getPassword());
            return new LoginResponse(sessionID, "Logged in", userId);
        } catch (InvalidUsernameException e) {
            return new ErrorMessageResponse(sessionID, "Error: Username does not exist");
        } catch (InvalidUserIDException e) {
            return new ErrorMessageResponse(sessionID, "Error: User does not exist");
        } catch (IncorrectPasswordException e) {
            return new ErrorMessageResponse(sessionID, "Error: Incorrect password");
        } catch (ExpiredUserException e){
            return  new ErrorMessageResponse(sessionID, "Error: This temporary user has expired");
        }
    }

    /**
     * ask the userManager to perform logout based on request info and pass response back to super
     * @param request a request that should contain appropriate information required to logout
     * @return a response that contains success text or error with explanation
     */
    private Response handleLogoutRequest(LogoutRequest request) {
        String sessionID = request.getSessionID();

        try {
            userManager.logout(request.getUserId());
            return new SimpleTextResponse(sessionID, "Logged out");
        } catch (InvalidUserIDException e) {
            return new ErrorMessageResponse(sessionID, "Error: Username does not exist");
        }
    }

    /**
     * ask the userManager to perform create new normal user based on request info and pass response back to super
     * @param request a request that should contain appropriate information required to create new normal user
     * @return a response that contains success text and userId for further communication or error with explanation
     */
    private Response handleNewNormalUserRequest(NewNormalUserRequest request) {
        String sessionID = request.getSessionID();
        String username = request.getUsername();
        String password = request.getPassword();

        try {
            userManager.createUser(username, password, UserRole.MEMBER);
            return new SimpleTextResponse(request.getSessionID(), "Successfully created the account!");
        } catch (DuplicateUsernameException e) {
            return new ErrorMessageResponse(sessionID, "Error: Username already taken");
        } catch (IOException e){
            return new ErrorMessageResponse(sessionID, "Error: Invalid Database");
        } catch (UnaccountedUserRoleException e) {
            throw new RuntimeException("This will never happen because we are passing in UserRole.MEMBER as the role parameter");
        }
    }

    /**
     * ask the userManager to create new admin user based on request info and pass response back to super
     * @param request a request that should contain appropriate information required to create new admin user
     * @return a response that contains success text and userId for further communication or error with explanation
     */
    private Response handleNewAdminUserRequest(NewAdminUserRequest request) {
        String sessionID = request.getSessionID();
        String username = request.getUsername();
        String password = request.getPassword();

        try {
            userManager.createUser(username, password, UserRole.ADMIN);
            return new SimpleTextResponse(request.getSessionID(), "Successfully created the account!");
        } catch (DuplicateUsernameException e) {
            return new ErrorMessageResponse(sessionID, "Error: Username already taken");
        } catch (IOException e){
            return new ErrorMessageResponse(sessionID, "Error: Invalid Database");
        } catch (UnaccountedUserRoleException e) {
            throw new RuntimeException("This will never happen because we are passing in UserRole.ADMIN as the role parameter");
        }
    }


    /**
     * ask the userManager to create new trial user based on request info and pass response back to super
     * @param request a request that should contain appropriate information required to create new admin user
     * @return a response that contains success text and userId for further communication or error with explanation
     */
    private Response handleNewTrialUserRequest(NewTrialUserRequest request){
        String sessionID = request.getSessionID();
        String userId = userManager.createTrialUser();
        return new LoginResponse(sessionID, "Successfully created user", userId);
    }

    private Response handlePromoteTrialUserRequest(PromoteTrialUserRequest request) {
        String sessionID = request.getSessionID();

        try {
            userManager.promoteTrialUser(request.getUserId(), request.getUsername(), request.getPassword());
            return new SimpleTextResponse(sessionID, "Successfully promoted user");
        } catch (DuplicateUsernameException e) {
            return new ErrorMessageResponse(sessionID, "Error: Username already taken");
        } catch (InvalidUserIDException e) {
            return new ErrorMessageResponse(sessionID, "Error: Invalid Id");

        } catch (UnaccountedUserRoleException e) {
            return new ErrorMessageResponse(sessionID, "Error: User is not a trial user");
        } catch (IOException e){
            return new ErrorMessageResponse(sessionID, "Error: Invalid Database");
        }
    }

    private Response handleBanUserRequest(BanUserRequest request) {
        String sessionID = request.getSessionID();

        try {
            userManager.banUser(request.getAdminId(), request.getUserId(), request.getBanLength());
            return new SimpleTextResponse(sessionID, "Successfully banned user");
        }
        catch (InvalidUserIDException e) {
            return new ErrorMessageResponse(sessionID, "Error: Invalid Id");
        }
    }
}
