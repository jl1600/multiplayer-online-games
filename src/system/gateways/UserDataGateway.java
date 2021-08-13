package system.gateways;
import shared.exceptions.use_case_exceptions.InvalidIDException;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;
import system.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public interface UserDataGateway {
    String path = System.getProperty("user.dir");
    String userFolderPath = path + "/src/system/database/users/";
    File userCountFile = new File(path + "/src/system/database/countFiles/user.txt");

    void addUser(User user) throws IOException;

    void deleteUser(String userId) throws IOException;

    void updateUser(User user) throws IOException, InvalidUserIDException;

    HashSet<User> getAllUsers() throws IOException;

    int getUserCount() throws IOException;
}
