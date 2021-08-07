package system.controllers;

import shared.constants.UserRole;
import shared.exceptions.use_case_exceptions.*;
import shared.request.Request;
import shared.request.game_request.*;
import shared.response.*;
import shared.response.game.GameInfoMapResponse;
import shared.response.game.NewGameMatchResponse;
import shared.response.misc.ErrorMessageResponse;
import shared.response.misc.SimpleTextResponse;
import system.use_cases.managers.GameManager;
import system.use_cases.managers.MatchManager;
import system.use_cases.managers.TemplateManager;
import system.use_cases.managers.UserManager;

import java.io.IOException;
import java.util.Set;

public class GameRequestHandler implements RequestHandler {

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
    public Response handleRequest(Request request) {

        if (request instanceof NewGameRequest) {
            return handleNewGameRequest((NewGameRequest) request);
        } else if (request instanceof MakeGameDesignChoiceRequest) {
            return handleMakeDesignChoiceRequest((MakeGameDesignChoiceRequest) request);
        } else if (request instanceof PlayGameMoveRequest) {
            return handlePlayMoveRequest((PlayGameMoveRequest) request);
        } else if (request instanceof NewGameMatchRequest) {
            return handleNewMatchRequest((NewGameMatchRequest) request);
        } else if (request instanceof GetAllPublicGamesInfoRequest) {
            return handleGetPublicGamesInfoRequest((GetAllPublicGamesInfoRequest) request);
        } else  if (request instanceof DeleteGameRequest){
            return handleDeleteGameRequest((DeleteGameRequest) request);
        } else  if (request instanceof GetOwnedGameInfoRequest) {
            return handleGetOwnedGameRequest((GetOwnedGameInfoRequest) request);
        } else if (request instanceof SetGamePublicStatusRequest) {
            return handleSetGamePublicStatus((SetGamePublicStatusRequest) request);
        }

        return new ErrorMessageResponse(request.getSessionID(), "Error: unidentified request");
    }

    private Response handleSetGamePublicStatus(SetGamePublicStatusRequest request) {
        try {
            // checking if the request sender actually owns the game
            Set<String> ownedGames = userManager.getOwnedGamesID(request.getSenderID());
            if (!ownedGames.contains(request.getGameID()) &&
                    userManager.getUserRole(request.getSenderID())!= UserRole.ADMIN) {
                return new ErrorMessageResponse(request.getSessionID(), "You don't have the permission to do this.");
            }
            gameManager.setGameAccessLevel(request.getGameID(), request.getGameAccessLevel());
            return new SimpleTextResponse(request.getSessionID(),"Success.");
        } catch (InvalidUserIDException e) {
            return new ErrorMessageResponse(request.getSessionID(),"Error: Invalid user ID");
        } catch (InvalidGameIDException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: Invalid game ID");
        }
    }

    private Response handleGetOwnedGameRequest(GetOwnedGameInfoRequest request) {
        try {
            if (request.getTargetUserID().equals(request.getSenderID())) {
                return new GameInfoMapResponse(request.getSessionID(),
                        gameManager.getAllGameTilesFromIdSet(userManager.getOwnedGamesID(request.getTargetUserID())));
            } else {
                return new GameInfoMapResponse(request.getSessionID(),
                        gameManager.getPublicGameTilesFromIdSet(userManager.getOwnedGamesID(request.getTargetUserID())));
            }
        } catch (InvalidUserIDException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: Invalid user ID");
        } catch (InvalidGameIDException e) {
            throw new RuntimeException("Error: This User contains an invalid game ID");
        }
    }


    private Response handleDeleteGameRequest(DeleteGameRequest request) {
        try {
            gameManager.removeGame(request.getGameID());
            userManager.removeOwnedGameID(request.getSenderID(),request.getGameID());
            return new SimpleTextResponse(request.getSessionID(), "Game successfully deleted");
        } catch (InvalidGameIDException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: Game not found.");
        } catch (InvalidUserIDException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: User not found.");
        } catch (IOException e) {
            throw new RuntimeException();
        }

    }

    private Response handleGetPublicGamesInfoRequest(GetAllPublicGamesInfoRequest request) {
        return new GameInfoMapResponse(request.getSessionID(), gameManager.getAllPublicIdAndTitles());
    }

    private Response handleNewGameRequest(NewGameRequest request) {

        String sender = request.getSenderID();
        String sessionID = request.getSessionID();

        try {
            if (userManager.getUserRole(request.getSenderID()) == UserRole.ADMIN) {
                return new ErrorMessageResponse(request.getSessionID(),
                        "Error: Admins cannot perform this action");
            }
            gameManager.initiateGameBuilder(sender, templateManager.getTemplate(request.getTemplateID()));
        } catch (CreationInProgressException e) {
            return new ErrorMessageResponse(sessionID, "Error: A creation is currently in progress");
        } catch (InvalidIDException e) {
            return new ErrorMessageResponse(sessionID, "Error: Template doesn't exist.");
        } catch (InvalidUserIDException e) {
            return new ErrorMessageResponse(sessionID, "Error: Sender has an invalid user ID.");
        }

        return getDesignQuestion(request);
    }

    private Response handleMakeDesignChoiceRequest(MakeGameDesignChoiceRequest request) {

        String sender = request.getSenderID();
        String sessionID = request.getSessionID();

        try {
            gameManager.makeDesignChoice(sender, request.getUserInput());
            String gameId;
            try {
                gameId = gameManager.buildGame(sender);
            }
            catch (InsufficientInputException e) {
                return getDesignQuestion(request);
            }
            userManager.addOwnedGameID(request.getSenderID(), gameId);
            return new SimpleTextResponse(sessionID, "Game successfully built!");
        } catch (NoCreationInProgressException e1) {
            return new ErrorMessageResponse(sessionID, "Error: No game creating is in progress.");
        } catch (InvalidInputException e2) {
            return new ErrorMessageResponse(sessionID,"Error: Invalid input, please re-enter a different input");
        } catch (InvalidUserIDException e) {
            return new ErrorMessageResponse(sessionID, "Error: Invalid user ID");
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private Response handlePlayMoveRequest(PlayGameMoveRequest request) {
        try {
            matchManager.playGameMove(request.getSenderID(), request.getMatchID(), request.getMove());
            Response res = new SimpleTextResponse(request.getSessionID(),
                    matchManager.getMatchTextContent(request.getSenderID(), request.getMatchID()));
            if (matchManager.checkFinished(request.getMatchID())) {
                matchManager.deleteMatch(request.getMatchID());
            }
            return res;
        } catch (InvalidMatchIDException e) {
            return new ErrorMessageResponse(request.getSessionID(),
                    "Error: The game match doesn't exist");
        } catch (InvalidUserIDException e) {
            return new ErrorMessageResponse(request.getSessionID(),
                    "Error: The game match doesn't contain this player");
        } catch (InvalidInputException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: Invalid input");
        }
    }

    private Response handleNewMatchRequest(NewGameMatchRequest request) {
        try {
            String matchID = matchManager.newMatch(request.getSenderID(),
                    gameManager.getGame(request.getGameID()),
                    templateManager.getTemplate(gameManager.getGame(request.getGameID()).getTemplateID()));
            return new NewGameMatchResponse(request.getSessionID(), matchID,
                    matchManager.getMatchTextContent(request.getSenderID(), matchID));
        } catch (InvalidIDException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: Game ID doesn't exist");
        } catch (InvalidMatchIDException e) {
            return new ErrorMessageResponse(request.getSessionID(),
                    "Error: The game match doesn't exist");
        } catch (InvalidUserIDException e) {
            return new ErrorMessageResponse(request.getSessionID(),
                    "Error: The game match doesn't contain this player");
        }
    }

    private Response getDesignQuestion(GameRequest request) {

        try {
            return new SimpleTextResponse(request.getSessionID(), gameManager.getDesignQuestion(request.getSenderID()));
        } catch (NoCreationInProgressException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: No game creation is in progress.");
        }
    }
}
