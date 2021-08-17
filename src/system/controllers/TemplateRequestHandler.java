package system.controllers;

import com.sun.net.httpserver.HttpExchange;
import shared.DTOs.Requests.CreateTemplateRequestBody;
import shared.DTOs.Requests.EditTemplateRequestBody;
import shared.DTOs.Responses.GeneralTemplateDataResponseBody;
import shared.DTOs.Responses.TemplateAllAttrsResponseBody;
import shared.constants.GameGenre;
import shared.exceptions.use_case_exceptions.*;
import system.use_cases.managers.TemplateManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *  TemplateRequestHandler Class
 */
public class TemplateRequestHandler extends RequestHandler {

    private final TemplateManager templateManager;
    /**
     * Constructor for TemplateRequestHandler()
     * @param templateManager template manager that contains all templates and able to make change to them
     */
    public TemplateRequestHandler(TemplateManager templateManager) {
        this.templateManager = templateManager;
    }

    /**
     * handle GET requests of template related requests
     * @param exchange the exchange that contains header and appropriate content used for handling
     * @throws IOException issue detected regarding input-output
     */
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
            case "default-attr-map":
                handleGetDefaultAttrMap(exchange);
                break;
            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }

    /**
     * handle POST request related to templates
     * @param exchange the exchange that contains header and appropriate content used for handling
     * @throws IOException issue detected regarding input-output
     */
    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        String specification = exchange.getRequestURI().toString().split("/")[2];
        switch (specification) {
            case "edit":
                handleEdit(exchange);
                break;
            case "create":
                handleCreate(exchange);
                break;
            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        CreateTemplateRequestBody body = gson.fromJson(getRequestBody(exchange), CreateTemplateRequestBody.class);
        try {
            templateManager.createTemplate(body.attrMap, body.genre);
            sendResponse(exchange, 201, "Success!");
        } catch (InvalidInputException e) {
            sendResponse(exchange, 400, "Invalid attribute name or attribute value.");
        }
    }

    private void handleGetDefaultAttrMap(HttpExchange exchange) throws IOException {
        String genre = getQueryArgFromGET(exchange);
        if (genre == null)
            return;
        TemplateAllAttrsResponseBody res = new TemplateAllAttrsResponseBody();
        try {
            res.attrMap = templateManager.getDefaultAttrMap(GameGenre.valueOf(genre));
            sendResponse(exchange, 200, gson.toJson(res));
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "Genre is invalid.");
        }

    }

    private void handleEdit(HttpExchange exchange) throws IOException {
        EditTemplateRequestBody body = gson.fromJson(getRequestBody(exchange), EditTemplateRequestBody.class);
        try {
            System.out.println(body.attrMap);
            templateManager.editTemplate(body.attrMap, body.templateID);
            sendResponse(exchange, 204, null);
        } catch (InvalidInputException e) {
            sendResponse(exchange, 400, "Invalid attribute name or attribute value.");
        } catch (InvalidIDException e) {
            sendResponse(exchange, 404, "Invalid template ID.");
        }
    }

    private void handleGetAllAttributes(HttpExchange exchange) throws IOException {
        String templateID = getQueryArgFromGET(exchange);
        if (templateID == null)
            return;
        try {
            TemplateAllAttrsResponseBody body = new TemplateAllAttrsResponseBody();
            body.templateID = templateID;
            body.attrMap = templateManager.getAttributeMap(templateID);
            sendResponse(exchange, 200, gson.toJson(body));
        } catch (InvalidIDException e) {
            sendResponse(exchange, 400, "Invalid Template ID.");
        }
    }

    private void handleGetAllTemplates(HttpExchange exchange) throws IOException {
        Set<String> allIDs = templateManager.getAllTemplateIDs();
        Set<GeneralTemplateDataResponseBody> dataSet = new HashSet<>();

        for (String id: allIDs) {
            try {
                GeneralTemplateDataResponseBody datum = new GeneralTemplateDataResponseBody();
                datum.gameGenre = templateManager.getGenre(id);
                datum.templateID = id;
                datum.title = templateManager.getTemplateTitle(id);
                dataSet.add(datum);
            } catch (InvalidIDException e) {
               throw new RuntimeException("System failure: The template ID got from template manager is invalid");
            }
        }
        sendResponse(exchange, 200, gson.toJson(dataSet));
    }

}
