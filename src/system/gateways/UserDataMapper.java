package system.gateways;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import shared.constants.UserRole;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;
import system.entities.User;
import system.entities.game.quiz.QuizGame;

import java.io.*;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserDataMapper implements UserDataGateway {

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    //Gson gson = new Gson();

    /**
     * Adds a user to the database and increases the total number of users created by 1
     * Users are added to the data file in the following format:
     * <id>,<username>,<password>,<role>,{<game-ids-separated-by-|>}
     *
     * @param user user to add to the database
     * @throws IOException if the database is not found
     */
    public void addUser(User user) throws IOException {
        addUser(user, true);
    }

    /**
     * Deletes the user with the specified userId from the database
     *
     * @param userId id of the user to delete
     * @throws IOException if the database is not found
     */
    public void deleteUser(String userId) throws IOException {
        File file = new File(userFolderPath + userId + ".json");
        if (!file.delete()) {
            throw new IOException();
        }
    }

    /**
     * Updates the user in the database
     *
     * @param user user to update
     * @throws IOException            if the database is not found
     * @throws InvalidUserIDException if the user does not exist
     */
    public void updateUser(User user) throws InvalidUserIDException, IOException {
        try {
            deleteUser(user.getUserId());
        } catch (IOException e) {
            throw new InvalidUserIDException();
        }
        addUser(user, false);
    }

    /**
     * @return all User entities in the user database
     * @throws IOException if the database is not found
     */
    public HashSet<User> getAllUsers() throws IOException {
        File folder = new File(userFolderPath);
        HashSet<User> users = new HashSet<>();

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            String userString = String.join("\n", Files.readAllLines(file.toPath()));
            User user = stringToUser(userString);
            users.add(user);
        }

        return users;
    }

    /**
     * @return number of users ever created. This number does not decrease when a user is deleted
     * @throws IOException if the database is not found
     */
    public int getUserCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(userCountFile));
        return new Integer(rd.readLine());
    }

    private User stringToUser(String userString) {
        return gson.fromJson(userString, User.class);
    }

    private String userToString(User user) {
        return gson.toJson(user);
    }


    private void incrementUserCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(userCountFile));
        int count = Integer.parseInt(rd.readLine()) + 1;
        rd.close();

        Writer wr = new FileWriter(userCountFile, false);
        wr.write(count + System.getProperty("line.separator"));
        wr.close();
    }

    private void addUser(User user, boolean increment) throws IOException {
        File userFile = new File(userFolderPath + user.getUserId() + ".json");
        Writer wr = new FileWriter(userFile);
        wr.write(userToString(user));
        wr.close();

        if (increment) incrementUserCount();
    }
}
