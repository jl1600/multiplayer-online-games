package system.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import shared.DTOs.Requests.CreateMatchRequestBody;
import shared.DTOs.Responses.MatchDataResponseBody;
import shared.exceptions.use_case_exceptions.*;
import shared.DTOs.Responses.GameDataResponseBody;
import system.use_cases.managers.GameManager;
import system.use_cases.managers.MatchManager;
import system.use_cases.managers.TemplateManager;
import system.use_cases.managers.UserManager;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.io.*;

import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameRequestHandler implements HttpHandler {

    private final GameManager gameManager;
    private final TemplateManager templateManager;
    private final UserManager userManager;
    private final MatchManager matchManager;
    private final ServerSocket serverSocket;

    public GameRequestHandler(GameManager gameManager,
                              TemplateManager templateManager,
                              UserManager userManager,
                              MatchManager matchManager) {
        this.gameManager = gameManager;
        this.templateManager = templateManager;
        this.userManager = userManager;
        this.matchManager = matchManager;
        try {
            serverSocket = new ServerSocket(8888);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create server socket, IO problem.");
        }
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
        String specification = exchange.getRequestURI().getPath().split("/")[2];
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

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String specification = exchange.getRequestURI().toString().split("/")[2];
        switch (specification) {
            case "create-builder":
                handleCreateBuilder(exchange);
                break;
            case "create-match":
                handleCreateMatch(exchange);
                break;
            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }

    private void handleCreateMatch(HttpExchange exchange) throws IOException {

        String data = getRequestBody(exchange);
        Gson gson = new Gson();
        CreateMatchRequestBody body  = gson.fromJson(data, CreateMatchRequestBody.class);

        try {
            String templateID = gameManager.getTemplateID(body.gameID);
            String matchID = matchManager.newMatch(body.userID, userManager.getUsername(body.userID),
                                    gameManager.getGame(body.gameID),
                                    templateManager.getTemplate(templateID));
            sendResponse(exchange, 204, null);

            Socket newPlayer = serverSocket.accept();
            handShake(newPlayer);
            BufferedReader reader = new BufferedReader(new InputStreamReader(newPlayer.getInputStream()));
            System.out.println("trying to read the player's ID");
            String playerID = String.valueOf(reader.read());
            System.out.println(playerID);
            PlayerInputListener inputListener = new PlayerInputListener(newPlayer, matchManager, matchID, playerID);
            MatchOutputDispatcher outputDispatcher = new MatchOutputDispatcher(matchManager, matchID);
            outputDispatcher.addPlayerOutput(newPlayer);
            inputListener.start();

        } catch (InvalidUserIDException | InvalidIDException e) {
            sendResponse(exchange, 400, "One of the provided IDs is invalid.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handShake(Socket client) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        reader.readLine();
        reader.readLine();
        String key = reader.readLine().split(" ")[1];
        PrintWriter printWriter = new PrintWriter(client.getOutputStream());
        printWriter.println("HTTP/1.1 101 Switching Protocols");
        printWriter.println("Upgrade: websocket");
        printWriter.println("Connection: Upgrade");
        printWriter.println("Sec-WebSocket-Accept: " + encode(key));
        printWriter.println();
        printWriter.flush();
    }
    private String encode(String key) throws Exception {
        key += "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        byte[] bytes = MessageDigest.getInstance("SHA-1").digest(key.getBytes());
        return DatatypeConverter.printBase64Binary(bytes);
    }

    private static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result +=
                    Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
    // NOT IMPLEMENTED
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
        Set<GameDataResponseBody> dataSet = new HashSet<>();
        for (String gameID: allPublicGames) {
            if (gameManager.getTemplateID(gameID).equals(templateID)) {
                GameDataResponseBody data = new GameDataResponseBody();
                data.id = gameID;
                data.ownerId = gameManager.getOwnerID(gameID);
                data.title = gameManager.getGameTitle(gameID);
                dataSet.add(data);
            }
        }
        Map<String, Set<GameDataResponseBody>> dataMap = new HashMap<>();
        dataMap.put("data", dataSet);

        Gson gson = new Gson();
        return gson.toJson(dataMap);
    }

    private void handleGetAllGameMatches(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 200, getAvailableGameMatchesData());
    }

    private String getAvailableGameMatchesData() {
        Set<String> preparingMatches = matchManager.getAllPreparingMatchIds();
        Set<MatchDataResponseBody> dataSet = new HashSet<>();
        for (String id: preparingMatches) {
            MatchDataResponseBody data = new MatchDataResponseBody();
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
        Map<String, Set<MatchDataResponseBody>> dataMap = new HashMap<>();
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
        Set<GameDataResponseBody> dataSet = new HashSet<>();
        for (String id: ownedIds) {
            try {
                if (gameManager.checkIsPublic(id)){
                    GameDataResponseBody data = new GameDataResponseBody();
                    data.ownerId = userID;
                    data.id = id;
                    data.title = gameManager.getGameTitle(id);
                    dataSet.add(data);
                }
            } catch (InvalidGameIDException e) {
                throw new RuntimeException("Fatal Error: The user contains an invalid game ID.");
            }
        }
        Map<String, Set<GameDataResponseBody>> dataMap = new HashMap<>();
        dataMap.put("data", dataSet);

        Gson gson = new Gson();
        return gson.toJson(dataMap);
    }

    private void sendResponse(HttpExchange exchange, int responseCode, String body) throws IOException {
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

    private String getRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr =  new InputStreamReader(exchange.getRequestBody(),"utf-8");
        BufferedReader br = new BufferedReader(isr);

        int b;
        StringBuilder buf = new StringBuilder();
        while ((b = br.read()) != -1) {
            buf.append((char) b);
        }
        buf.delete(0, 8); // Eliminating the {"data":
        buf.deleteCharAt(buf.length() - 1); // Eliminating the last }
        br.close();
        isr.close();
        return buf.toString();
    }
    private String getOwnedGamesData(String userID) throws InvalidUserIDException {
            Set<String> ownedIds = userManager.getOwnedGamesID(userID);
            Set<GameDataResponseBody> dataSet = new HashSet<>();
            for (String id: ownedIds) {
                GameDataResponseBody data = new GameDataResponseBody();
                data.ownerId = userID;
                data.id = id;
                data.title = gameManager.getGameTitle(id);
                dataSet.add(data);
            }
            Map<String, Set<GameDataResponseBody>> dataMap = new HashMap<>();
            dataMap.put("data", dataSet);

            Gson gson = new Gson();
            return gson.toJson(dataMap);
    }

    private String getAllPublicGamesData() {
        Set<GameDataResponseBody> dataSet = new HashSet<>();
        Set<String> publicGames = gameManager.getAllPublicGamesID();

        for (String id: publicGames) {
            GameDataResponseBody game = new GameDataResponseBody();
            game.id = id;
            game.title = gameManager.getGameTitle(id);
            game.ownerId = gameManager.getOwnerID(id);
            dataSet.add(game);
        }
        Map<String, Set<GameDataResponseBody>> dataMap = new HashMap<>();
        dataMap.put("data", dataSet);

        Gson gson = new Gson();
        return gson.toJson(dataMap);
    }


}
