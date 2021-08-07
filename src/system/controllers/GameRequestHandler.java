package system.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import shared.DTOs.MatchData;
import shared.exceptions.use_case_exceptions.*;
import shared.DTOs.GameData;
import system.use_cases.managers.GameManager;
import system.use_cases.managers.MatchManager;
import system.use_cases.managers.TemplateManager;
import system.use_cases.managers.UserManager;
import java.io.IOException;
import java.io.OutputStream;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameRequestHandler implements HttpHandler {

    private final GameManager gameManager;
    private final TemplateManager templateManager;
    private final UserManager userManager;
    private final MatchManager matchManager;

    public GameRequestHandler(GameManager gameManager,
                              TemplateManager templateManager,
                              UserManager userManager,
                              MatchManager matchManager) {
        this.gameManager = gameManager;
        this.templateManager = templateManager;
        this.userManager = userManager;
        this.matchManager = matchManager;
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

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String specification = exchange.getRequestURI().toString().split("/")[2];

        switch (specification) {
            case "all-public-games":
                handleGetAllPublicGames(exchange);
                break;
            case "all-owned-games":
                handleGetAllOwnedGames(exchange);
                break;
            case "public-owned-games":
                handleGetPublicOwnedGames(exchange);
                break;
            case "available-matches":
                handleGetAllGameMatches(exchange);
                break;
            case "public-games-with-template":
                handleGetPublicGamesByTemplate(exchange);
                break;
            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }

    private void handlePostRequest(HttpExchange exchange) {
        String specification = exchange.getRequestURI().toString().split("/")[2];
        switch (specification) {
            case "create-builder":
                handleCreateBuilder(exchange);
                break;
        }
    }

    private void handleCreateBuilder(HttpExchange exchange) {

    }

    private void handleGetPublicGamesByTemplate(HttpExchange exchange) throws IOException {
        String templateID;
        try {
            String query = exchange.getRequestURI().toURL().getQuery();
            if (query == null) {
                sendResponse(exchange, 400, "Missing Query.");
                return;
            }
            templateID = query.split("=")[1];
        } catch (MalformedURLException e) {
            sendResponse(exchange, 404, "Malformed URL.");
            return;
        }
        sendResponse(exchange, 200, getPublicGameDataByTemplate(templateID));
    }

    private String getPublicGameDataByTemplate(String templateID) {
        Set<String> allPublicGames = gameManager.getAllPublicGamesID();
        Set<GameData> dataSet = new HashSet<>();
        for (String gameID: allPublicGames) {
            if (gameManager.getTemplateID(gameID).equals(templateID)) {
                GameData data = new GameData();
                data.id = gameID;
                data.ownerId = gameManager.getOwnerID(gameID);
                data.title = gameManager.getGameTitle(gameID);
                dataSet.add(data);
            }
        }
        Map<String, Set<GameData>> dataMap = new HashMap<>();
        dataMap.put("data", dataSet);

        Gson gson = new Gson();
        return gson.toJson(dataMap);
    }

    private void handleGetAllGameMatches(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 200, getAvailableGameMatchesData());
    }

    private String getAvailableGameMatchesData() {
        Set<String> preparingMatches = matchManager.getAllPreparingMatchIds();
        Set<MatchData> dataSet = new HashSet<>();
        for (String id: preparingMatches) {
            MatchData data = new MatchData();
            try {
                String gameId = matchManager.getGameIdFromMatch(id);
                data.gameTitle = gameManager.getGameTitle(gameId);
                data.matchId = id;
                data.hostName = matchManager.getHostId(id);
                data.numPlayers = matchManager.getPlayerCount(id);
                data.maxPlayers = matchManager.getPlayerCountLimit(id);
                dataSet.add(data);
            } catch (InvalidMatchIDException e) {
                throw new RuntimeException("The match ID returned from match manager doesn't exist anymore");
            }
        }
        Map<String, Set<MatchData>> dataMap = new HashMap<>();
        dataMap.put("data", dataSet);

        Gson gson = new Gson();
        return gson.toJson(dataMap);
    }


    private void handleGetAllPublicGames(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 200, getAllPublicGamesData());
    }

    private void handleGetAllOwnedGames(HttpExchange exchange) throws IOException {
        String userID;
        try {
            String query = exchange.getRequestURI().toURL().getQuery();
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
            sendResponse(exchange, 200, getOwnedGamesData(userID));
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 400, "Invalid User ID.");
        }
    }

    private void handleGetPublicOwnedGames(HttpExchange exchange) throws IOException {
        String userID;
        try {
            String query = exchange.getRequestURI().toURL().getQuery();
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
            sendResponse(exchange, 200, getPublicOwnedGamesData(userID));
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 400, "Invalid User ID.");
        }
    }

    private String getPublicOwnedGamesData(String userID) throws InvalidUserIDException {
        Set<String> ownedIds = userManager.getOwnedGamesID(userID);
        Set<GameData> dataSet = new HashSet<>();
        for (String id: ownedIds) {
            try {
                if (gameManager.checkIsPublic(id)){
                    GameData data = new GameData();
                    data.ownerId = userID;
                    data.id = id;
                    data.title = gameManager.getGameTitle(id);
                    dataSet.add(data);
                }
            } catch (InvalidGameIDException e) {
                throw new RuntimeException("Fatal Error: The user contains an invalid game ID.");
            }
        }
        Map<String, Set<GameData>> dataMap = new HashMap<>();
        dataMap.put("data", dataSet);

        Gson gson = new Gson();
        return gson.toJson(dataMap);
    }

    private void sendResponse(HttpExchange exchange, int responseCode, String body) throws IOException {
        OutputStream outputStream = exchange.getResponseBody();
        exchange.sendResponseHeaders(responseCode, body.length());
        outputStream.write(body.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private String getOwnedGamesData(String userID) throws InvalidUserIDException {
            Set<String> ownedIds = userManager.getOwnedGamesID(userID);
            Set<GameData> dataSet = new HashSet<>();
            for (String id: ownedIds) {
                GameData data = new GameData();
                data.ownerId = userID;
                data.id = id;
                data.title = gameManager.getGameTitle(id);
                dataSet.add(data);
            }
            Map<String, Set<GameData>> dataMap = new HashMap<>();
            dataMap.put("data", dataSet);

            Gson gson = new Gson();
            return gson.toJson(dataMap);
    }

    private String getAllPublicGamesData() {
        Set<GameData> dataSet = new HashSet<>();
        Set<String> publicGames = gameManager.getAllPublicGamesID();

        for (String id: publicGames) {
            GameData game = new GameData();
            game.id = id;
            game.title = gameManager.getGameTitle(id);
            game.ownerId = gameManager.getOwnerID(id);
            dataSet.add(game);
        }
        Map<String, Set<GameData>> dataMap = new HashMap<>();
        dataMap.put("data", dataSet);

        Gson gson = new Gson();
        return gson.toJson(dataMap);
    }


}
