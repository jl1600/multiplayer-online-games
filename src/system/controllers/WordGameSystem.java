package system.controllers;

import shared.request.Request;
import shared.request.game_request.GameRequest;
import shared.request.template_request.TemplateRequest;
import shared.request.user_request.UserRequest;
import shared.response.misc.ErrorMessageResponse;
import shared.response.Response;
import system.gateways.*;
import system.use_cases.managers.GameManager;
import system.use_cases.managers.MatchManager;
import system.use_cases.managers.TemplateManager;
import system.use_cases.managers.UserManager;

import java.io.IOException;

public class WordGameSystem {

    private int highestSessionID;
    private final GameRequestHandler gameRH;
    private final TemplateRequestHandler templateRH;
    private final UserRequestHandler userRH;

    public WordGameSystem() throws IOException {
        highestSessionID = -1;

        GameDataGateway gameGateway = new GameDataMapper();
        GameManager gm = new GameManager(gameGateway);

        TemplateDataGateway templateDataGateway = new TemplateDataMapper();
        TemplateManager tm = new TemplateManager(templateDataGateway);

        UserDataGateway userGateway = new UserDataMapper();
        UserManager um = new UserManager(userGateway);

        MatchManager mm = new MatchManager();
        gameRH = new GameRequestHandler(gm, tm, um, mm);
        templateRH = new TemplateRequestHandler(tm, um);
        userRH = new UserRequestHandler(um);
    }

    public String connect() {
        highestSessionID += 1;
        return Integer.toString(highestSessionID);
    }

    public Response processRequest(Request request) {
        if (request instanceof GameRequest) {
            return gameRH.handleRequest(request);
        }
        else if (request instanceof TemplateRequest) {
            return templateRH.handleRequest(request);
        }
        else if (request instanceof UserRequest) {
            return userRH.handleRequest(request);
        }
        else return new ErrorMessageResponse(request.getSessionID(), "Error: Unidentified request.");
    }

}
