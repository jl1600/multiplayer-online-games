package client.controllers;

public interface IClientController {

    // User requests
    void sendLoginRequest(String username, String password);
    void sendRegisterNormalUserRequest(String username, String password);
    void sendRegisterTrialUserRequest();
    void sendRegisterAdminUserRequest(String username, String password);
    void sendDeleteUserRequest(String password);
    void sendEditPasswordRequest(String password, String newPassword);
    void sendEditUserNameRequest(String newUsername);
    void sendLogoutRequest();
    void sendPromoteTrialUserRequest(String username, String password);
    void sendGetUserRoleRequest();

    // Game requests
    void sendNewGameRequest(String TemplateID);
    void sendMakeGameDesignChoiceRequest(String designChoice);
    void sendNewGameMatchRequest(String gameID);
    void sendPlayGameMoveRequest(String matchID, String move);
    void sendJoinGameMatchRequest(String matchID);
    void sendGetAllMatchInfoRequest();
    void sendGetAllGameInfoRequest();
    void sendGetSelfOwnedGameInfoRequest();
    void sendGetUserOwnedGameInfoRequest(String targetUserID);
    void sendDeleteGameRequest(String gameID);
    void sendSetGamePublicStatusRequest(String gameID, boolean isPublic);

    // Template requests
    void sendNewTemplateRequest();
    void sendMakeTemplateDesignChoiceRequest(String designChoice);
    void sendGetAllTemplateInfoRequest();
    void sendEditTemplateAttributeRequest(String templateID, String attributeName, String attributeValue);
    void sendStartTemplateEditRequest(String templateID);
    void sendCancelTemplateEditRequest(String templateID);
    void sendSaveTemplateEditRequest(String templateID);
    void sendDeleteTemplateRequest(String templateID);
}
