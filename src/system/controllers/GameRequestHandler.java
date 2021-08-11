package system.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import shared.DTOs.Requests.*;
import shared.DTOs.Responses.DesignQuestionResponseBody;
import shared.DTOs.Responses.MatchDataResponseBody;
import shared.exceptions.use_case_exceptions.*;
import shared.DTOs.Responses.GameDataResponseBody;
import system.use_cases.managers.GameManager;
import system.use_cases.managers.MatchManager;
import system.use_cases.managers.TemplateManager;
import system.use_cases.managers.UserManager;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.io.*;

import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class GameRequestHandler implements HttpHandler {

    private final GameManager gameManager;
    private final TemplateManager templateManager;
    private final UserManager userManager;
    private final MatchManager matchManager;
    private final ServerSocket serverSocket;
    private final Gson gson;

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
            case "make-design-choice":
                handleMakeDesignChoice(exchange);
                break;
            case "cancel-builder":
                handleCancelBuilder(exchange);
                break;
            case "create-match":
                handleCreateMatch(exchange);
                break;
            case "join-match":
                handleJoinMatch(exchange);
                break;
            case "leave-match":
                handleLeaveMatch(exchange);
                break;
            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }

    private void handleLeaveMatch(HttpExchange exchange) throws IOException {
        LeaveMatchRequestBody body = gson.fromJson(getRequestBody(exchange), LeaveMatchRequestBody.class);
        try {
            matchManager.removePlayer(body.userID, body.matchID);
            sendResponse(exchange, 204, null);
        } catch (InvalidMatchIDException e) {
            sendResponse(exchange, 404, "Invalid ID.");
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 400, "Match doesn't contain this user.");
        }
    }

    private void handleJoinMatch(HttpExchange exchange) throws IOException {
        JoinMatchRequestBody body = gson.fromJson(getRequestBody(exchange), JoinMatchRequestBody.class);
        try {
            matchManager.addPlayer(body.userID, userManager.getUsername(body.userID), body.matchID);
            acceptPlayerSocket(body.userID, body.matchID);
        } catch (InvalidMatchIDException e) {
            sendResponse(exchange, 403, "Match already started or the given ID is invalid.");
        } catch (DuplicateUserIDException e) {
            sendResponse(exchange, 400, "The user is already in this match.");
        } catch (MaxPlayerReachedException e) {
            sendResponse(exchange, 403, "The max number of players is reached.");
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 404, "The user ID is invalid.");
        }
    }

    private void handleCancelBuilder(HttpExchange exchange) throws IOException {
        CancelBuilderRequestBody body = gson.fromJson(getRequestBody(exchange), CancelBuilderRequestBody.class);
        try {
            gameManager.destroyBuilder(body.userID);
            sendResponse(exchange, 204, null);
        } catch (NoCreationInProgressException e) {
            sendResponse(exchange, 400, "User ID is invalid or no builder is in progress.");
        }
    }

    private void handleMakeDesignChoice(HttpExchange exchange) throws IOException {
        DesignChoiceRequestBody body = gson.fromJson(getRequestBody(exchange), DesignChoiceRequestBody.class);
        try {
            gameManager.makeDesignChoice(body.userID, body.designChoice);
            try {
                gameManager.buildGame(body.userID);
                sendResponse(exchange, 201, "Success!");
            } catch (InsufficientInputException e) {
                DesignQuestionResponseBody res = new DesignQuestionResponseBody();
                res.designQuestion = gameManager.getDesignQuestion(body.userID);
                sendResponse(exchange, 200, gson.toJson(res));
            }
        } catch (NoCreationInProgressException e) {
            sendResponse(exchange, 404, "No game builder associated with this user.");
        } catch (InvalidInputException e) {
            sendResponse(exchange, 400, "Invalid Input");
        }
    }

    private void handleCreateMatch(HttpExchange exchange) throws IOException {

        String data = getRequestBody(exchange);
        CreateMatchRequestBody body  = gson.fromJson(data, CreateMatchRequestBody.class);

        try {
            String templateID = gameManager.getTemplateID(body.gameID);
            String matchID = matchManager.newMatch(body.userID, userManager.getUsername(body.userID),
                                    gameManager.getGame(body.gameID),
                                    templateManager.getTemplate(templateID));
            System.out.println(matchID);
            sendResponse(exchange, 204, null); // Telling the client that match is successfully created.
            acceptPlayerSocket(body.userID, matchID);

        } catch (InvalidUserIDException | InvalidIDException e) {
            sendResponse(exchange, 400, "One of the provided IDs is invalid.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void acceptPlayerSocket(String userID, String matchID) throws IOException, InvalidMatchIDException {
        Socket connection = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        // handShake(newPlayer);
        String playerID = reader.readLine();
        // Refusing connection until it's the correct user ID that we are looking for.
        while (!playerID.equals(userID)) {
            connection.close();
            connection = serverSocket.accept();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            playerID = reader.readLine();
        }
        PlayerInputListener inputListener = new PlayerInputListener(connection, matchManager, matchID, playerID);
        MatchOutputDispatcher outputDispatcher = new MatchOutputDispatcher(matchManager, matchID);
        outputDispatcher.addPlayerOutput(connection);
        matchManager.addObserver(outputDispatcher, matchID);
        inputListener.start();
    }

    //////////////////////////////////////////////////////
    // May need to remove this later
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

    private void handleCreateBuilder(HttpExchange exchange) throws IOException {
        CreateGameBuilderRequestBody body = gson.fromJson(getRequestBody(exchange), CreateGameBuilderRequestBody.class);
        DesignQuestionResponseBody question = new DesignQuestionResponseBody();
        try {
            gameManager.initiateGameBuilder(body.userID, templateManager.getTemplate(body.templateID));
            question.designQuestion = gameManager.getDesignQuestion(body.userID);
            sendResponse(exchange, 201, gson.toJson(question));
        } catch (CreationInProgressException e) {
            try {
                question.designQuestion = gameManager.getDesignQuestion(body.userID);
                sendResponse(exchange, 200, gson.toJson(question));
            } catch (NoCreationInProgressException noCreationInProgressException) {
                throw new RuntimeException("No creation in progress. This should never happen.");
            }
        } catch (InvalidIDException e) {
            sendResponse(exchange, 404, "The provided ID is invalid.");
        } catch (NoCreationInProgressException e) {
            throw new RuntimeException("No creation in progress. This should never happen.");
        }
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
            try {
                if (gameManager.getTemplateID(gameID).equals(templateID)) {
                    GameDataResponseBody data = new GameDataResponseBody();
                    data.id = gameID;
                    data.ownerName = userManager.getUsername(gameManager.getOwnerID(gameID));
                    data.title = gameManager.getGameTitle(gameID);
                    data.accessLevel = gameManager.getAccessLevel(gameID);
                    dataSet.add(data);
                }
            } catch (InvalidGameIDException | InvalidUserIDException e) {
                throw new RuntimeException("Invalid game ID from the list of public games. This should never happen.");
            }
        }

        return gson.toJson(dataSet);
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
            } catch (InvalidGameIDException e) {
                throw new RuntimeException("The game ID got from match is invalid.");
            }
        }

        return gson.toJson(dataSet);
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
                    data.ownerName = userManager.getUsername(userID);
                    data.id = id;
                    data.title = gameManager.getGameTitle(id);
                    dataSet.add(data);
                }
            } catch (InvalidGameIDException e) {
                throw new RuntimeException("Fatal Error: The user contains an invalid game ID.");
            }
        }

        return gson.toJson(dataSet);
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
        br.close();
        isr.close();
        return buf.toString();
    }
    private String getOwnedGamesData(String userID) throws InvalidUserIDException {
            Set<String> ownedIds = userManager.getOwnedGamesID(userID);
            Set<GameDataResponseBody> dataSet = new HashSet<>();
            for (String id: ownedIds) {
                GameDataResponseBody data = new GameDataResponseBody();
                data.ownerName = userManager.getUsername(userID);
                data.id = id;
                try {
                    data.title = gameManager.getGameTitle(id);
                } catch (InvalidGameIDException e) {
                    throw new RuntimeException("Game ID from owned list is invalid.");
                }
                dataSet.add(data);
            }

            return gson.toJson(dataSet);
    }

    private String getAllPublicGamesData() {
        Set<GameDataResponseBody> dataSet = new HashSet<>();
        Set<String> publicGames = gameManager.getAllPublicGamesID();

        for (String id: publicGames) {
            GameDataResponseBody game = new GameDataResponseBody();
            game.id = id;
            try {
                game.title = gameManager.getGameTitle(id);
                game.ownerName = userManager.getUsername(gameManager.getOwnerID(id));
            } catch (InvalidGameIDException | InvalidUserIDException e) {
                throw new RuntimeException("Game ID or user ID got from public game list is invalid.");
            }

            dataSet.add(game);
        }

        return gson.toJson(dataSet);
    }


}
