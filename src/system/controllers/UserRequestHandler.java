package system.controllers;

import com.sun.net.httpserver.HttpExchange;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;
import shared.exceptions.use_case_exceptions.InvalidUsernameException;
import system.use_cases.managers.UserManager;

import java.io.IOException;
import java.net.MalformedURLException;

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
            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {

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


}
