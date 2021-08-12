package system.controllers;

import com.sun.net.httpserver.HttpExchange;
import shared.DTOs.Requests.LoginRequestBody;
import shared.DTOs.Requests.LogoutRequestBody;
import shared.DTOs.Requests.RegisterRequestBody;
import shared.exceptions.use_case_exceptions.*;
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
            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        RegisterRequestBody body = gson.fromJson(getRequestBody(exchange), RegisterRequestBody.class);
        try {
            userManager.createUser(body.username, body.password, body.role);
            sendResponse(exchange, 204, null);
        } catch (DuplicateUsernameException e) {
            sendResponse(exchange, 403, "Duplicate username.");
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
            sendResponse(exchange, 200, "{\"userID\":\"" + userID+"\"}");
        } catch (InvalidUsernameException | IncorrectPasswordException | ExpiredUserException e) {
            sendResponse(exchange, 400, "User doesn't exist, is expired, or the password is incorrect.");
        } catch (InvalidUserIDException e) {
            throw new RuntimeException("Invalid user ID. This should never happen.");
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
