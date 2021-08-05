package system;

import shared.request.Request;
import shared.request.game_request.GameRequest;
import shared.request.template_request.TemplateRequest;
import shared.request.user_request.UserRequest;
import shared.response.misc.ErrorMessageResponse;
import shared.response.Response;
import shared.response.misc.SimpleTextResponse;
import system.controllers.GameRequestHandler;
import system.controllers.TemplateRequestHandler;
import system.controllers.UserRequestHandler;
import system.gateways.*;
import system.use_cases.managers.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WordGameSystem implements AutoCloseable {

    private final GameRequestHandler gameRH;
    private final TemplateRequestHandler templateRH;
    private final UserRequestHandler userRH;

    private final ServerSocket serverSocket;
    private final ClientSeeker clientSeeker;

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

        serverSocket = new ServerSocket(4444); // this port number should be read from a configuration file.

        clientSeeker = new ClientSeeker();
    }

    private class ClientSeeker extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    RequestRetriever ret = new RequestRetriever(serverSocket.accept());
                    ret.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class RequestRetriever extends Thread {
        private final ObjectOutputStream oos;
        private final ObjectInputStream ois;
        public RequestRetriever(Socket client) {
            try {
                oos = new ObjectOutputStream(client.getOutputStream());
                ois = new ObjectInputStream(client.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException("Cannot get I/O streams of client.");
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Response res = processRequest((Request) ois.readObject());
                    oos.writeObject(res);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("Cannot get I/O streams of client.");
                }
            }
        }
    }

    public void run() {
        clientSeeker.start();
    }


    private Response processRequest(Request request) {
        if (request instanceof GameRequest) {
            return gameRH.handleRequest(request);
        }
        else if (request instanceof TemplateRequest) {
            return templateRH.handleRequest(request);
        }
        else if (request instanceof UserRequest) {
            return userRH.handleRequest(request);
        }
        else return new ErrorMessageResponse(request.getSessionID(), "Error: Unidentified request.");
    }

    @Override
    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
