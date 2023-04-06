package system.use_cases.managers;

import shared.constants.GameAccessLevel;
import shared.constants.GameGenre;
import shared.constants.IDType;
import shared.exceptions.entities_exception.IDNotYetSetException;
import shared.exceptions.entities_exception.UnaccountedEnumException;
import shared.exceptions.use_case_exceptions.*;

import system.entities.template.Template;
import system.entities.game.Game;
import system.gateways.GameDataGateway;
import system.use_cases.builders.GameInteractiveBuilder;
import system.use_cases.factories.GameBuilderFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * GameManager Class
 */
public class GameManager {
    private final Map<String, Game> games;
    private final Map<String, GameInteractiveBuilder> gameBuilders; // mapping of username to builder object
    private final Map<String, Game> temporaryGames;
    private final IdManager idManager;
    private final GameDataGateway gateway;

    /**
     * Constructor of GameManager
     * @param gateway the gateway that communicate with database
     * @throws IOException if there is an issue with input-output
     */
    public GameManager(GameDataGateway gateway) throws IOException {
        games = new HashMap<>();
        gameBuilders = new HashMap<>();
        temporaryGames = new HashMap<>();
        this.gateway = gateway;

        for (Game game : this.gateway.getAllGames()) {
            games.put(game.getID(), game);
        }
        this.idManager = new IdManager(gateway.getGameCount() + 1);
    }

    /**
     * Starts the process of building a game.
     *
     * This method will create an Interactive builder, which can be accessed by user ID. A game is gradually
     * built using this builder.
     *
     * @param creatorID The ID of the creator of this game.
     * @param template  A Template object, which is used to skip some steps in the process of game building.
     * @throws CreationInProgressException Exception is thrown when there is already a game building that correspond
     *                                     to the given user ID.
     */
    public void initiateGameBuilder(String creatorID, Template template) throws CreationInProgressException {
        if (gameBuilders.containsKey(creatorID)) {
            throw new CreationInProgressException();
        }

        GameBuilderFactory factory = new GameBuilderFactory();
        GameInteractiveBuilder builder = factory.getGameBuilder(creatorID, template);

        gameBuilders.put(creatorID, builder);
    }

    /**
     * Returns a human-readable string that asks for a value, which can be used to build the game object.
     *
     * @param creatorID The string identifier of the user that is building this game.
     * @throws NoCreationInProgressException No such user with such ID is building any game.
     */
    public String getDesignQuestion(String creatorID) throws NoCreationInProgressException {

        if (!gameBuilders.containsKey(creatorID)) {
            throw new NoCreationInProgressException();
        }
        return gameBuilders.get(creatorID).getDesignQuestion();
    }

    /**
     * Provides input for the building process of the game that is being built by the user with creatorID.
     *
     * @param creatorID    The string identifier of the user that is building this game.
     * @param designChoice The input for the building process.
     * @throws InvalidInputException The design choice provided is invalid.
     */
    public void makeDesignChoice(String creatorID, String designChoice)
            throws NoCreationInProgressException, InvalidInputException {

        if (!gameBuilders.containsKey(creatorID)) {
            throw new NoCreationInProgressException();
        }

        gameBuilders.get(creatorID).makeDesignChoice(designChoice);
    }

    /**
     * Delete the specified builder.
     *
     * @param creatorID The user ID associated with this builder.
     * @throws NoCreationInProgressException when there is no such builder to be destroyed.
     */
    public void destroyBuilder(String creatorID) throws NoCreationInProgressException {
        if (!gameBuilders.containsKey(creatorID)) {
            throw new NoCreationInProgressException();
        } else {
            gameBuilders.remove(creatorID);
        }
    }

    /**
     * Conclude the building process. Provide the game with and ID and stores it. Remove the CreatorID
     * from the collection of users that are in building process.
     *
     * @param creatorID The string identifier of the user that is building this game.
     * @throws NoCreationInProgressException No such user with such ID is building any game.
     * @throws NotReadyException    Game is not ready to be built. Need more input.
     */
    public String buildGame(String creatorID)
            throws NoCreationInProgressException, NotReadyException {

        if (!gameBuilders.containsKey(creatorID)) {
            throw new NoCreationInProgressException();
        }

        if (!gameBuilders.get(creatorID).isReadyToBuild())
            throw new NotReadyException();

        String id;
        try {
            id = idManager.getNextId();
            Game game = gameBuilders.get(creatorID).build(id);
            games.put(id, game);
            gateway.addGame(game);
        } catch (IDNotYetSetException e) {
            throw new IDNotYetSetException();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        gameBuilders.remove(creatorID);
        return id;
    }

    /**
     * Conclude the building process. Provide the game with and ID and stores it. Remove the CreatorID
     * from the collection of users that are in building process.
     *
     * @param creatorID The string identifier of the user that is building this game.
     * @throws NoCreationInProgressException No such user with such ID is building any game.
     * @throws NotReadyException    Game is not ready to be built. Need more input.
     */
    public String buildTemporaryGame(String creatorID)
            throws NoCreationInProgressException, NotReadyException {

        if (!gameBuilders.containsKey(creatorID)) {
            throw new NoCreationInProgressException();
        }

        if (!gameBuilders.get(creatorID).isReadyToBuild())
            throw new NotReadyException();

        String id;
        try {
            id = idManager.getNextId();
            Game game = gameBuilders.get(creatorID).build(id);
            games.put(id, game);
            temporaryGames.put(id, game);
        } catch (IDNotYetSetException e) {
            throw new IDNotYetSetException();
        }
        gameBuilders.remove(creatorID);
        return id;
    }

    /**
     * Save the temporary game with the given ID to the database.
     * @param gameID the gameID to save
     */
    public void saveTemporaryGame(String gameID) throws InvalidIDException {
        if (!temporaryGames.containsKey(gameID))
            throw new InvalidIDException(IDType.GAME);
        try {
            gateway.addGame(temporaryGames.remove(gameID));
        } catch (IOException e) {
            throw new RuntimeException("System failure: Can't connect to the database");
        }
    }

    /**
     * Returns the Game object with the corresponding ID.
     * @param id The string identifier of the Game
     * @throws InvalidIDException There is no such game in the system.
     */
    public Game getGame(String id) throws InvalidIDException {
        if (!games.containsKey(id))
            throw new InvalidIDException(IDType.GAME);
        return games.get(id);
    }

    /**
     * @return a mapping of all Game Ids to game titles.
     */
    public Set<String> getAllGameIDs() {
        return new HashSet<>(games.keySet());
    }

    /**
     * @return a set of public game ids.
     */
    public Set<String> getAllPublicGamesID() {
        Set<String> publicIDs = new HashSet<>();
        for (String id : games.keySet()) {
            if (games.get(id).isPublic()) {
                publicIDs.add(id);
            }
        }
        return publicIDs;
    }

    /**
     * Set the public status of the game to the specified value.
     *
     * @param gameID          The ID of the game.
     * @param gameAccessLevel The value representing whether the game is public, private, friends only, deleted
     * @throws InvalidIDException when there is an invalid Game ID
     */
    public void setGameAccessLevel(String gameID, GameAccessLevel gameAccessLevel) throws InvalidIDException {
        if (!games.containsKey(gameID)) {
            throw new InvalidIDException(IDType.GAME);
        }
        if (gameAccessLevel != null) {
            games.get(gameID).setGameAccessLevel(gameAccessLevel);
            try {
                gateway.updateGame(games.get(gameID));
                System.out.println(games.get(gameID).getTitle());
                System.out.println(games.get(gameID).getGameAccessLevel().name());
            } catch (IOException e) {
                throw new RuntimeException("Dysfunctional Database.");
            }
        }

    }
    /**
     * Revert the publicity status of the game based on its stored value
     * @param gameID The ID of the game.
     * @throws InvalidIDException when there is an Invalid GameID
     * */
    public void undoSetGameAccessLevel(String gameID) throws InvalidIDException {
        if (!games.containsKey(gameID)) {
            throw new InvalidIDException(IDType.GAME);
        }
        GameAccessLevel currentAC = games.get(gameID).getGameAccessLevel();
        GameAccessLevel prevAC = games.get(gameID).getPreviousGameAccessLevel();
        games.get(gameID).setGameAccessLevel(prevAC);
        games.get(gameID).setPreviousGameAccessLevel(currentAC);
        try {
            gateway.updateGame(games.get(gameID));
            System.out.println(games.get(gameID).getTitle());
            System.out.println(games.get(gameID).getGameAccessLevel().name());
            System.out.println(games.get(gameID).getPreviousGameAccessLevel().name());
        } catch (IOException e) {
            throw new RuntimeException("Dysfunctional Database.");
        }
    }

    /**
     * Returns whether the game is a public game
     * @param gameID The unique string identifier of the game
     * @throws InvalidIDException when there is an invalid GameID
     */
    public boolean checkIsPublic(String gameID) throws InvalidIDException {
        if (!games.containsKey(gameID)) {
            throw new InvalidIDException(IDType.GAME);
        }
        return games.get(gameID).isPublic();
    }

    /**
     * @param gameID the game id
     * @return the genre of the game.
     * @throws InvalidIDException when there is an Invalid GameID
     */
    public GameGenre getGenre(String gameID) throws InvalidIDException {
        if (!games.containsKey(gameID)) {
            throw new InvalidIDException(IDType.GAME);
        }
        return games.get(gameID).getGenre();
    }

    /**
     * Returns the title of a game
     *
     * @param gameID The unique string identifier of the game
     * @throws InvalidIDException when there is an Invalid GameID
     */
    public String getGameTitle(String gameID) throws InvalidIDException {
        if (!games.containsKey(gameID)) {
            throw new InvalidIDException(IDType.GAME);
        }
        return games.get(gameID).getTitle();
    }

    /**
     * Returns the template ID of a game
     *
     * @param gameID The unique string identifier of the game
     * @throws InvalidIDException when there is an Invalid GameID
     */
    public String getTemplateID(String gameID) throws InvalidIDException {
        if (!games.containsKey(gameID)) {
            throw new InvalidIDException(IDType.GAME);
        }
        return games.get(gameID).getTemplateID();
    }

    /**
     * Returns the owner ID of a game
     * @param gameID The unique string identifier of the game
     * @throws InvalidIDException when there is an Invalid GameID
     */
    public String getOwnerID(String gameID) throws InvalidIDException {
        if (!games.containsKey(gameID)) {
            throw new InvalidIDException(IDType.GAME);
        }
        return games.get(gameID).getOwnerId();
    }

    /**
     * get a specified game's access level
     * @param gameID the inputted game id
     * @return the access level of the parameter game id
     * @throws InvalidIDException if gameID is not in the current games list or is null
     */
    public GameAccessLevel getAccessLevel(String gameID) throws InvalidIDException {
        if (!games.containsKey(gameID)) {
            throw new InvalidIDException(IDType.GAME);
        }
        return games.get(gameID).getGameAccessLevel();
    }

    /**
     * get a specified game's previous access level
     * @param gameID the inputted game id
     * @return the access level of the parameter game id
     * @throws InvalidIDException if gameID is not in the current games list or is null
     */
    public GameAccessLevel getPreviousAccessLevel(String gameID) throws InvalidIDException {
        if (!games.containsKey(gameID)) {
            throw new InvalidIDException(IDType.GAME);
        }
        return games.get(gameID).getPreviousGameAccessLevel();
    }

    /**
     * get games owned by a user that has any access level except DELETED
     * @param userID the specified user's id
     * @return a list of userID's owned not deleted games
     */
    public Set<String> getOwnedNotDeletedGameID(String userID) {
        Set<String> ownedNotDeletedGameIDs = new HashSet<>();
        for (String id : games.keySet()) {
            //if owner id match and game is not deleted
            String ownerID = games.get(id).getOwnerId();
            if (ownerID.equals(userID)) {
                if (!games.get(id).getGameAccessLevel().equals(GameAccessLevel.DELETED)) {
                    ownedNotDeletedGameIDs.add(id);
                }
            }
        }
        return ownedNotDeletedGameIDs;
    }

    /**
     * get games owned by a user that has FRIEND only access level
     * @param userID the specified user's id
     * @return a list of userID's owned and has access level of FRIEND
     */
    public Set<String> getOwnedFriendOnlyGameID(String userID) {
        Set<String> friendOnlyGameIDs = new HashSet<>();
        for (String id : games.keySet()) {
            //if owner id match and game is FRIEND only
            String ownerID = games.get(id).getOwnerId();
            if (ownerID.equals(userID)) {
                if (games.get(id).getGameAccessLevel().equals(GameAccessLevel.FRIEND)) {
                    friendOnlyGameIDs.add(id);
                }
            }
        }

        return friendOnlyGameIDs;
    }
}