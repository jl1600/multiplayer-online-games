package system.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import shared.DTOs.Requests.CreateTemplateBuilderRequestBody;
import shared.DTOs.Requests.DesignChoiceRequestBody;
import shared.DTOs.Requests.EditTemplateRequestBody;
import shared.DTOs.Responses.DesignQuestionResponseBody;
import shared.DTOs.Responses.GeneralTemplateDataResponseBody;
import shared.DTOs.Responses.TemplateAllAttrsResponseBody;
import shared.constants.GameGenre;
import shared.exceptions.use_case_exceptions.*;
import system.entities.template.QuizTemplate;
import system.use_cases.managers.TemplateManager;
import system.use_cases.managers.UserManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;


public class TemplateRequestHandler extends RequestHandler {

    private final TemplateManager templateManager;

    /**
     * Constructor for TemplateRequestHandler()
     * @param templateManager template manager that contains all templates and able to make change to them
     * @param userManager user manager that contains all user entities and able to make change to them
     */
    public TemplateRequestHandler(TemplateManager templateManager, UserManager userManager) {
        this.templateManager = templateManager;
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        String specification = exchange.getRequestURI().getPath().split("/")[2];
        switch (specification) {
            case "all-templates":
                handleGetAllTemplates(exchange);
                break;
            case "all-attributes":
                handleGetAllAttributes(exchange);
                break;
            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }

    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        String specification = exchange.getRequestURI().toString().split("/")[2];
        switch (specification) {
            case "create-builder":
                handleCreateBuilder(exchange);
                break;
            case "make-design-choice":
                handleMakeDesignChoice(exchange);
                break;
            case "edit":
                handleEdit(exchange);
                break;
            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }

    private void handleEdit(HttpExchange exchange) throws IOException {
        EditTemplateRequestBody body = gson.fromJson(getRequestBody(exchange), EditTemplateRequestBody.class);
        try {
            templateManager.editTemplate(body.attrMap, body.templateID);
            sendResponse(exchange, 204, null);
        } catch (NoSuchAttributeException | InvalidInputException e) {
            sendResponse(exchange, 400, "Invalid attribute name or attribute value.");
        } catch (InvalidTemplateIDException e) {
            sendResponse(exchange, 404, "Invalid template ID.");
        }
    }

    private void handleCreateBuilder(HttpExchange exchange) throws IOException {
        CreateTemplateBuilderRequestBody body =
                gson.fromJson(getRequestBody(exchange), CreateTemplateBuilderRequestBody.class);
        DesignQuestionResponseBody question = new DesignQuestionResponseBody();
        try {
            templateManager.initiateTemplateBuilder(body.userID, body.genre);
            question.designQuestion = templateManager.getDesignQuestion(body.userID);
            sendResponse(exchange, 201, gson.toJson(question));
        } catch (CreationInProgressException e) {
            try {
                question.designQuestion = templateManager.getDesignQuestion(body.userID);
                sendResponse(exchange, 200, gson.toJson(question));
            } catch (NoCreationInProgressException f) {
                throw new RuntimeException("No creation in progress. This should never happen.");
            }
        } catch (NoCreationInProgressException e) {
            throw new RuntimeException("No creation in progress. This should never happen.");
        }
    }

    private void handleMakeDesignChoice(HttpExchange exchange) throws IOException {
        DesignChoiceRequestBody body = gson.fromJson(getRequestBody(exchange), DesignChoiceRequestBody.class);
        try {
            templateManager.makeDesignChoice(body.userID, body.designChoice);
            try {
                templateManager.buildTemplate(body.userID);
                sendResponse(exchange, 201, "Success!");
            } catch (InsufficientInputException e) {
                DesignQuestionResponseBody res = new DesignQuestionResponseBody();
                res.designQuestion = templateManager.getDesignQuestion(body.userID);
                sendResponse(exchange, 200, gson.toJson(res));
            }
        } catch (NoCreationInProgressException e) {
            sendResponse(exchange, 404, "No game builder associated with this user.");
        } catch (InvalidInputException e) {
            sendResponse(exchange, 400, "Invalid Input");
        }
    }
    private void handleGetAllAttributes(HttpExchange exchange) throws IOException {
        String templateID;
        try {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                sendResponse(exchange, 400, "Missing Query.");
                return;
            }
            templateID = query.split("=")[1];
        } catch (MalformedURLException e) {
            sendResponse(exchange, 404, "Malformed URL.");
            return;
        }
        try {
            TemplateAllAttrsResponseBody body = new TemplateAllAttrsResponseBody();
            body.templateID = templateID;
            body.attrMap = templateManager.getAttributeMap(templateID);
            sendResponse(exchange, 200, gson.toJson(body));
        } catch (InvalidTemplateIDException e) {
            sendResponse(exchange, 400, "Invalid Template ID.");
        }

    }

    private void handleGetAllTemplates(HttpExchange exchange) throws IOException {
        Set<String> allIDs = templateManager.getAllTemplateIDs();
        Set<GeneralTemplateDataResponseBody> dataSet = new HashSet<>();

        for (String id: allIDs) {
            try {
                GeneralTemplateDataResponseBody datum = new GeneralTemplateDataResponseBody();
                if (templateManager.getTemplate(id) instanceof QuizTemplate)
                    datum.gameGenre = GameGenre.QUIZ;
                else datum.gameGenre = GameGenre.HANGMAN;
                datum.templateID = id;
                datum.title = templateManager.getTemplateTitle(id);
                dataSet.add(datum);
            } catch (InvalidIDException e) {
                e.printStackTrace();
            }
        }
        sendResponse(exchange, 200, gson.toJson(dataSet));
    }

}
