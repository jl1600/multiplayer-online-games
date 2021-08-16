package system.use_cases.managers;

import shared.constants.GameAccessLevel;
import shared.constants.GameGenre;
import shared.exceptions.entities_exception.IDNotYetSetException;
import shared.exceptions.use_case_exceptions.*;

import system.entities.game.quiz.QuizGame;
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

public class GameManager {
    private final Map<String, Game> games;
    private final Map<String, GameInteractiveBuilder> gameBuilders; // mapping of username to builder object
    private final IdManager idManager;
    private final GameDataGateway gateway;

    public GameManager(GameDataGateway gateway) throws IOException {
        games = new HashMap<>();
        gameBuilders = new HashMap<>();
        this.gateway = gateway;

        for (Game game: this.gateway.getAllGames()) {
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
     * @param template A Template object, which is used to skip some steps in the process of game building.
     * @throws CreationInProgressException Exception is thrown when there is already a game building that correspond
     * to the given user ID.
     * */
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
     * */
    public String getDesignQuestion(String creatorID) throws NoCreationInProgressException {

        if (!gameBuilders.containsKey(creatorID)) {
            throw new NoCreationInProgressException();
        }
        return gameBuilders.get(creatorID).getDesignQuestion();
    }

    /**
     * Provides input for the building process of the game that is being built by the user with creatorID.
     *
     * @param creatorID The string identifier of the user that is building this game.
     * @param designChoice The input for the building process.
     *
     * @throws InvalidInputException The design choice provided is invalid.
     * */
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
     * */
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
     * @throws InsufficientInputException Game is not ready to be built. Need more input.
     * */
    public String buildGame(String creatorID)
            throws NoCreationInProgressException, InsufficientInputException {

        if (!gameBuilders.containsKey(creatorID)) {
            throw new NoCreationInProgressException();
        }

        if (!gameBuilders.get(creatorID).isReadyToBuild())
            throw new InsufficientInputException();

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
     * @throws InsufficientInputException Game is not ready to be built. Need more input.
     * */
    public String buildTemporaryGame(String creatorID)
            throws NoCreationInProgressException, InsufficientInputException {

        if (!gameBuilders.containsKey(creatorID)) {
            throw new NoCreationInProgressException();
        }

        if (!gameBuilders.get(creatorID).isReadyToBuild())
            throw new InsufficientInputException();

        String id;
        try {
            id = idManager.getNextId();
            Game game = gameBuilders.get(creatorID).build(id);
            games.put(id, game);
        } catch (IDNotYetSetException e) {
            throw new IDNotYetSetException();
        }
        gameBuilders.remove(creatorID);
        return id;
    }

    /**
     * Removes the game from the system and database.
     *
     * @param gameId The String identifier of the Game.
     * @throws InvalidGameIDException There is no such a game in the system.
     * */
    public void removeGame(String gameId) throws InvalidGameIDException {
        if (games.containsKey(gameId)) {
            try {
                gateway.deleteGame(gameId);
            } catch (IOException e) {
                throw new RuntimeException("Cannot delete game from the database");
            }
            games.remove(gameId);
        } else {
            throw new InvalidGameIDException();
        }
    }

    /**
     * Returns the Game object with the corresponding ID.
     *
     * @param id The string identifier of the Game
     * @throws InvalidIDException There is no such game in the system.
     * */
    public Game getGame(String id) throws InvalidIDException {
        if (!games.containsKey(id))
            throw new InvalidIDException();
        return games.get(id);
    }

    /**
     * Returns a mapping of all Game Ids to game titles.
     * */
    public Set<String> getAllGameIDs() {
        return new HashSet<>(games.keySet());
    }

    /**
     * Returns a set of public game ids.
     * */
    public Set<String> getAllPublicGamesID() {
        Set<String> publicIDs = new HashSet<>();
        for (String id: games.keySet()) {
            if (games.get(id).isPublic()) {
                publicIDs.add(id);
            }
        }
        return publicIDs;
    }

    /**
     * Set the public status of the game to the specified value.
     * @param gameID The ID of the game.
     * @param gameAccessLevel The value representing whether the game is public, private, friends only, deleted
     * */
    public void setGameAccessLevel(String gameID, GameAccessLevel gameAccessLevel) throws InvalidGameIDException {
        System.out.println("setGML");
        if(!games.containsKey(gameID)) {
            throw new InvalidGameIDException();
        }
        if (gameAccessLevel != null){
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

    public void undoSetGameAccessLevel(String gameID) throws InvalidGameIDException {
        System.out.println("undoGML");
        if(!games.containsKey(gameID)) {
            throw new InvalidGameIDException();
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
     * */
    public boolean checkIsPublic(String gameID) throws InvalidGameIDException {
        if(!games.containsKey(gameID)) {
            throw new InvalidGameIDException();
        }
        return games.get(gameID).isPublic();
    }

    /**
     * Returns the genre of the game.
     * */
    public GameGenre getGenre(String gameID) throws InvalidGameIDException {
        if(!games.containsKey(gameID)) {
            throw new InvalidGameIDException();
        }
        if (games.get(gameID) instanceof QuizGame) {
            return GameGenre.QUIZ;
        } else return GameGenre.HANGMAN;
    }

    /**
     * Returns the title of a game
     * @param gameID The unique string identifier of the game
     * */
    public String getGameTitle(String gameID) throws InvalidGameIDException {
        if(!games.containsKey(gameID)) {
            throw new InvalidGameIDException();
        }
        return games.get(gameID).getTitle();
    }

    /**
     * Returns the template ID of a game
     * @param gameID The unique string identifier of the game
     * */
    public String getTemplateID(String gameID) throws InvalidGameIDException {
        if(!games.containsKey(gameID)) {
            throw new InvalidGameIDException();
        }
        return games.get(gameID).getTemplateID();
    }

    /**
     * Returns the owner ID of a game
     * @param gameID The unique string identifier of the game
     * */
    public String getOwnerID(String gameID) throws InvalidGameIDException {
        if(!games.containsKey(gameID)) {
            throw new InvalidGameIDException();
        }
        return games.get(gameID).getOwnerId();
    }

    public GameAccessLevel getAccessLevel(String gameID) throws InvalidGameIDException {
        if(!games.containsKey(gameID)) {
            throw new InvalidGameIDException();
        }
        return games.get(gameID).getGameAccessLevel();
    }

    public GameAccessLevel getPreviousAccessLevel(String gameID) throws InvalidGameIDException {
        if(!games.containsKey(gameID)) {
            throw new InvalidGameIDException();
        }
        return games.get(gameID).getPreviousGameAccessLevel();
    }

    public Set<String> getOwnedNotDeletedGameID(String userID){
        Set<String> ownedNotDeletedGameIDs = new HashSet<>();
        for (String id: games.keySet()) {
            //if owner id match and game is not deleted
            if ( games.get(id).getOwnerId() == userID) {
                if (games.get(id).getGameAccessLevel() != GameAccessLevel.DELETED){
                    ownedNotDeletedGameIDs.add(id);
                }
            }
        }
        return ownedNotDeletedGameIDs;
    }

    public Set<String> getOwnedFriendOnlyGameID(String userID){
        Set<String> friendOnlyGameIDs = new HashSet<>();

        for (String id: games.keySet()) {
            //if owner id match and game is FRIEND only
            if ( games.get(id).getOwnerId() == userID &&
                    (games.get(id).getGameAccessLevel() == GameAccessLevel.FRIEND)) {
                friendOnlyGameIDs.add(id);
            }
        }

        return friendOnlyGameIDs;
    }
}