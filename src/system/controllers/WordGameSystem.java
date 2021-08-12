package system.controllers;

import com.sun.net.httpserver.HttpServer;
import system.gateways.*;
import system.use_cases.managers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class WordGameSystem {

    private final GameRequestHandler gameRH;
    private final TemplateRequestHandler templateRH;
    private final UserRequestHandler userRH;

    private final HttpServer server;

    public WordGameSystem() throws IOException {

        GameDataGateway gameGateway = new GameDataMapper();
        GameManager gm = new GameManager(gameGateway);

        TemplateDataGateway templateDataGateway = new TemplateDataMapper();
        TemplateManager tm = new TemplateManager(templateDataGateway);

        UserDataGateway userGateway = new UserDataMapper();
        UserManager um = new UserManager(userGateway);

        MatchManager mm = new MatchManager();
        gameRH = new GameRequestHandler(gm, tm, um, mm);
        templateRH = new TemplateRequestHandler(tm, um);
        userRH = new UserRequestHandler(um);

        server = HttpServer.create(new InetSocketAddress("localhost", 4444), 20);

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        server.createContext("/game", gameRH);
        server.createContext("/template", templateRH);
        server.createContext("/user", userRH);
        server.setExecutor(threadPoolExecutor);

    }

    public void run() {
        server.start();
        System.out.println(" Server started on port 4444");
    }

    public static void main(String[] args) {
        try {
            WordGameSystem server = new WordGameSystem();
            server.run();
        } catch (IOException e) {
            throw new RuntimeException("Problem connecting to the database.");
        }
    }
}
