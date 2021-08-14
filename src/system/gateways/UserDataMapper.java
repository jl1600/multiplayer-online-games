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
    public void updateUser(User user) throws InvalidUserIDException, IOException {
        try {
            deleteUser(user.getUserId());
        } catch (IOException e) {
            throw new InvalidUserIDException();
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

    private void addUser(User user, boolean increment) throws IOException {
        File userFile = new File(USER_FOLDER + user.getUserId() + SUFFIX);
        Writer wr = new FileWriter(userFile);
        wr.write(userToJson(user));
        wr.close();

        if (increment) {
            incrementUserCount();
        }
    }

    private void incrementUserCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(USER_COUNT_FILE));
        int count = Integer.parseInt(rd.readLine()) + 1;
        rd.close();

        Writer wr = new FileWriter(USER_COUNT_FILE, false);
        wr.write(count + System.getProperty("line.separator"));
        wr.close();
    }

    private User jsonToUser(String userString) {
        return gson.fromJson(userString, User.class);
    }

    private String userToJson(User user) {
        return gson.toJson(user);
    }
    public void banUser(User user, Date bannedUntil) throws IOException {
        File userFile = new File(userFolderPath + user.getUserId() + ".txt");
        Writer wr = new FileWriter(userFile);
        wr.write(userToString(user));
        wr.write(String.valueOf(bannedUntil));
        wr.close();
    }
}
