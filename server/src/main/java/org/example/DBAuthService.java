package org.example;

import java.sql.*;

public class DBAuthService implements AuthService {
    private static volatile DBAuthService instance;

    public static final String DB_CONNECTION = "jdbc:postgresql://localhost:5432/dbchat";
    public static final String DB_USER = "postgres";
    public static final String DB_PASSWORD = "s32a7Sdqg";
    public static Connection connection;
    private static boolean isConnected;

    public static final String QUERY_LOGIN = "SELECT * FROM chat_clients WHERE login='%s' AND pass='%s'";
    public static final String QUERY_NICK_CHECK = "SELECT * FROM chat_clients WHERE nick='%s'";
    public static final String QUERY_NICK_CHANGE = "UPDATE chat_clients SET nick='%s' WHERE login='%s'";

    private DBAuthService() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            isConnected = true;
        } catch (SQLException | ClassNotFoundException throwable) {
            isConnected = false;
        }

    }

    public static DBAuthService getDBAuthService() {
        DBAuthService result = instance;
        if (result != null) {
            return result;
        }
        synchronized (DBAuthService.class) {
            if (instance == null) {
                instance = new DBAuthService();
            }
            return instance;
        }
    }

    public static boolean isConnected() {
        return isConnected;
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        try (PreparedStatement statement = connection.prepareStatement(String.format(QUERY_LOGIN, login, password));
             ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                return result.getString("nick").trim();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean changeNick(String login, String nick) {
        try (PreparedStatement nickCheck = connection.prepareStatement(String.format(QUERY_NICK_CHECK, nick));
        PreparedStatement nickUpdate = connection.prepareStatement(String.format(QUERY_NICK_CHANGE, nick, login));
        ResultSet result = nickCheck.executeQuery()) {
            if (!result.next()) {
                nickUpdate.executeUpdate();
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}
