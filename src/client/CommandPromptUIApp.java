package client;


import client.controllers.*;
import client.gateways.IServerCommunicator;
import client.gateways.ServerSocketCommunicator;
import client.presenters.CommandPromptPresenter;
import client.presenters.IClientPresenter;
import shared.constants.UserRole;
import shared.exceptions.entities_exception.UnaccountedUserRoleException;
import shared.exceptions.use_case_exceptions.IncorrectResponseTypeException;
import system.controllers.WordGameSystem;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandPromptUIApp {

    private final IClientController controller;
    private final IClientPresenter presenter;
    private UserRole currentUserRole;

    /**
     * Constructor for Command-Prompt User Interface
     * @param system the system that process given request
     */
    public CommandPromptUIApp(String hostAddress, int port) {
        IServerCommunicator communicator = new ServerSocketCommunicator(hostAddress, port);
        controller = new ClientController(communicator);
        presenter = new CommandPromptPresenter(communicator);

    }

    /**
     *  Run the application
     */
    public void startup() {
        onStartup();
    }

    private void cls() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private String getInput() {
        Scanner scan = new Scanner(System.in);
        return scan.nextLine();
    }

    private String getInput(String prompt) {
        System.out.println(prompt);
        return getInput();
    }

    private void printMessage(String message) {

        System.out.println(message);
        System.out.println("Press Enter to continue.");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onStartup() {
        cls();
        System.out.println("Welcome!");
        System.out.println("1. Log in");
        System.out.println("2. Sign up");
        System.out.println("3. Trial");
        System.out.println("4. Exit Program");

        String choice = getInput("Please enter (1/2/3/4): ");

        boolean invalidInput = false;
        switch (choice) {
            case "1":
                onLogin();
                break;
            case "2":
                onRegister();
                break;
            case "3":
                onTrial();
                break;
            case "4":
                System.exit(0);
                break;
            default:
                printMessage("Error: Invalid Choice.");
                onStartup(); //invalid choice, method call itself to repeat
        }
    }

    private void onTrial() {
        cls();

        controller.sendRegisterTrialUserRequest();

        if (presenter.isResponseErrorMessage()) {
            printMessage(presenter.getTextFromResponse());
            onStartup();
        } else {
            currentUserRole = UserRole.TRIAL;
            onMainMenu();
        }
    }

    private void onLogin() {
        cls();

        String username = getInput("Please enter username: ");
        String password = getInput("Please enter the password:");

        controller.sendLoginRequest(username,password);

        if (presenter.isResponseErrorMessage()) {
            printMessage(presenter.getTextFromResponse());
            onStartup();
        } else {
            controller.sendGetUserRoleRequest();
            try {
                currentUserRole = presenter.getUserRoleFromResponse();
            } catch (IncorrectResponseTypeException e) {
                e.printStackTrace();
            }
            onMainMenu();
        }
    }

    private void onRegister() {
        cls();

        String username = getInput("Please enter username: ");
        String password = getInput("Please enter the password:");
        String confirmedPassword = getInput("Please re-enter the password:");
        UserRole role = getUserRoleInput();

        if (!password.equals(confirmedPassword)) {
            printMessage("Error: Passwords does not match.");
            onRegister(); // repeat
        } else {

            if (role == UserRole.MEMBER) {
                controller.sendRegisterNormalUserRequest(username, password);
            } else {
                controller.sendRegisterAdminUserRequest(username, password);
            }

            if (presenter.isResponseErrorMessage()) {
                printMessage(presenter.getTextFromResponse());
                onStartup();
            } else if (role == UserRole.ADMIN) {
                printMessage(presenter.getTextFromResponse());
                currentUserRole = role;
                onAdminMainMenu();
            } else if (role == UserRole.MEMBER) {
                printMessage(presenter.getTextFromResponse());
                currentUserRole = role;
                onNormalMainMenu();
            } else {
                printMessage(presenter.getTextFromResponse());
                currentUserRole = role;
                onTrialMainMenu();
            }

        }
    }


    // Helper for onRegister
    private UserRole getUserRoleInput() {

        String userTypeStr = getInput("Please enter user type (admin/member):");

        switch (userTypeStr) {
            case "admin":
                return UserRole.ADMIN;
            case "member":
                return UserRole.MEMBER;
            default:
                return getUserRoleInput();
        }

    }

    // MAIN MENU
    private void onMainMenu() {

        switch (currentUserRole) {
            case ADMIN:
                onAdminMainMenu();
                break;
            case TRIAL:
                onTrialMainMenu();
                break;
            case MEMBER:
                onNormalMainMenu();
                break;
            default:
                throw new UnaccountedUserRoleException();
        }
    }

    private void onAdminMainMenu() {
        cls();

        System.out.println("MAIN MENU");
        System.out.println("1. Create template");
        System.out.println("2. Edit template");
        System.out.println("3. Edit Profile");
        System.out.println("4. Logout");

        String choice = getInput("Please enter (1/2/3/4): ");

        switch (choice){
            case "1":
                onNewTemplate();
                break;
            case "2":
                onEditTemplate();
                break;
            case "3":
                onEditProfile();
                break;
            case "4":
                onLogOut();
                break;
            default:
                printMessage("Error: Invalid Choice.");
                onAdminMainMenu();
        }
    }

    private void onTrialMainMenu() {
        cls();

        System.out.println("MAIN MENU");
        System.out.println("1. Join an existing match");
        System.out.println("2. Start a new match");
        System.out.println("3. Design a new game");
        System.out.println("4. Edit your game");
        System.out.println("5. Delete your game");
        System.out.println("6. Upgrade to a normal account (for free!)");
        System.out.println("7. Log out"); // although these look like the normal main menu, some things are handled
                                          // differently. for example, the trial user is deleted upon logout

        String choice = getInput("Please enter (1/2/3/4/5/6/7): ");

        switch (choice){
            case "1":
                onJoinGameMatch();
                break;
            case "2":
                onNewGameMatch();
                break;
            case "3":
                onNewGame();
                break;
            case "4":
                onEditGame();
                break;
            case "5":
                onDeleteGame();
                break;
            case "6":
                onPromoteTrialUser();
                break;
            case "7":
                onLogOut();
                onDeleteSelf();
                break;
            default:
                printMessage("Error: Invalid Choice.");
                onTrialMainMenu();
        }
    }

    private void onNormalMainMenu() {
        cls();

        System.out.println("MAIN MENU");
        System.out.println("1. Join an existing match");
        System.out.println("2. Start a new match");
        System.out.println("3. Design a new game");
        System.out.println("4. Edit your game");
        System.out.println("5. Delete your game");
        System.out.println("6. Edit your profile");
        System.out.println("7. Log out");

        String choice = getInput("Please enter (1/2/3/4/5/6/7): ");

        switch (choice){
            case "1":
                onJoinGameMatch();
                break;
            case "2":
                onNewGameMatch();
                break;
            case "3":
                onNewGame();
                break;
            case "4":
                onEditGame();
                break;
            case "5":
                onDeleteGame();
                break;
            case "6":
                onEditProfile();
                break;
            case "7":
                onLogOut();
                break;
            default:
                printMessage("Error: Invalid Choice.");
                onNormalMainMenu();
        }
    }

    private void onJoinGameMatch() {
        cls();
        printMessage("Feature is under development for phase2.");
        onMainMenu();
    }

    private String getMatchChoice() {

        cls();

        controller.sendGetAllMatchInfoRequest();
        Map<String, String> matchesInfo = presenter.getMatchInfoMapFromResponse();

        System.out.println("List of all matches available to you:\n");
        for (String id: matchesInfo.keySet()) {
            System.out.println("ID:" + id + " - " + matchesInfo.get(id));
        }

        String chosenID = getInput("Please enter one of the IDs above or enter 'back'/'b' to go back.");
        if (chosenID.equals("back") | chosenID.equals("b")) {
            onMainMenu();
        }
        if (!matchesInfo.containsKey(chosenID)) {
            printMessage("Error: Invalid input.");
            return getMatchChoice();
        }
        else {
            return chosenID;
        }
    }

    private void onNewGameMatch() {
        cls();
        System.out.println("NEW MATCH");
        System.out.println("0. Back\n"+
                "1. Create a match from one of all the public games\n" +
                "2. From games designed by you\n" +
                "3. From public games designed by specified user (Phase 2)\n" +
                "4. From games of specified template (phase 2)");
        System.out.println("Please enter (0/1/2/3/4): ");
        String choice = getInput();
        String gameID = null;
        switch (choice){
            case "0":
                onMainMenu();
            case "1":
                gameID = getPublicGameChoice();
                break;
            case "2":
                gameID = getSelfOwnedGameChoice();
                break;
            case "3":
                onNewGameMatchByUser();
                break;
            case "4":
               onNewGameMatchByTemplate();
               break;
            default:
                printMessage("Error: Invalid Choice.");
                onNewGameMatch();

        }

        if(gameID == null){
            printMessage("Error: Uninitialized game ID.");
            onMainMenu();
        }
        controller.sendNewGameMatchRequest(gameID);

        if (!presenter.isResponseErrorMessage()) {
            String matchID = presenter.getNewMatchIdFromResponse();

            while (!presenter.getTextFromResponse().equals("Error: The game match doesn't exist")) {
                System.out.println(presenter.getTextFromResponse());
                controller.sendPlayGameMoveRequest(matchID, getInput());
            }

        }
        else {
            printMessage(presenter.getTextFromResponse());
        }

        onMainMenu();

    }

    private void onNewGameMatchByTemplate() {
        printMessage("Feature is under development.");
        onMainMenu();
    }

    private void onNewGameMatchByUser() {
        printMessage("Feature is under development.");
        onMainMenu();
    }

    private void onNewGameMatchByOwned() {
    }

    private void onNewGameMatchByPublic() {




    }


    private String getPublicGameChoice() {
        cls();

        controller.sendGetAllGameInfoRequest();
        Map<String, String> gamesInfo = presenter.getGameInfoMapFromResponse();

        System.out.println("List of all public games available to you:\n");
        for (String id: gamesInfo.keySet()) {
            System.out.println(id + " - " + gamesInfo.get(id));
        }

        String chosenID = getInput("Please enter one of the numbers above or enter 'back'/'b' to go back.");
        if (chosenID.equals("back") | chosenID.equals("b")) {
            onNewGameMatch();
        }
        if (!gamesInfo.containsKey(chosenID)) {
            printMessage("Error: Invalid input.");
            return getPublicGameChoice();
        }
        else {
            return chosenID;
        }
    }

    private void onNewGame() {
        cls();

        String chosenTemplate = getTemplateChoice();

        controller.sendNewGameRequest(chosenTemplate);
        if (presenter.isResponseErrorMessage()) {
            System.out.println(presenter.getTextFromResponse());
            System.out.println("Continuing previous game creation...");
        }

        while (!presenter.getTextFromResponse().equals("Game successfully built!")) {
            System.out.println(presenter.getTextFromResponse());
            controller.sendMakeGameDesignChoiceRequest(getInput());
        }

        printMessage(presenter.getTextFromResponse());
        if (currentUserRole == UserRole.MEMBER) {
            onNormalMainMenu();
        } else {
            onTrialMainMenu();
        }
    }

    // Helper for onNewGame
    private String getTemplateChoice() {
        cls();

        controller.sendGetAllTemplateInfoRequest();
        Map<String, String> templatesInfo = presenter.getTemplateInfoMapFromResponse();

        System.out.println("List of all templates currently in the system:\n");
        for (String id: templatesInfo.keySet()) {
            System.out.println(id + " - " + templatesInfo.get(id));
        }

        String chosenID = getInput("Please enter one of the numbers above or enter 'back'/'b' to go back.");
        if (chosenID.equals("back") | chosenID.equals("b")) {
            onMainMenu();
        }
        if (!templatesInfo.containsKey(chosenID)) {
            printMessage("Error: Invalid input.");
            return getTemplateChoice();
        }
        else {
            return chosenID;
        }
    }
    // ---------------------------------------------

    //super menu: onMainMenu
    private void onEditGame() {
        cls();
        String gameChoice = getSelfOwnedGameChoice();

        String input = "";
        while(!input.equals("public") && !input.equals("private"))
        {
            input = getInput("Set public status to (public/private): ");
        }
        controller.sendSetGamePublicStatusRequest(gameChoice, input.equals("public"));
        printMessage(presenter.getTextFromResponse());
        onMainMenu();
    }


    private void onDeleteGame() {
        cls();
        String gameChoice = getSelfOwnedGameChoice(); //the id of the chosen game
        controller.sendDeleteGameRequest(gameChoice);

        printMessage(presenter.getTextFromResponse());
        onMainMenu();
    }
    // Helper
    private String getSelfOwnedGameChoice() {
        cls();

        controller.sendGetSelfOwnedGameInfoRequest();
        Map<String, String> gamesInfo = presenter.getGameInfoMapFromResponse();

        System.out.println("List of all games created by you:\n");
        for (String id: gamesInfo.keySet()) {
            System.out.println("ID: " + id + " - " + gamesInfo.get(id));
        }

        String chosenID = getInput("Please enter one of the numbers above or enter 'back'/'b' to go back.");
        if (chosenID.equals("back") | chosenID.equals("b")) {
            onMainMenu();
        }
        if (!gamesInfo.containsKey(chosenID)) {
            printMessage("Error: Invalid input.");
            return getSelfOwnedGameChoice();
        }
        else {
            return chosenID;
        }
    }

    private void onEditProfile() {
        cls();
        System.out.println("EDIT PROFILE");

        System.out.println("0. Back");
        System.out.println("1. Edit username");
        System.out.println("2. Edit password");
        System.out.println("3. Delete self");


        String choice = getInput("Please enter (0/1/2/3): ");

        switch(choice){
            case "0":
                onMainMenu();
                break;
            case "1":
                onEditUsername();
                break;
            case "2":
                onEditPassword();
                break;
            case "3":
                onDeleteSelf();
                break;
            default:
                printMessage("Error: Invalid Choice.");
                onEditProfile();

        }

    }
    private void onEditUsername() {
        cls();
        if (currentUserRole != UserRole.TRIAL) {
            controller.sendEditUserNameRequest(getInput("Please enter new username"));
            printMessage(presenter.getTextFromResponse());
        } else {
            printMessage("Error: Inappropriate user role.");
        }
        onEditProfile();
    }

    private void onEditPassword() {
        cls();
        if (currentUserRole != UserRole.TRIAL) {
            controller.sendEditPasswordRequest(
                    getInput("Please enter old password"),
                    getInput("Please enter new password")
            );
            printMessage(presenter.getTextFromResponse());
        } else {
            printMessage("Error: Inappropriate user role. ");
        }
        onEditProfile();
    }

    private void onDeleteSelf() {
        cls();

        if(currentUserRole == UserRole.TRIAL){
            controller.sendDeleteUserRequest(null);
        }else{
            controller.sendDeleteUserRequest(getInput("Please enter your password"));
        }

        printMessage(presenter.getTextFromResponse());
        if (presenter.isResponseErrorMessage()) {
            onEditProfile();
        } else {
            currentUserRole = null;
            onStartup();
        }
    }

    private void onNewTemplate() {
        cls();

        controller.sendNewTemplateRequest();

        if (presenter.isResponseErrorMessage()) {
            System.out.println(presenter.getTextFromResponse() + " Continuing previous template creation...");
        }

        //while the message is not this Error thing, keep pumping for more input
        while (!presenter.getTextFromResponse().equals("Error: No template creating is in progress.")) {
            if (presenter.getTextFromResponse().equals("Template successfully built!")){
                System.out.println(presenter.getTextFromResponse());
                break;
            }
            System.out.println(presenter.getTextFromResponse());
            controller.sendMakeTemplateDesignChoiceRequest(getInput());
        }

        onAdminMainMenu();
    }


    private void onEditTemplate() {
        cls();

        // choosing from list of all templates
        String templateID = getTemplateChoice();
        controller.sendStartTemplateEditRequest(templateID);
        if (presenter.isResponseErrorMessage()) {
            printMessage(presenter.getTextFromResponse());
            onMainMenu();
        } else {
            System.out.println("Current attributes of this template are:");
            Map<String, String> attrMap = presenter.getTemplateAttributeMapFromResponse();
            Map<String, String> indexedAttrNames = new HashMap<>();
            int i = 0;
            for(String attr: attrMap.keySet()) {
                i++;
                System.out.println(i + ": " + attr + ": " + attrMap.get(attr));
                indexedAttrNames.put(Integer.toString(i), attr);
            }
            String choice = getInput("Type \"save\" to save this template. Type \"back\" or \"b\" to go back." +
                    "Type one of the numbers above to edit the corresponding attribute");
            while (!choice.equals("back") && !choice.equals("b") && !choice.equals("save")) {
                if (!indexedAttrNames.containsKey(choice)) {
                    System.out.println("Please enter a valid input.");
                } else {
                    controller.sendEditTemplateAttributeRequest(templateID, indexedAttrNames.get(choice),
                            getInput(indexedAttrNames.get(choice) + ": "));
                }
                choice = getInput("Type \"save\" to save this template. Type \"back\" or \"b\" to go back." +
                        "Type one of the numbers above to edit the corresponding attribute");
            }
            if (choice.equals("back") || choice.equals("b")) {
                controller.sendCancelTemplateEditRequest(templateID);

            } else  {
                controller.sendSaveTemplateEditRequest(templateID);
                printMessage(presenter.getTextFromResponse());
            }
            onMainMenu();
        }
    }

    private void onDeleteTemplate(String templateChoice) {
        cls();

        controller.sendDeleteTemplateRequest(templateChoice);
        printMessage(presenter.getTextFromResponse());

        onEditTemplate();
    }

    private void onPromoteTrialUser() {
        String username = getInput("Please enter username");
        String password = getInput("Please enter password");
        controller.sendPromoteTrialUserRequest(username,password);

        printMessage(presenter.getTextFromResponse());
        if (!presenter.isResponseErrorMessage()) {
            controller.sendGetUserRoleRequest();
            currentUserRole = presenter.getUserRoleFromResponse();
        }
        onMainMenu();
    }


    private void onLogOut() {
        if(currentUserRole == UserRole.TRIAL){
            onDeleteSelf();
        } else{
            controller.sendLogoutRequest();
        }
        currentUserRole = null;
        onStartup();
    }

}
