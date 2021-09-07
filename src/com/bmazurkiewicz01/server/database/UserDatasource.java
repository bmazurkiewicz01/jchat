package com.bmazurkiewicz01.server.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDatasource {
    private static final String DB_NAME = "users.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:C:\\Users\\Bartosz\\Documents\\GitHub\\jchat\\databases\\" + DB_NAME;
    private static final int ITERATIONS = 1000;

    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PASSWORD = "password";

    private static final String QUERY_USERS = "SELECT * FROM " + TABLE_NAME;
    private static final String INSERT_USER = String.format("INSERT INTO %s (%s,%s) VALUES(?,?)", TABLE_NAME, COLUMN_NAME, COLUMN_PASSWORD);

    private Connection connection;
    private Codec codec;
    private PreparedStatement queryUsers;
    private PreparedStatement insertUser;

    public UserDatasource() {
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING);
            codec = new Codec(ITERATIONS);
            queryUsers = connection.prepareStatement(QUERY_USERS);
            insertUser = connection.prepareStatement(INSERT_USER);
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database: " + e.getMessage());
        }
    }

    public List<User> queryUsers() {
        try {
            ResultSet result = queryUsers.executeQuery();

            List<User> users = new ArrayList<>();
            while (result.next()) {
                User user = new User(result.getString(COLUMN_NAME), result.getString(COLUMN_PASSWORD));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean insertUser(User user) {
        try {
            insertUser.setString(1, user.getName());
            insertUser.setString(2, codec.generateHashedPassword(user.getPassword()));
            int affectedRows = insertUser.executeUpdate();

            return affectedRows == 1;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean searchUserByName(String name) {
        List<User> users = queryUsers();
        if (users == null || users.isEmpty()) return false;
        for (User anotherUser : users) {
            if (name.equals(anotherUser.getName())) return true;
        }
        return false;
    }

    public boolean searchUser(User user) {
        List<User> users = queryUsers();
        if (users == null || users.isEmpty()) return false;

        int userId;
        if ((userId = getUserId(user.getName(), users)) != -1) {
            return codec.validatePassword(user.getPassword(), users.get(userId).getPassword());
        }
        return false;
    }

    private int getUserId(String name, List<User> users) {
        for (int i = 0; i < users.size(); i++) {
            if (name.equals(users.get(i).getName())) {
                return i;
            }
        }
        return -1;
    }

    public boolean close() {
        if (connection != null) {
            try {
                connection.close();
                return true;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        return false;
    }
}
