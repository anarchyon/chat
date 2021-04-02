package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int PORT = 8189;
    private List<ClientHandler> clients;
    private AuthService authService;
    private static final Logger LOGGER = LogManager.getLogger(Server.class);

    public Server () {
        clients = new ArrayList<>();
        authService = DBAuthService.getDBAuthService();
        ExecutorService executorService = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOGGER.info("Server is Listening");
            while (true) {
                Socket socket = serverSocket.accept();
                LOGGER.info("Client connected");
                new ClientHandler(this, socket, executorService);
            }
        } catch (IOException e) {
            LOGGER.error(e);
//            e.printStackTrace();
        } finally {
            authService.close();
            executorService.shutdown();
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized void broadcastMessage(ClientHandler sender, String message) {
        for (ClientHandler client : clients) {
            if (sender == null){
                client.sendMessage(message);
            } else {
                client.sendMessage(sender.getNick() + message);
            }
        }
    }

    public synchronized void broadcastClientsList() {
        List<String> list = new ArrayList<>();
        for (ClientHandler client : clients) {
            list.add(client.getNick());
        }
        String stringList = "/list" + list.toString();
        broadcastMessage(null, stringList);
    }

    public void privateMessage(ClientHandler sender, String nick, String message) {
        if (sender.getNick().equals(nick)) {
            return;
        }
        for (ClientHandler client : clients) {
            if (client.getNick().equals(nick)) {
                client.sendMessage("(private from)" + sender.getNick() + message);
                sender.sendMessage("(private to)" + client.getNick() + message);
                return;
            }
        }
        sender.sendMessage(nick + " not found in chat");
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler client : clients) {
            if (client.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void subscribe (ClientHandler client) {
        clients.add(client);
        LOGGER.info(client.getNick() + " subscribed");
        broadcastClientsList();
    }

    public synchronized void unsubscribe(ClientHandler client) {
        clients.remove(client);
        LOGGER.info(client.getNick() + " unsubscribed");
        broadcastClientsList();
    }
}
