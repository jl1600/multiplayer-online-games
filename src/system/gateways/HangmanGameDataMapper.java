package system.gateways;

import shared.exceptions.entities_exception.IDAlreadySetException;
import shared.exceptions.use_case_exceptions.*;
import system.entities.game.Game;
import system.entities.game.hangman.HangmanGame;
import system.use_cases.builders.normal_builders.HangmanGameBuilder;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class HangmanGameDataMapper {
    String folderPath = GameDataGateway.gameFolderPath + "hangman/";

    public void addGame(HangmanGame game) throws IOException {
        addGame(game, true);
    }

    public void updateGame(HangmanGame game) throws InvalidGameIDException, IOException {
        deleteGame(game);
        addGame(game, false);
    }

    public void deleteGame(HangmanGame game) throws IOException, InvalidGameIDException {
        File file = new File(folderPath + game.getID() + ".txt");
        if (!file.exists()) {
            throw new InvalidGameIDException();
        }
        if (!file.delete()) {
            throw new IOException();
        }
    }

    public Set<Game> getAllGames() throws IDAlreadySetException {
        File folder = new File(folderPath);
        HashSet<Game> games = new HashSet<>();
        try {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                String gameString = String.join("\n", Files.readAllLines(file.toPath()));
                Game game = hangmanGameFromTxt(gameString);
                games.add(game);
            }
        } catch (InsufficientInputException | CreationInProgressException | IOException e) {
            throw new RuntimeException();
        }
        return games;
    }

    public int getGameCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(GameDataGateway.gameCountFile));
        return new Integer(rd.readLine());
    }


    private void addGame(HangmanGame game, boolean increment) throws IOException {
        File templateFile = new File(folderPath + game.getID() + ".txt");
        Writer wr = new FileWriter(templateFile);
        wr.write(hangmanGameToTxt(game));
        wr.close();

        if (increment) incrementGameCount();
    }

    private void incrementGameCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(GameDataGateway.gameCountFile));
        int count = Integer.parseInt(rd.readLine()) + 1;
        rd.close();

        Writer wr = new FileWriter(GameDataGateway.gameCountFile, false);
        wr.write(count + System.getProperty("line.separator"));
        wr.close();
    }

    String hangmanGameToTxt(HangmanGame g) {
        StringBuilder result = new StringBuilder();

        result.append("@gameId:");
        result.append(g.getID());
        result.append("\n");

        result.append("@templateId:");
        result.append(g.getTemplateID());
        result.append("\n");

        result.append("@ownerId:");
        result.append(g.getOwnerId());
        result.append("\n");

        result.append("@title:");
        result.append(g.getTitle());
        result.append("\n");

        result.append("@isPublic:");
        result.append(g.isPublic().toString());
        result.append("\n");

        result.append("@puzzles:\n");
        for (List<String> puzzle : g.getPuzzles()) {
            result.append(puzzle.get(0));
            result.append("|");
            result.append(puzzle.get(1));
            result.append("\n");
        }
        return result.toString();
    }

    public HangmanGame hangmanGameFromTxt(String gameString) throws InsufficientInputException, CreationInProgressException {
        String[] textData = gameString.split("@gameId:");
        textData = textData[1].split("\n", 2);
        String gameId = textData[0];

        textData = textData[1].split("@templateId:");
        textData = textData[1].split("\n", 2);
        String templateId = textData[0];

        textData = textData[1].split("@ownerId:");
        textData = textData[1].split("\n", 2);
        String ownerId = textData[0];

        textData = textData[1].split("@title:");
        textData = textData[1].split("\n", 2);
        String title = textData[0];

        textData = textData[1].split("@isPublic:");
        textData = textData[1].split("\n", 2);
        boolean isPublic = Boolean.parseBoolean(textData[0]);

        HangmanGameBuilder builder = new HangmanGameBuilder();
        builder.setGameId(gameId);
        builder.setTemplateId(templateId);
        builder.setOwnerId(ownerId);
        builder.setIsPublic(isPublic);
        builder.setTitle(title);

        textData = textData[1].split("@puzzles:\n");
        String[] rawPuzzles = textData[1].split("\n");
        String[] dataRow;
        for (String rawPuzzle : rawPuzzles) {
            dataRow = rawPuzzle.split("\\|");
            builder.addPuzzle(dataRow[0], dataRow[1]);
        }
        return builder.toHangmanGame();
    }
}