package system.gateways;
import shared.exceptions.use_case_exceptions.InvalidIDException;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;
import system.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public interface UserDataGateway {

    void addUser(User user) throws IOException;

    void deleteUser(String userId) throws IOException;

    void updateUser(User user) throws IOException, InvalidUserIDException;

    HashSet<User> getAllUsers() throws IOException;

    int getUserCount() throws IOException;
}
