package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final int PORT = 8189;
    private List<ClientHandler> clients;
    private AuthService authService;

    public Server () {
        clients = new ArrayList<>();
        authService = DBAuthService.getDBAuthService();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            authService.close();
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
        System.out.println(client.getNick() + " subscribed");
        broadcastClientsList();
    }

    public synchronized void unsubscribe(ClientHandler client) {
        clients.remove(client);
        System.out.println(client.getNick() + " unsubscribed");
        broadcastClientsList();
    }
}
