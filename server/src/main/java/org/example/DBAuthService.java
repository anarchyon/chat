package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class DBAuthService implements AuthService {
    private static volatile DBAuthService instance;
    private static final Logger LOGGER = LogManager.getLogger(DBAuthService.class);

    //public static final String DB_CONNECTION = "jdbc:postgresql://localhost:5432/dbchat";
    //public static final String DB_USER = "postgres";
    //public static final String DB_PASSWORD = "s32a7Sdqg";
    public static final String SQLite_CONNECTION = "jdbc:sqlite:ChatDB.db";
    public Connection connection;

    private PreparedStatement findByLoginAndPassword;
    private PreparedStatement nickCheck;
    private PreparedStatement nickUpdate;

    private DBAuthService() {
        try {
            //Class.forName("org.postgresql.Driver");
            Class.forName("org.sqlite.JDBC");
            //connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            connection = DriverManager.getConnection(SQLite_CONNECTION);
        } catch (SQLException | ClassNotFoundException throwable) {
            LOGGER.error("Ошибка подключения к БД");
        }
        createPreparedStatement();
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

    private void createPreparedStatement() {
        try {
            findByLoginAndPassword = connection.prepareStatement(
                    "SELECT * FROM chat_clients WHERE LOWER(login)=LOWER(?) AND password=?");
            nickCheck = connection.prepareStatement("SELECT * FROM chat_clients WHERE nick=?");
            nickUpdate = connection.prepareStatement(
                    "UPDATE chat_clients SET nick=? WHERE login=?");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage());
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        ResultSet resultSet = null;
        try {
            findByLoginAndPassword.setString(1, login);
            findByLoginAndPassword.setString(2, password);
            resultSet = findByLoginAndPassword.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("nick").trim();
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage());
        } finally {
            closeResultSet(resultSet);
        }
        return null;
    }

    @Override
    public int changeNick(String login, String nick) {
        ResultSet resultSet = null;
        try {
            nickCheck.setString(1, nick);
            nickUpdate.setString(1, nick);
            nickUpdate.setString(2, login);
            resultSet = nickCheck.executeQuery();
            if (!resultSet.next()) {
                nickUpdate.executeUpdate();
                return AuthService.ANSWER_CHANGE_NICK_OK;
            } else {
                return AuthService.ANSWER_CHANGE_NICK_BUSY;
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage());
        } finally {
            closeResultSet(resultSet);
        }
        return AuthService.ANSWER_CHANGE_NICK_OTHER_FAIL;
    }

    private void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                LOGGER.error(throwables.getMessage());
            }
        }
    }

    @Override
    public void close() {
        try {
            findByLoginAndPassword.close();
            nickUpdate.close();
            nickCheck.close();
            connection.close();
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage());
        }
    }
}
