package system.controllers;

import com.sun.net.httpserver.HttpServer;
import shared.exceptions.use_case_exceptions.InvalidIDException;
import system.gateways.*;
import system.use_cases.managers.*;
import system.utilities.EmailService;
import system.utilities.PseudoEmailComposer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 三步走
 * 1. 实现房间
 * 1.1 实现用户
 * 1.2 实现房间
 * 2. 实现聊天
 * 3. 实现地图
 * 4. 实现卡
 */



/**
 * WordGameSystem Class
 */
public class WordGameSystem {

    private final HttpServer server;

    /**
     * Constructor of WordGameSystem
     * @throws IOException issue detected regarding input-output
     * @throws InvalidIDException the user id responsible for this exception is not in the userManager's user list
     */
    public WordGameSystem() throws IOException, InvalidIDException {

        // room
        RoomDataGateway roomGateway = new RoomDataMapper();
        RoomManager rm = new RoomManager(roomGateway);

        // game
        GameDataGateway gameGateway = new GameDataMapper();
        GameManager gm = new GameManager(gameGateway);

        // template is completely useless
        TemplateDataGateway templateDataGateway = new TemplateDataMapper();
        TemplateManager tm = new TemplateManager(templateDataGateway);

        // user now has player and host and admin
        UserDataGateway userGateway = new UserDataMapper();
        UserManager um = new UserManager(userGateway);

        //
        MatchManager mm = new MatchManager();

        GameRequestHandler gameRH = new GameRequestHandler(gm, tm, um, mm, rm);
        TemplateRequestHandler templateRH = new TemplateRequestHandler(tm);

        EmailService eService = new PseudoEmailComposer();
        UserRequestHandler userRH = new UserRequestHandler(um, gm, eService);

        server = HttpServer.create(new InetSocketAddress("localhost", 8000), 20);

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        server.createContext("/game", gameRH);
        server.createContext("/template", templateRH);
        server.createContext("/user", userRH);
        server.setExecutor(threadPoolExecutor);

    }

    /**
     * run WorldGameSystem Helper
     */
    public void run() {
        server.start();
        System.out.println(" Server started on port 8000");
    }

    /**
     * main function of WorldGameSystem
     * @param args args
     */
    public static void main(String[] args) {
        try {
            WordGameSystem server = new WordGameSystem();
            server.run();
        } catch (IOException e) {
            throw new RuntimeException("Problem connecting to the database.");
        }  catch (InvalidIDException e) {
            throw new RuntimeException("User ID not found in database");
        }
    }
}
