package system.gateways;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;
import system.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;

public interface UserDataGateway {
    String path = System.getProperty("user.dir");
    String userFolderPath = path + "/src/system/database/users/";
    File userCountFile = new File(path + "/src/system/database/countFiles/user.txt");

    void addUser(User user) throws IOException;
    void deleteUser(String userId) throws IOException;
    void banUser(User user, Date duration) throws IOException;
    void updateUser(User user) throws IOException, InvalidUserIDException;
    HashSet<User> getAllUsers() throws IOException;
    int getUserCount() throws IOException;
}
