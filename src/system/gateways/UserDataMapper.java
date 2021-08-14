package system.gateways;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;
import system.entities.User;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class UserDataMapper implements UserDataGateway {
    private final String PATH = System.getProperty("user.dir");
    private final String USER_FOLDER = PATH + "/src/system/database/users/";
    private final File USER_COUNT_FILE = new File(PATH + "/src/system/database/countFiles/user.txt");
    private final String SUFFIX = ".json";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
        File file = new File(USER_FOLDER + userId + SUFFIX);
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
        File folder = new File(USER_FOLDER);
        HashSet<User> users = new HashSet<>();
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.getName().endsWith(SUFFIX)) {
                String userString = String.join("\n", Files.readAllLines(file.toPath()));
                User user = jsonToUser(userString);
                users.add(user);
            }
        }
        return users;
    }

    /**
     * @return number of users ever created. This number does not decrease when a user is deleted
     * @throws IOException if the database is not found
     */
    public int getUserCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(USER_COUNT_FILE));
        return new Integer(rd.readLine());
    }

    private User jsonToUser(String userString) {
        return gson.fromJson(userString, User.class);
    }

    private String userToJson(User user) {
        return gson.toJson(user);
    }

    private void incrementUserCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(USER_COUNT_FILE));
        int count = Integer.parseInt(rd.readLine()) + 1;
        rd.close();

        Writer wr = new FileWriter(USER_COUNT_FILE, false);
        wr.write(count + System.getProperty("line.separator"));
        wr.close();
    }

    private void addUser(User user, boolean increment) throws IOException {
        File userFile = new File(USER_FOLDER + user.getUserId() + SUFFIX);
        Writer wr = new FileWriter(userFile);
        wr.write(userToJson(user));
        wr.close();

        if (increment) incrementUserCount();
    }
}
