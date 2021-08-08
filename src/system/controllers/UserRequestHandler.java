package system.controllers;

import system.use_cases.managers.UserManager;

public class UserRequestHandler {

    /**
     * a user manager that can manipulate all user entities
     */
    private final UserManager userManager;

    /**
     * Constructor for UserRequestHandler class.
     * @param um user manager that contains all user entities and able to make change to them
     */
    public UserRequestHandler(UserManager um) {
        this.userManager = um;
    }

}
