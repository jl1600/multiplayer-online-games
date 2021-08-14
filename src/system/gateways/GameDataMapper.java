package system.gateways;


import com.google.gson.*;
import shared.exceptions.entities_exception.UnknownGameTypeException;
import shared.exceptions.use_case_exceptions.*;
import system.entities.game.Game;
import system.entities.game.hangman.HangmanGame;
import system.entities.game.quiz.QuizGame;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.lang.String;

public class GameDataMapper implements GameDataGateway {
    private final String PATH = System.getProperty("user.dir");
    private final String GAME_FOLDER = PATH + "/src/system/database/games/";
    private final File GAME_COUNT_FILE = new File(PATH + "/src/system/database/countFiles/game.txt");
    private final String[] SUBFOLDERS = {"quiz/", "hangman/"};
    private final String SUFFIX = ".json";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Adds the input QuizGame to the database and increases the total number of games created by 1
     *
     * @param game the Game object to add.
     * @throws IOException              if there is a problem writing ot the file.
     * @throws UnknownGameTypeException if the input Game is not a QuizGame.
     */
    public void addGame(Game game) throws IOException {
        addGame(game, true);
    }

    /**
     * Updates the input QuizGame in the database
     *
     * @param game the QuizGame object to be updated.
     * @throws IOException If there is a problem writing to the file.
     */
    public void updateGame(Game game) throws IOException {
        deleteGame(game.getID());
        addGame(game, false);
    }

    /**
     * Deletes the input QuizGame from the database.
     *
     * @param gameID The gameID of the game to be deleted.
     * @throws IOException If no corresponding file exists in the database.
     */
    public void deleteGame(String gameID) throws IOException {
        boolean deleted = false;
        for (String subfolder : SUBFOLDERS) {
            File file = new File(GAME_FOLDER + subfolder + gameID + SUFFIX);
            if (file.delete()) {
                deleted = true;
            }
        }
        if (!deleted) {
            throw new IOException();
        }
    }

    /**
     * Returns a set of all QuizGames in the database.
     *
     * @return a set of all QuizGames in the database.
     * @throws FileNotFoundException If there is an error reading any file.
     */
    public Set<Game> getAllGames() throws IOException {
        HashSet<Game> games = new HashSet<>();
        for (String subfolder : SUBFOLDERS) {
            File folder = new File(GAME_FOLDER + subfolder);
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                if (file.getName().endsWith(SUFFIX)) {
                    String gameString = String.join("\n", Files.readAllLines(file.toPath()));
                    Game game = jsonToGame(gameString, subfolder);
                    games.add(game);
                }
            }
        }
        return games;
    }

    /**
     * @return number of games ever created. This number does not decrease when a user is deleted
     * @throws IOException if the database is not found
     */
    public int getGameCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(GAME_COUNT_FILE));
        return new Integer(rd.readLine());
    }

    private void incrementGameCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(GAME_COUNT_FILE));
        int count = Integer.parseInt(rd.readLine()) + 1;
        rd.close();

        Writer wr = new FileWriter(GAME_COUNT_FILE, false);
        wr.write(count + System.getProperty("line.separator"));
        wr.close();
    }

    private void addGame(Game game, boolean increment) throws IOException {
        String subfolder;
        if (game instanceof QuizGame) {
            subfolder = SUBFOLDERS[0];
        } else if (game instanceof HangmanGame) {
            subfolder = SUBFOLDERS[1];
        } else {
            throw new RuntimeException();
        }

        File gameFile = new File(GAME_FOLDER + subfolder + game.getID() + SUFFIX);
        Writer wr = new FileWriter(gameFile);
        wr.write(gameToJson(game));
        wr.close();

        if (increment) {
            incrementGameCount();
        }
    }

    private String gameToJson(Game game) {
        return gson.toJson(game);
    }

    public Game jsonToGame(String gameString, String subfolder) {
        if (subfolder.equals(SUBFOLDERS[0])) {
            return gson.fromJson(gameString, QuizGame.class);
        } else if (subfolder.equals(SUBFOLDERS[1])) {
            return gson.fromJson(gameString, HangmanGame.class);
        } else {
            throw new RuntimeException();
        }
    }
}
