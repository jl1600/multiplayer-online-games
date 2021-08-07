package system.gateways;


import shared.constants.GameAccessLevel;
import shared.constants.UserRole;
import shared.exceptions.entities_exception.IDAlreadySetException;
import shared.exceptions.entities_exception.UnknownGameTypeException;
import shared.exceptions.use_case_exceptions.*;
import system.entities.game.Game;
import system.entities.game.quiz.QuizAnswer;
import system.entities.game.quiz.QuizGame;
import system.entities.game.quiz.QuizQuestion;
import system.use_cases.builders.normal_builders.QuizGameBuilder;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.lang.String;
import java.lang.StringBuilder;

public class QuizGameDataMapper {
    String folderPath = GameDataGateway.gameFolderPath + "quiz/";

    /**
     * Adds the input QuizGame to the database and increases the total number of games created by 1
     *
     * @param game the Game object to add.
     * @throws IOException if there is a problem writing ot the file.
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
        File file = new File(folderPath + game.getID() + ".txt");
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

        try {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                String gameString = String.join("\n", Files.readAllLines(file.toPath()));
                Game game = quizGameFromString(gameString);
                games.add(game);
            }
        } catch (InsufficientInputException | CreationInProgressException e) {
            throw new RuntimeException();
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
        File templateFile = new File(folderPath + game.getID() + ".txt");
        Writer wr = new FileWriter(templateFile);
        wr.write(quizGameToString(game));
        wr.close();

        if (increment) incrementGameCount();
    }

    private String quizGameToString(QuizGame g) {
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

        result.append("@maxAttempts:");
        result.append(g.getMaxAttempts());
        result.append("\n");

        result.append("@isPublic:");
        result.append(g.getGameAccessLevel().toString());
        result.append("\n");

        // Score Categories
        result.append("@scoreCategories\n");
        HashMap<String, QuizGame.ScoreCategory> scoreCategoryData = g.getScoreCategoryData();
        ArrayList<String> categoriesInOrder = new ArrayList<>();
        for (String sc : scoreCategoryData.keySet()) {
            categoriesInOrder.add(sc);
            result.append(scoreCategoryData.get(sc).dataString());
            result.append("\n");
        }
        result.append("@scoreCategories\n");

        // Questions
        result.append("@questions\n");
        ArrayList<QuizQuestion> questionData = g.getQuestionData();
        result.append(this.quizQuestionsToString(questionData, categoriesInOrder));
        result.append("@questions\n\n");
        return result.toString();
    }

    private String quizQuestionsToString(ArrayList<QuizQuestion> questionData, ArrayList<String> categoriesInOrder) {
        StringBuilder result = new StringBuilder();
        for (QuizQuestion quizQuestion : questionData) {
            // The Question text
            result.append(quizQuestion.getQuestionData());
            result.append("|");

            // Answer text in format {Score1, Score2, ... ScoreN}AnswerString
            ArrayList<QuizAnswer> answerData = quizQuestion.getAnswerData();
            result.append(quizAnswersToString(answerData, categoriesInOrder));

            result.append("\n");
        }
        return result.toString();
    }

    private String quizAnswersToString(ArrayList<QuizAnswer> answerData, ArrayList<String> categoriesInOrder) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        for (QuizAnswer ans : answerData) {
            result.append("{");
            int j = 0;
            for (String category : categoriesInOrder) {
                HashMap<String, Double> scoreData = ans.getScoresRewardsByCategory();
                result.append(scoreData.get(category));
                if (j < categoriesInOrder.size() - 1) {
                    result.append(",");
                }
                j++;
            }
            result.append("},");
            result.append(ans.toString());
            if (i < answerData.size() - 1) {
                result.append("|");
            }
            i++;
        }
        return result.toString();
    }

    public QuizGame quizGameFromString(String gameString) throws IDAlreadySetException, InsufficientInputException, CreationInProgressException {
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

        textData = textData[1].split("@maxAttempts:");
        textData = textData[1].split("\n", 2);
        int maxAttempts = Integer.parseInt(textData[0]);

        textData = textData[1].split("@isPublic:");
        textData = textData[1].split("\n", 2);
        String gameAccessLevelString = textData[0];
        GameAccessLevel gameAccessLevel = resolveGameAccessLevel(gameAccessLevelString);

        QuizGameBuilder quizGameBuilder = new QuizGameBuilder();
        quizGameBuilder.setId(gameId);
        quizGameBuilder.setTemplateId(templateId);
        quizGameBuilder.setOwnerId(ownerId);
        quizGameBuilder.setTitle(title);
        quizGameBuilder.setMaxAttempts(maxAttempts);
        quizGameBuilder.setGameAccessLevel(gameAccessLevel);

        // Score Categories
        textData = textData[1].split("@scoreCategories\n");
        String[] rawCategories = textData[1].split("\n");

        ArrayList<String> categoryNames = new ArrayList<>();
        String[] categoryData;
        double weight;
        String name;
        double minAcceptable;
        String categoryMessage;
        for (String category : rawCategories) {
            categoryData = category.split("\\|");
            weight = Double.parseDouble(categoryData[0]);
            name = categoryData[1];
            categoryNames.add(name);
            minAcceptable = Double.parseDouble(categoryData[2]);
            categoryMessage = categoryData[3];
            quizGameBuilder.addScoreCategory(name, weight, minAcceptable, categoryMessage);
        }

        // Questions
        textData = textData[2].split("@questions\n");
        String[] rawQuestions = textData[1].split("\n");
        for (String questionString : rawQuestions) {
            QuizQuestion questionResult = quizQuestionFromString(questionString, categoryNames);
            quizGameBuilder.addQuestion(questionResult);
        }
        return quizGameBuilder.toQuizGame();

    }

    private GameAccessLevel resolveGameAccessLevel(String gameAccessLevelString) {
        if (gameAccessLevelString.equals(GameAccessLevel.PUBLIC.name())){
            return GameAccessLevel.PUBLIC;
        } else if (gameAccessLevelString.equals(GameAccessLevel.PRIVATE.name())){
            return GameAccessLevel.PRIVATE;
        } else if (gameAccessLevelString.equals(GameAccessLevel.FRIEND.name())){
            return GameAccessLevel.FRIEND;
        } else{
            return GameAccessLevel.DELETED;
        }
    }

    private QuizQuestion quizQuestionFromString(String questionString, ArrayList<String> categoryNames) {
        String[] questionData = questionString.split("\\|");
        String questionText = questionData[0];

        // Parse the answers and their scores
        ArrayList<QuizAnswer> answers = new ArrayList<>();
        for (int a = 1; a < questionData.length; a++) {

            String[] data1 = questionData[a].split("},");
            String[] scores = data1[0].replace("{", "").split(",");

            HashMap<String, Double> answerScoreRewards = new HashMap<>();

            for (int i = 0; i < scores.length; i++) {
                answerScoreRewards.put(categoryNames.get(i), Double.parseDouble(scores[i]));
            }
            String answerText = data1[1];
            QuizAnswer answerResult = new QuizAnswer(answerText, answerScoreRewards);
            answers.add(answerResult);
        }
        return new QuizQuestion(questionText, answers);
    }
}
