package org.example;

public interface AuthService {
    int ANSWER_CHANGE_NICK_OK = 1;
    int ANSWER_CHANGE_NICK_BUSY = -1;
    int ANSWER_CHANGE_NICK_OTHER_FAIL = 0;

    String getNicknameByLoginAndPassword(String login, String password);
    int changeNick(String login, String nick);
    void close();
}
