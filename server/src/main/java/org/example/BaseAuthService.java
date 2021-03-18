package org.example;

import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {
    List<Entry> entries;

    public BaseAuthService() {
        entries = new ArrayList<>();
        entries.add(new Entry("client", "pass", "client1"));
        entries.add(new Entry("hero", "hero", "Онегин"));
        entries.add(new Entry("login", "password", "nick"));
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (Entry entry : entries) {
            if (entry.login.equals(login) && entry.password.equals(password)) {
                return entry.nick;
            }
        }
        return null;
    }

    @Override
    public boolean changeNick(String login, String nick) {
        return false;
    }

    private final class Entry {
        private String login;
        private String password;
        private String nick;

        public Entry(String login, String password, String nick) {
            this.login = login;
            this.password = password;
            this.nick = nick;
        }
    }
}
