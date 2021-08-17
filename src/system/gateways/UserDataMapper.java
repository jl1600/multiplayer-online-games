package system.gateways;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import shared.constants.IDType;
import shared.exceptions.use_case_exceptions.InvalidIDException;
import system.entities.User;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * UserDataMapper Class
 */
public class UserDataMapper implements UserDataGateway {
    private final String PATH = System.getProperty("user.dir");
    private final String USER_FOLDER = PATH + "/src/system/database/users/";
    private final File USER_COUNT_FILE = new File(PATH + "/src/system/database/countFiles/user.txt");
    private final String SUFFIX = ".json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * {@inheritDoc}
     */
    public void addUser(User user) throws IOException {
        addUser(user, true);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteUser(String userId) throws IOException {
        File file = new File(USER_FOLDER + userId + SUFFIX);
        if (!file.delete()) {
            throw new IOException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateUser(User user) throws InvalidIDException, IOException {
        try {
            deleteUser(user.getUserId());
        } catch (IOException e) {
            throw new InvalidIDException(IDType.USER);
        }
        addUser(user, false);
    }

    /**
     * {@inheritDoc}
     */
    public Set<User> getAllUsers() throws IOException {
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
     * {@inheritDoc}
     */
    public int getUserCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(USER_COUNT_FILE));
        return new Integer(rd.readLine());
    }

    /**
     * {@inheritDoc}
     */
    public void incrementUserCount() throws IOException {
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

        if (increment) {
            incrementUserCount();
        }
    }

    private User jsonToUser(String userString) {
        return gson.fromJson(userString, User.class);
    }

    private String userToJson(User user) {
        return gson.toJson(user);
    }

}
