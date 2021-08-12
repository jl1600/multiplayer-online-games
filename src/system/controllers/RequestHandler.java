package system.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public abstract class RequestHandler implements HttpHandler {

    protected Gson gson;

    public RequestHandler() {
        gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
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

    protected abstract void handleGetRequest(HttpExchange exchange) throws IOException;
    protected abstract void handlePostRequest(HttpExchange exchange) throws IOException;

    protected static void sendResponse(HttpExchange exchange, int responseCode, String body) throws IOException {
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
}
