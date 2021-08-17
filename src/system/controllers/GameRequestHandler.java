package system.controllers;

import com.sun.net.httpserver.HttpExchange;
import shared.DTOs.Requests.*;
import shared.DTOs.Responses.DesignQuestionResponseBody;
import shared.DTOs.Responses.MatchDataResponseBody;
import shared.constants.IDType;
import shared.constants.UserRole;
import shared.exceptions.use_case_exceptions.*;
import shared.DTOs.Responses.GameDataResponseBody;
import system.use_cases.managers.GameManager;
import system.use_cases.managers.MatchManager;
import system.use_cases.managers.TemplateManager;
import system.use_cases.managers.UserManager;

import java.io.*;

import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

/**
 * GameRequestHandlerClass
 */
public class GameRequestHandler extends RequestHandler {

    private final static int PORT = 8888;
    private final GameManager gameManager;
    private final TemplateManager templateManager;
    private final UserManager userManager;
    private final MatchManager matchManager;
    private final ServerSocket serverSocket;

    /**
     * Constructor of GameRequestHandler
     *
     * @param gameManager     the game manager that contains all games and can manipulate them
     * @param templateManager the template manager that contains all template and can manipulate them
     * @param userManager     the user manager that contains user games and can manipulate them
     * @param matchManager    the match manager that contains all match and can manipulate them
     */
    public GameRequestHandler(GameManager gameManager,
                              TemplateManager templateManager,
                              UserManager userManager,
                              MatchManager matchManager) {
        this.gameManager = gameManager;
        this.templateManager = templateManager;
        this.userManager = userManager;
        this.matchManager = matchManager;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create server socket, IO problem.");
        }
    }

    /**
     * handles game related GET requests
     *
     * @param exchange the exchange that contains header and appropriate content used for handling
     * @throws IOException issue detected regarding input-output
     */
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
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
            case "prev-access-level":
                handleGetPrevAccessLevel(exchange);
            case "available-games":
                handleGetAvailableGamesByUserID(exchange);
            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }


    /**
     * handles game related POST requests
     *
     * @param exchange the exchange that contains header and appropriate content used for handling
     * @throws IOException issue detected regarding input-output
     */
    protected void handlePostRequest(HttpExchange exchange) throws IOException {
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
            case "access-level":
                handleAccessLevel(exchange);
                break;
            case "undo-access-level":
                handleUndoAccessLevel(exchange);
                break;
            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }

    private void handleGetPrevAccessLevel(HttpExchange exchange) throws IOException {
        AccessLevelRequestBody body = gson.fromJson(getRequestBody(exchange), AccessLevelRequestBody.class);
        String prevAL;
        try {
            prevAL = gameManager.getPreviousAccessLevel(body.gameID).name();
            sendResponse(exchange, 204, prevAL);
        } catch (InvalidIDException e) {
            sendResponse(exchange, 404, "The game ID is invalid.");
        }
    }

    private void handleUndoAccessLevel(HttpExchange exchange) throws IOException {
        UndoAccessLevelRequestBody body = gson.fromJson(getRequestBody(exchange), UndoAccessLevelRequestBody.class);
        try {
            gameManager.undoSetGameAccessLevel(body.gameID);
            sendResponse(exchange, 204, null);
        } catch (InvalidIDException e) {
            sendResponse(exchange, 404, "The game ID is invalid.");
        }

    }

    private void handleAccessLevel(HttpExchange exchange) throws IOException {
        AccessLevelRequestBody body = gson.fromJson(getRequestBody(exchange), AccessLevelRequestBody.class);
        try {
            gameManager.setGameAccessLevel(body.gameID, body.accessLevel);
            sendResponse(exchange, 204, null);
        } catch (InvalidIDException e) {
            sendResponse(exchange, 404, "The game ID is invalid.");
        }
    }

    private void handleLeaveMatch(HttpExchange exchange) throws IOException {
        LeaveMatchRequestBody body = gson.fromJson(getRequestBody(exchange), LeaveMatchRequestBody.class);
        try {
            matchManager.removePlayer(body.userID, body.matchID);
            sendResponse(exchange, 204, null);
        } catch (InvalidIDException e) {
            if (e.getIDType() == IDType.MATCH)
                sendResponse(exchange, 404, "Invalid Match ID.");
            else if (e.getIDType() == IDType.USER)
                sendResponse(exchange, 400, "Match doesn't contain this user.");
        }
    }

    private void handleJoinMatch(HttpExchange exchange) throws IOException {
        JoinMatchRequestBody body = gson.fromJson(getRequestBody(exchange), JoinMatchRequestBody.class);
        try {
            matchManager.addPlayer(body.userID, userManager.getUsername(body.userID), body.matchID);
            ClientSocketSeeker clientSeeker = new ClientSocketSeeker(matchManager, serverSocket, body.userID, body.matchID);
            clientSeeker.start();
        } catch (InvalidIDException e) {
            if (e.getIDType() == IDType.MATCH)
                sendResponse(exchange, 403, "Match already started or the given ID is invalid.");
            else if (e.getIDType() == IDType.USER)
                sendResponse(exchange, 404, "The user ID is invalid.");
        } catch (DuplicateUserIDException e) {
            sendResponse(exchange, 400, "The user is already in this match.");
        } catch (MaxPlayerReachedException e) {
            sendResponse(exchange, 403, "The max number of players is reached.");
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
                String gameID = (userManager.getUserRole(body.userID) == UserRole.TRIAL) ?
                        gameManager.buildTemporaryGame(body.userID) : gameManager.buildGame(body.userID);
                userManager.addOwnedGameID(body.userID, gameID);
                sendResponse(exchange, 201, "Success!");
            } catch (NotReadyException e) {
                DesignQuestionResponseBody res = new DesignQuestionResponseBody();
                res.designQuestion = gameManager.getDesignQuestion(body.userID);
                sendResponse(exchange, 200, gson.toJson(res));
            } catch (InvalidIDException e) {
                throw new RuntimeException("user id is invalid. This should never happen.");
            }
        } catch (NoCreationInProgressException e) {
            sendResponse(exchange, 404, "No game builder associated with this user.");
        } catch (InvalidInputException e) {
            sendResponse(exchange, 400, "Invalid Input");
        }
    }

    private void handleCreateMatch(HttpExchange exchange) throws IOException {
        String data = getRequestBody(exchange);
        CreateMatchRequestBody body = gson.fromJson(data, CreateMatchRequestBody.class);

        try {
            String templateID = gameManager.getTemplateID(body.gameID);
            String matchID = matchManager.newMatch(body.userID, userManager.getUsername(body.userID),
                    gameManager.getGame(body.gameID),
                    templateManager.getTemplate(templateID));
            sendResponse(exchange, 204, null); // Telling the client that match is successfully created.
            ClientSocketSeeker clientSeeker = new ClientSocketSeeker(matchManager, serverSocket, body.userID, matchID);
            clientSeeker.start();
        } catch (InvalidIDException e) {
            sendResponse(exchange, 400, "One of the provided IDs is invalid.");
        }
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
        String templateID = getQueryArgFromGET(exchange);
        if (templateID == null)
            return;
        sendResponse(exchange, 200, getPublicGameDataByTemplate(templateID));
    }

    private String getPublicGameDataByTemplate(String templateID) {
        Set<String> allPublicGames = gameManager.getAllPublicGamesID();
        Set<GameDataResponseBody> dataSet = new HashSet<>();
        for (String gameID : allPublicGames) {
            try {
                if (gameManager.getTemplateID(gameID).equals(templateID)) {
                    GameDataResponseBody data = new GameDataResponseBody();
                    data.id = gameID;
                    data.ownerName = userManager.getUsername(gameManager.getOwnerID(gameID));
                    data.title = gameManager.getGameTitle(gameID);
                    data.accessLevel = gameManager.getAccessLevel(gameID);
                    data.previousAccessLevel = gameManager.getPreviousAccessLevel(gameID);
                    data.genre = gameManager.getGenre(gameID);
                    dataSet.add(data);
                }
            } catch (InvalidIDException e) {
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
        for (String id : preparingMatches) {
            MatchDataResponseBody data = new MatchDataResponseBody();
            try {
                String gameId = matchManager.getGameIdFromMatch(id);
                data.gameTitle = gameManager.getGameTitle(gameId);
                data.matchId = id;
                data.hostName = matchManager.getHostName(id);
                data.numPlayers = matchManager.getPlayerCount(id);
                data.maxPlayers = matchManager.getPlayerCountLimit(id);
                data.genre = gameManager.getGenre(gameId);
                dataSet.add(data);
            } catch (InvalidIDException e) {
                if (e.getIDType() == IDType.MATCH)
                    throw new RuntimeException("The match ID returned from match manager doesn't exist anymore");
                else if (e.getIDType() == IDType.GAME)
                    throw new RuntimeException("The game ID or host ID got from match is invalid.");
            }
        }

        return gson.toJson(dataSet);
    }


    private void handleGetAllPublicGames(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 200, getAllPublicGamesData());
    }

    private void handleGetAvailableGamesByUserID(HttpExchange exchange) throws IOException {
        String ownerID = getQueryArgFromGET(exchange);
        if (ownerID == null)
            return;
        try {
            sendResponse(exchange, 200, getAvailableGameDataByUserID(ownerID));
        } catch (InvalidIDException e) {
            System.out.println("inv");
            sendResponse(exchange, 400, "Invalid User ID.");
        }

    }

    private void handleGetAllOwnedGames(HttpExchange exchange) throws IOException {
        String userID = getQueryArgFromGET(exchange);
        if (userID == null)
            return;
        try {
            sendResponse(exchange, 200, getOwnedGamesData(userID));
        } catch (InvalidIDException e) {
            sendResponse(exchange, 400, "Invalid User ID.");
        }
    }

    private void handleGetPublicOwnedGames(HttpExchange exchange) throws IOException {
        String userID = getQueryArgFromGET(exchange);
        if (userID == null)
            return;
        try {
            sendResponse(exchange, 200, getPublicOwnedGamesData(userID));
        } catch (InvalidIDException e) {
            sendResponse(exchange, 400, "Invalid User ID.");
        }
    }

    private String getPublicOwnedGamesData(String userID) throws InvalidIDException {
        Set<String> ownedIds = userManager.getOwnedGamesID(userID);
        Set<GameDataResponseBody> dataSet = new HashSet<>();
        for (String id : ownedIds) {
            try {
                if (gameManager.checkIsPublic(id)) {
                    GameDataResponseBody data = new GameDataResponseBody();
                    data.ownerName = userManager.getUsername(userID);
                    data.id = id;
                    data.title = gameManager.getGameTitle(id);
                    data.accessLevel = gameManager.getAccessLevel(id);
                    data.previousAccessLevel = gameManager.getPreviousAccessLevel(id);
                    data.genre = gameManager.getGenre(id);
                    dataSet.add(data);
                }
            } catch (InvalidIDException e) {
                if (e.getIDType() == IDType.GAME)
                    throw new RuntimeException("Fatal Error: The user contains an invalid game ID.");
                else throw new InvalidIDException(IDType.USER);
            }
        }

        return gson.toJson(dataSet);
    }


    private String getOwnedGamesData(String userID) throws InvalidIDException {

        Set<String> ownedIds = userManager.getOwnedGamesID(userID);
        Set<GameDataResponseBody> dataSet = new HashSet<>();
        for (String id : ownedIds) {
            GameDataResponseBody data = new GameDataResponseBody();
            data.ownerName = userManager.getUsername(userID);
            data.id = id;
            try {
                data.title = gameManager.getGameTitle(id);
                data.accessLevel = gameManager.getAccessLevel(id);
                data.previousAccessLevel = gameManager.getPreviousAccessLevel(id);
                data.genre = gameManager.getGenre(id);
            } catch (InvalidIDException e) {
                if (e.getIDType() == IDType.GAME)
                    throw new RuntimeException("Fatal Error: The user contains an invalid game ID.");
                else throw new InvalidIDException(IDType.USER);
            }
            dataSet.add(data);
        }

        return gson.toJson(dataSet);
    }

    private String getAvailableGameDataByUserID(String userID) throws InvalidIDException {
        Set<GameDataResponseBody> dataSet = new HashSet<>();
        //duplicates will be take cared by built in
        Set<String> availableGameIDs = new HashSet<>();

        if (userManager.getUserRole(userID).equals(UserRole.ADMIN)) {
            availableGameIDs = gameManager.getAllGameIDs();
        } else {
            Set<String> userFriendList = userManager.getFriendList(userID);
            //Step 1: get all public games
            availableGameIDs.addAll(gameManager.getAllPublicGamesID());
            //Step 2: get all owned creations that are not DELETED
            availableGameIDs.addAll(gameManager.getOwnedNotDeletedGameID(userID));
            //Step 3: for every friend, get all of their friend only games
            for (String friendID : userFriendList) {
                availableGameIDs.addAll(gameManager.getOwnedFriendOnlyGameID(friendID));
            }
        }

        return getJsonDataFromGameIDs(dataSet, availableGameIDs);
    }

    private String getJsonDataFromGameIDs(Set<GameDataResponseBody> dataSet, Set<String> availableGameIDs) {
        for (String id : availableGameIDs) {
            GameDataResponseBody game = new GameDataResponseBody();
            game.id = id;
            try {
                game.title = gameManager.getGameTitle(id);
                game.ownerName = userManager.getUsername(gameManager.getOwnerID(id));
                game.accessLevel = gameManager.getAccessLevel(id);
                game.previousAccessLevel = gameManager.getPreviousAccessLevel(id);
                game.genre = gameManager.getGenre(id);
            } catch (InvalidIDException e) {
                throw new RuntimeException("Game ID or user ID got from public game list is invalid.");
            }

            dataSet.add(game);
        }
        return gson.toJson(dataSet);
    }


    private String getAllPublicGamesData() {
        Set<GameDataResponseBody> dataSet = new HashSet<>();
        Set<String> publicGames = gameManager.getAllPublicGamesID();
        return getJsonDataFromGameIDs(dataSet, publicGames);
    }
}
