package client.controllers;

import client.gateways.IServerCommunicator;
import shared.request.game_request.*;
import shared.request.template_request.*;
import shared.request.user_request.*;
import shared.request.template_request.GetAllTemplateInfoRequest;
import shared.request.template_request.MakeTemplateDesignChoiceRequest;
import shared.request.template_request.NewTemplateRequest;
import shared.request.user_request.LoginRequest;
import shared.request.user_request.NewNormalUserRequest;
import shared.request.user_request.NewTrialUserRequest;
import shared.response.user.LoginResponse;

public class ClientController implements IClientController {
    IServerCommunicator communicator;
    String userID;
    String sessionID;

    /**
     * Constructor of ClientController
     * @param communicator the gateway from client to server system
     */
    public ClientController(IServerCommunicator communicator) {
        this.communicator = communicator;
        this.sessionID = communicator.getSessionID();
    }

    /**
     * Sends login request to server
     * @param username the username received from input
     * @param password the password received from input
     */
    @Override
    public void sendLoginRequest(String username, String password) {
        communicator.sendRequest(new LoginRequest(communicator.getSessionID(), username, password));
        receiveUserId();
    }

    /**
     * Sends a register normal user request to server
     * @param username the username received from input
     * @param password the password received from input
     */
    @Override
    public void sendRegisterNormalUserRequest(String username, String password) {
        communicator.sendRequest(new NewNormalUserRequest(communicator.getSessionID(), username, password));
    }

    /**
     * Sends a register temporary user request to server
     * @param username
     * @param password
     */
    @Override
    public void sendRegisterTemporaryUserRequest(String username, String password) {
        communicator.sendRequest(new NewTempUserRequest(communicator.getSessionID(), username, password));

    }

    /**
     * Sends a register admin user request to server
     * @param username the username received from input
     * @param password the password received from input
     */
    @Override
    public void sendRegisterAdminUserRequest(String username, String password) {
        communicator.sendRequest(new NewAdminUserRequest(communicator.getSessionID(), username, password));
    }

    /**
     * Sends a register trial user request to server
     */
    @Override
    public void sendRegisterTrialUserRequest() {
        communicator.sendRequest(new NewTrialUserRequest(sessionID));
        receiveUserId();
    }

    /**
     * Sends a delete user request to server
     * @param password the password received from input
     */
    @Override
    public void sendDeleteUserRequest(String password) {
        communicator.sendRequest(new DeleteUserRequest(sessionID, userID, password));
    }

    /**
     * Sends an edit password request to server
     * @param password the current password received from input
     * @param newPassword the new password received from input
     */
    @Override
    public void sendEditPasswordRequest(String password, String newPassword) {
        communicator.sendRequest(new EditPasswordRequest(sessionID, userID, password, newPassword));
    }

    /**
     * Sends an edit username request to server
     * @param newUsername the new username recieved from input
     */
    @Override
    public void sendEditUserNameRequest(String newUsername) {
        communicator.sendRequest(new EditUsernameRequest(sessionID, userID, newUsername));
    }

    /**
     * send a logout request to server
     */
    @Override
    public void sendLogoutRequest() {
        communicator.sendRequest(new LogoutRequest(sessionID, userID));
    }

    @Override
    public void sendPromoteTrialUserRequest(String username, String password) {
        communicator.sendRequest(new PromoteTrialUserRequest(sessionID, userID, username, password));
    }

    @Override
    public void sendGetUserRoleRequest() {
        communicator.sendRequest(new GetUserRoleRequest(
                communicator.getSessionID(),
                userID
        ));
    }

    @Override
    public void sendNewGameRequest(String TemplateID) {
        communicator.sendRequest(new NewGameRequest(sessionID, userID, TemplateID));
    }

    @Override
    public void sendMakeGameDesignChoiceRequest(String designChoice) {
        communicator.sendRequest(new MakeGameDesignChoiceRequest(sessionID, userID, designChoice));
    }

    @Override
    public void sendNewGameMatchRequest(String gameID) {
        communicator.sendRequest(new NewGameMatchRequest(sessionID, userID, gameID));
    }

    @Override
    public void sendPlayGameMoveRequest(String matchID, String move) {
        communicator.sendRequest(new PlayGameMoveRequest(sessionID, userID, matchID, move));
    }

    @Override
    public void sendNewTemplateRequest() {
        communicator.sendRequest(new NewTemplateRequest(sessionID, userID));
    }

    @Override
    public void sendMakeTemplateDesignChoiceRequest(String designChoice) {
        communicator.sendRequest(new MakeTemplateDesignChoiceRequest(sessionID, userID, designChoice));
    }

    @Override
    public void sendGetAllTemplateInfoRequest() {
        communicator.sendRequest(new GetAllTemplateInfoRequest(sessionID, userID));
    }

    @Override
    public void sendEditTemplateAttributeRequest(String templateID, String attributeName, String attributeValue) {
        communicator.sendRequest(new EditTemplateAttributeRequest(sessionID, userID,
                templateID, attributeName, attributeValue));
    }

    @Override
    public void sendStartTemplateEditRequest(String templateID) {
        communicator.sendRequest(new StartTemplateEditRequest(sessionID, userID, templateID));
    }

    @Override
    public void sendCancelTemplateEditRequest(String templateID) {
        communicator.sendRequest(new CancelTemplateEditRequest(sessionID, userID, templateID));
    }

    @Override
    public void sendSaveTemplateEditRequest(String templateID) {
        communicator.sendRequest(new SaveTemplateEditRequest(sessionID, userID, templateID));
    }

    @Override
    public void sendJoinGameMatchRequest(String chosenMatch) {
        communicator.sendRequest(new JoinGameMatchRequest(sessionID, userID, chosenMatch));
    }

    @Override
    public void sendGetAllMatchInfoRequest() {
        communicator.sendRequest(new GetAllMatchInfoRequest(sessionID, userID));
    }

    @Override
    public void sendGetAllGameInfoRequest() {
        communicator.sendRequest(new GetAllPublicGamesInfoRequest(sessionID, userID));
    }

    @Override
    public void sendGetSelfOwnedGameInfoRequest() {
        communicator.sendRequest(new GetOwnedGameInfoRequest(sessionID, userID, userID));
    }

    @Override
    public void sendGetUserOwnedGameInfoRequest(String targetUserID) {
        communicator.sendRequest(new GetOwnedGameInfoRequest(sessionID, userID, targetUserID));
    }

    @Override
    public void sendDeleteGameRequest(String gameChoice) {
        communicator.sendRequest(new DeleteGameRequest(sessionID,userID,gameChoice));
    }

    @Override
    public void sendSetGamePublicStatusRequest(String gameID, boolean isPublic) {
        communicator.sendRequest(new SetGamePublicStatusRequest(sessionID, userID, gameID, isPublic));
    }

    @Override
    public void sendDeleteTemplateRequest(String templateID) {
        communicator.sendRequest(new DeleteTemplateRequest(sessionID, userID, templateID));
    }

    private void receiveUserId() {
        if (communicator.getResponse() instanceof LoginResponse) {
            LoginResponse res = (LoginResponse) communicator.getResponse();
            this.userID = res.getUserId();
        }
    }


}
