package org.example;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class Client {
    private final int PORT = 8189;
    private final String INET_ADDRESS = "localhost";

    public static final String SYSTEM_FLAG_DISCONNECT = "/end";
    public static final String SYSTEM_FLAG_AUTHORIZATION = "/auth";
    public static final String SYSTEM_FLAG_PRIVATE_MESSAGE = "/w";
    public static final int AUTHORIZATION_OK = 0;
    public static final int AUTHORIZATION_BAD = 1;
    public static final int AUTHORIZATION_BUSY = 2;
    public static final int AUTHORIZATION_TIMEOUT = 3;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String login;
    private String nick;

    private boolean isConnectionOk;
    private Callback<Integer> getAuthStatus;
    private Callback<String> callOnMsgReceived;
    private Callback<String> callOnChangeClientList;
    private Callback<String> callChangeNick;
    private Callback<String> callErrors;

    private HistoryWriter historyWriter;

    public void connect() {
        try {
            socket = new Socket(INET_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            isConnectionOk = true;
            Thread authAndReading = new Thread(() -> {
                try {
                    authentication();
                    setHistory();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            authAndReading.start();
        } catch (IOException e) {
            System.out.println("Unable to connect to server");
            isConnectionOk = false;
        }
    }

    public void authentication() throws IOException {
        while (true) {
            String answer = in.readUTF();
            if (answer.startsWith("/authok")) {
                String[] tokens = answer.split("\\s");
                login = tokens[1];
                nick = tokens[2];
                getAuthStatus.callback(AUTHORIZATION_OK);
                break;
            } else if (answer.equals("/authbad")) {
                System.out.println("Invalid login data");
                getAuthStatus.callback(AUTHORIZATION_BAD);
            } else if (answer.equals("/authbusy")) {
                System.out.println("Client is logged in already");
                getAuthStatus.callback(AUTHORIZATION_BUSY);
            } else if (answer.equals("/authtimeout")) {
                System.out.println("Connection broken by timeout");
                getAuthStatus.callback(AUTHORIZATION_TIMEOUT);
                closeConnection();
            }
        }
    }

    private void setHistory() throws IOException {
        historyWriter = new HistoryWriter(login);
        List<String> messages = historyWriter.readHistory();
        messages.forEach(msg -> callOnMsgReceived.callback(msg + "\n"));
    }

    private void readMessages() {
        try {
            while (true) {
                String incomingMessage = in.readUTF();
                if (incomingMessage.startsWith("/list")) {
                    callOnChangeClientList.callback(incomingMessage.replaceFirst("/list", ""));
                } else if (incomingMessage.startsWith("/changeok")) {
                    String[] tokens = incomingMessage.split("\\s");
                    nick = tokens[1];
                    callChangeNick.callback(nick);
                } else if (incomingMessage.equals("/nickbusy")) {
                    callErrors.callback("Ник занят");
                } else if(incomingMessage.equals("/nickerror")) {
                    callErrors.callback("Ошибка при смене ника");
                } else {
                    callOnMsgReceived.callback(incomingMessage + "\n");
                    historyWriter.writeHistory(incomingMessage);
                }
            }
        } catch (IOException e) {
            System.out.println("Socket was closed");
        }
        closeConnection();
    }

    public void sendMessage(String message) {
        try {
            if (!socket.isClosed()) {
                out.writeUTF(message);
                if (message.equalsIgnoreCase(SYSTEM_FLAG_DISCONNECT)) {
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return nick;
    }

    public String getLogin() {
        return login;
    }

    public void closeConnection() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void setCallOnMsgReceived(Callback<String> callOnMsgReceived) {
        this.callOnMsgReceived = callOnMsgReceived;
    }

    public void setCallOnChangeClientList(Callback<String> callOnChangeClientList) {
        this.callOnChangeClientList = callOnChangeClientList;
    }

    public void setGetAuthStatus(Callback<Integer> getAuthStatus) {
        this.getAuthStatus = getAuthStatus;
    }

    public boolean isConnectionOk() {
        return isConnectionOk;
    }

    public void setCallChangeNick(Callback<String> callChangeNick) {
        this.callChangeNick = callChangeNick;
    }

    public void setCallErrors(Callback<String> callErrors) {
        this.callErrors = callErrors;
    }
}
