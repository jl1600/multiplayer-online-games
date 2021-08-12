package system.controllers;

import com.sun.net.httpserver.HttpExchange;
import system.use_cases.managers.UserManager;

import java.io.IOException;

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

            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {

    }
}
