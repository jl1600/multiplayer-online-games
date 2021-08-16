package shared.DTOs.Requests;

import shared.constants.UserRole;

public class RegisterRequestBody {
    public String username;
    public String password;
    public String email;
    public UserRole role;
}
