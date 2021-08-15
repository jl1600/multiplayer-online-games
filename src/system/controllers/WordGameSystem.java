package system.controllers;

import com.sun.net.httpserver.HttpServer;
import shared.exceptions.use_case_exceptions.InvalidUserIDException;
import system.gateways.*;
import system.use_cases.managers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class WordGameSystem {

    private final HttpServer server;

    public WordGameSystem() throws IOException, InvalidUserIDException{

        GameDataGateway gameGateway = new GameDataMapper();
        GameManager gm = new GameManager(gameGateway);

        TemplateDataGateway templateDataGateway = new TemplateDataMapper();
        TemplateManager tm = new TemplateManager(templateDataGateway);

        UserDataGateway userGateway = new UserDataMapper();

        UserManager um = new UserManager(userGateway);

        MatchManager mm = new MatchManager();
        GameRequestHandler gameRH = new GameRequestHandler(gm, tm, um, mm);
        TemplateRequestHandler templateRH = new TemplateRequestHandler(tm, um);
        UserRequestHandler userRH = new UserRequestHandler(um);

        server = HttpServer.create(new InetSocketAddress("localhost", 8000), 20);

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        server.createContext("/game", gameRH);
        server.createContext("/template", templateRH);
        server.createContext("/user", userRH);
        server.setExecutor(threadPoolExecutor);

    }

    public void run() {
        server.start();
        System.out.println(" Server started on port 8000");
    }

    public static void main(String[] args) {
        try {
            WordGameSystem server = new WordGameSystem();
            server.run();
        } catch (IOException e) {
            throw new RuntimeException("Problem connecting to the database.");
        }  catch (InvalidUserIDException e) {
            throw new RuntimeException("User ID not found in database");
        }
    }
}
