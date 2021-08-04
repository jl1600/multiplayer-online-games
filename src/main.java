import client.CommandPromptUIApp;
import system.controllers.WordGameSystem;

import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {
        WordGameSystem system = new WordGameSystem();
        CommandPromptUIApp client = new CommandPromptUIApp(system);
        client.startup();
    }
}
