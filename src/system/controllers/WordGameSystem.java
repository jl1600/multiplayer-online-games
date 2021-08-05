package system.controllers;

import shared.request.Request;
import shared.request.game_request.GameRequest;
import shared.request.template_request.TemplateRequest;
import shared.request.user_request.UserRequest;
import shared.response.misc.ErrorMessageResponse;
import shared.response.Response;
import shared.response.misc.SimpleTextResponse;
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
    private final IdManager sessionIdM;
    private final ConcurrentMap<String, Socket> clientSockets;
    private final ConcurrentMap<String, ObjectOutputStream> outStreams;
    private final ConcurrentMap<String, ObjectInputStream> inStreams;
    private final ServerSocket serverSocket;
    private final ClientSeeker clientSeeker;

    public WordGameSystem() throws IOException {
        sessionIdM = new IdManager(0);

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

        clientSockets = new ConcurrentHashMap<>();
        inStreams = new ConcurrentHashMap<>();
        outStreams = new ConcurrentHashMap<>();
        serverSocket = new ServerSocket(4444); // this port number should be read from a configuration file.

        clientSeeker = new ClientSeeker();
    }

    public class ClientSeeker extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    newSession(serverSocket.accept());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void run() {
        clientSeeker.start();
        while(true) {
            processRequests();
        }
    }

    private void newSession(Socket socket) throws IOException {
        String newID = sessionIdM.getNextId();
        clientSockets.put(newID, socket);
        outStreams.put(newID, new ObjectOutputStream(socket.getOutputStream()));
        inStreams.put(newID, new ObjectInputStream(socket.getInputStream()));
        outStreams.get(newID).writeObject(new SimpleTextResponse(newID, "Successfully connected."));
    }

    private void processRequests() {
            for (String sID: inStreams.keySet()) {
                try {
                    Response res = processRequest((Request) inStreams.get(sID).readObject());
                    outStreams.get(sID).writeObject(res);
                } catch (IOException | ClassNotFoundException e) {
                    try {
                        clientSockets.get(sID).getInetAddress().isReachable(5);
                    } catch (IOException ex) {
                        clientSockets.remove(sID);
                    }
                    e.printStackTrace();
                }
            }
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
