package client.presenters;

import shared.constants.UserRole;

import java.util.Map;

public interface IClientPresenter {
    String getTextFromResponse();
    Map<String, String> getTemplateInfoMapFromResponse() throws IncorrectResponseTypeException;
    UserRole getUserRoleFromResponse() throws IncorrectResponseTypeException;
    boolean isResponseErrorMessage();

    Map<String, String> getMatchInfoMapFromResponse();

    String getNewMatchIdFromResponse();

    Map<String, String> getGameInfoMapFromResponse();

    Map<String, String> getTemplateAttributeMapFromResponse();
}
