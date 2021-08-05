package client.presenters;

import client.gateways.IServerCommunicator;
import shared.constants.UserRole;
import shared.exceptions.use_case_exceptions.IncorrectResponseTypeException;
import shared.response.*;
import shared.response.game.GameInfoMapResponse;
import shared.response.game.MatchInfoMapResponse;
import shared.response.game.NewGameMatchResponse;
import shared.response.misc.ErrorMessageResponse;
import shared.response.misc.FinishStateResponse;
import shared.response.misc.SimpleTextResponse;
import shared.response.template.StartTemplateEditResponse;
import shared.response.template.TemplateInfoMapResponse;
import shared.response.user.LoginResponse;
import shared.response.user.UserRoleResponse;

import java.io.IOException;
import java.util.Map;

public class CommandPromptPresenter implements IClientPresenter {
    IServerCommunicator communicator;

    public CommandPromptPresenter(IServerCommunicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public String getTextFromResponse() {
        Response res = communicator.getResponse();
        if (res instanceof SimpleTextResponse) {
            return ((SimpleTextResponse) res).getText();
        } else if (res instanceof LoginResponse) {
            return ((LoginResponse) res).getText();
        } else if (res instanceof ErrorMessageResponse) {
            return ((ErrorMessageResponse) res).getErrorMessage();
        } else if (res instanceof NewGameMatchResponse) {
            return ((NewGameMatchResponse) res).getText();
        } else {
            return "Response has no text";
        }
    }

    @Override
    public UserRole getUserRoleFromResponse() {
        Response res = communicator.getResponse();
        if (res instanceof UserRoleResponse) {
            return ((UserRoleResponse) res).getUserRole();
        } else {
            throw new IncorrectResponseTypeException();
        }
    }

    @Override
    public Map<String, String> getTemplateInfoMapFromResponse() {
        Response res = communicator.getResponse();
        if (res instanceof TemplateInfoMapResponse){
            return ((TemplateInfoMapResponse) res).getIdToTitleMap();
        } else {
            throw new IncorrectResponseTypeException();
        }
    }

    @Override
    public boolean isResponseErrorMessage() {
        return communicator.getResponse() instanceof ErrorMessageResponse;
    }


    @Override
    public Map<String, String> getMatchInfoMapFromResponse() {
        Response res = communicator.getResponse();
        if (res instanceof MatchInfoMapResponse) {
            return ((MatchInfoMapResponse) res).getIdToTitle();
        } else {
            throw new IncorrectResponseTypeException();
        }
    }

    @Override
    public String getNewMatchIdFromResponse() {
        Response res = communicator.getResponse();
        if (res instanceof NewGameMatchResponse) {
            return ((NewGameMatchResponse) res).getMatchID();
        } else {
            throw new IncorrectResponseTypeException();
        }
    }


    @Override
    public Map<String, String> getGameInfoMapFromResponse() {
        Response response = communicator.getResponse();
        if (response instanceof GameInfoMapResponse) {
            return ((GameInfoMapResponse) response).getIdToTitle();
        } else {
            throw new IncorrectResponseTypeException();
        }
    }

    @Override
    public Map<String, String> getTemplateAttributeMapFromResponse() {
        Response response = communicator.getResponse();
        if (response instanceof StartTemplateEditResponse) {
            return ((StartTemplateEditResponse) response).getAttributeMap();
        } else {
            throw new IncorrectResponseTypeException();
        }
    }


}
