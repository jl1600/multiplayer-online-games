package system.gateways;


import com.google.gson.*;
import shared.exceptions.entities_exception.UnknownGameTypeException;
import shared.exceptions.use_case_exceptions.*;
import system.entities.game.Game;
import system.entities.game.quiz.QuizGame;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.lang.String;

public class QuizGameDataMapper {
    String folderPath = GameDataGateway.gameFolderPath + "quiz/";
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Adds the input QuizGame to the database and increases the total number of games created by 1
     *
     * @param game the Game object to add.
     * @throws IOException              if there is a problem writing ot the file.
     * @throws UnknownGameTypeException if the input Game is not a QuizGame.
     */
    public void addGame(QuizGame game) throws IOException {
        addGame(game, true);
    }

    /**
     * Updates the input QuizGame in the database
     *
     * @param game the QuizGame object to be updated.
     * @throws IOException If there is a problem writing to the file.
     */
    public void updateGame(QuizGame game) throws InvalidGameIDException, IOException {
        deleteGame(game);
        addGame(game, false);
    }

    /**
     * Deletes the input QuizGame from the database.
     *
     * @param game The Game to be deleted.
     * @throws IOException If no corresponding file exists in the database.
     */
    public void deleteGame(QuizGame game) throws IOException, InvalidGameIDException {
        File file = new File(folderPath + game.getID() + ".json");
        if (!file.exists()) {
            throw new InvalidGameIDException();
        }
        if (!file.delete()) {
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
        File folder = new File(folderPath);
        HashSet<Game> games = new HashSet<>();
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            String gameString = String.join("\n", Files.readAllLines(file.toPath()));
            Game game = quizGameFromString(gameString);
            games.add(game);
        }
        return games;
    }

    /**
     * @return number of games ever created. This number does not decrease when a user is deleted
     * @throws IOException if the database is not found
     */
    public int getGameCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(GameDataGateway.gameCountFile));
        return new Integer(rd.readLine());
    }

    private void incrementGameCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(GameDataGateway.gameCountFile));
        int count = Integer.parseInt(rd.readLine()) + 1;
        rd.close();

        Writer wr = new FileWriter(GameDataGateway.gameCountFile, false);
        wr.write(count + System.getProperty("line.separator"));
        wr.close();
    }

    private void addGame(QuizGame game, boolean increment) throws IOException {
        File templateFile = new File(folderPath + game.getID() + ".json");
        Writer wr = new FileWriter(templateFile);
        wr.write(quizGameToString(game));
        wr.close();

        if (increment) incrementGameCount();
    }

    private String quizGameToString(QuizGame game) {
        return gson.toJson(game);
    }

    public QuizGame quizGameFromString(String gameString) {
        return gson.fromJson(gameString, QuizGame.class);
    }
}
