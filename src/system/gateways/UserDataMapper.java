package system.gateways;
import shared.constants.UserRole;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;
import system.entities.User;

import java.io.*;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserDataMapper implements UserDataGateway {
    /**
     * Adds a user to the database and increases the total number of users created by 1
     * Users are added to the data file in the following format:
     * <id>,<username>,<password>,<role>,{<game-ids-separated-by-|>}
     * @param user user to add to the database
     * @throws IOException if the database is not found
     */
    public void addUser(User user) throws IOException {
        addUser(user, true);
    }

    /**
     * Deletes the user with the specified userId from the database
     * @param userId id of the user to delete
     * @throws IOException if the database is not found
     */
    public void deleteUser(String userId) throws IOException {
        File file = new File(userFolderPath + userId + ".txt");
        if (!file.delete()) {
            throw new IOException();
        }
    }

    /**
     * Updates the user in the database
     * @param user user to update
     * @throws IOException if the database is not found
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
            String userString = String.join(",", Files.readAllLines(file.toPath()));
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

    private User stringToUser(String userString){
        String[] userDetails = userString.split(",");
        String userId = userDetails[0].trim();
        String username = userDetails[1].trim();
        String password = userDetails[2].trim();
        String roleString = userDetails[3].trim();

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        Date registerDate = null;
        try{
            registerDate = sdf.parse(userDetails[4].trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        User user = new User(userId, username, password, resolveUserRole(roleString),registerDate);

        String gameCreationString = userDetails[5].trim();
        gameCreationString = gameCreationString.substring(1, gameCreationString.length() - 1);
        for (String gameId : gameCreationString.split("\\|")) {
            user.addGameID(gameId);
        }

        return user;
    }

    private String userToString(User user) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);

        return user.getUserId() + "," +
                user.getUsername() + "," +
                user.getPassword() + "," +
                user.getRole() + "," +
                sdf.format(user.getRegisterDate()) +",{" +
                String.join("|", user.getGameCreationSet()) + "}" +
                System.getProperty("line.separator").replace("{|", "{");
    }

    private UserRole resolveUserRole(String role) {
        if (role.equals(UserRole.MEMBER.name())){
            return UserRole.MEMBER;
        } else if (role.equals(UserRole.ADMIN.name())){
            return UserRole.ADMIN;
        } else {
            return UserRole.TRIAL;
        }
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
        File userFile = new File(userFolderPath + user.getUserId() + ".txt");
        Writer wr = new FileWriter(userFile);
        wr.write(userToString(user));
        wr.close();

        if (increment) incrementUserCount();
    }
}
