package system.controllers;

import shared.request.Request;
import shared.response.Response;

public interface RequestHandler {
    Response handleRequest(Request request);
}
