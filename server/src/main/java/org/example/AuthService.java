package org.example;

public interface AuthService {
    String getNicknameByLoginAndPassword(String login, String password);

    boolean changeNick(String login, String nick);
}
