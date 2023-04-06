package system.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import shared.constants.UserRole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;

/**
 * Abstract RequestHandler Class
 */
public abstract class RequestHandler implements HttpHandler {

    /**
     * gson only used to convert to json
     */
    protected Gson gson;

    /**
     * Constructor of RequestHandler
     */
    public RequestHandler() {
        gson = new Gson();
    }

    /**
     * handles requests based on cases
     * @param exchange the exchange that contains header and appropriate content used for handling
     * @throws IOException issue detected regarding input-output
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println(exchange.getRequestURI());
        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetRequest(exchange);
                break;
            case "POST":
                handlePostRequest(exchange);
                break;
            default:
                sendResponse(exchange, 404,"Unidentified Request.");
        }
    }

    /**
     * handle get requests
     * @param exchange the exchange that contains header and appropriate content used for handling
     * @throws IOException issue detected regarding input-output
     */
    protected abstract void handleGetRequest(HttpExchange exchange) throws IOException;
    /**
     * handle post requests
     * @param exchange the exchange that contains header and appropriate content used for handling
     * @throws IOException issue detected regarding input-output
     */
    protected abstract void handlePostRequest(HttpExchange exchange) throws IOException;


    /**
     * send response
     * @param exchange the exchange that contains appropriate content used for handling
     * @param responseCode the exchanges response code represent the kind of state is in
     * @param body the response body contents
     * @throws IOException issue detected with input-output
     */
    protected static void sendResponse(HttpExchange exchange, int responseCode, String body) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        if (responseCode != 204) {
            OutputStream outputStream = exchange.getResponseBody();
            exchange.sendResponseHeaders(responseCode, body.length());
            outputStream.write(body.getBytes());
            outputStream.flush();
            outputStream.close();
        } else {
            exchange.sendResponseHeaders(204, -1);
        }
    }

    /**
     *
     * @param exchange the exchange that contains appropriate content used for handling
     * @return the request's body content
     * @throws IOException issue detected with input-output
     */
    protected static String getRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr =  new InputStreamReader(exchange.getRequestBody(),"utf-8");
        BufferedReader br = new BufferedReader(isr);

        int b;
        StringBuilder buf = new StringBuilder();
        while ((b = br.read()) != -1) {
            buf.append((char) b);
        }
        br.close();
        isr.close();
        return buf.toString();
    }

    /**
     *
     * @param exchange the exchange that contains appropriate content used for handling
     * @param userRole the current user's user role
     * @param targetRole the userRole that is desired
     * @return false if failed to match, no permission; true if the two user roles matched, has permission
     * @throws IOException issue detected regarding input-output
     */
    protected static boolean hasPermission(HttpExchange exchange, UserRole userRole, UserRole targetRole) throws IOException {
        if (userRole != targetRole) {
            sendResponse(exchange, 403, "This user doesn't have the permission to perform this command.");
            return false;
        } else {
            return true;
        }
    }

    /**
     * get query argument from GET request
     * @param exchange the exchange that contains appropriate content used for handling
     * @return return query content
     * @throws IOException issue detected regarding input-output
     */
    protected static String getQueryArgFromGET(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                sendResponse(exchange, 400, "Missing Query.");
                return null;
            }
            return query.split("=")[1];
        } catch (MalformedURLException e) {
            sendResponse(exchange, 404, "Malformed URL.");
            return null;
        }
    }
}
