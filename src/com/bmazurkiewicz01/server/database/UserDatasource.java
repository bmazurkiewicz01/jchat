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
    private static final String GET_USER_ID = "SELECT " + COLUMN_ID + " FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = ?";
    private static final String INSERT_USER = String.format("INSERT INTO %s (%s,%s) VALUES(?,?)", TABLE_NAME, COLUMN_NAME, COLUMN_PASSWORD);
    private static final String UPDATE_USER_NAME = "UPDATE " + TABLE_NAME + " SET " + COLUMN_NAME + " = ?" + " WHERE " + COLUMN_ID + " = ?";
    private static final String UPDATE_USERS_PASSWORD = "UPDATE " + TABLE_NAME + " SET " + COLUMN_PASSWORD + " = ?" + " WHERE " + COLUMN_ID + " = ?";

    private Connection connection;
    private Codec codec;
    private PreparedStatement queryUsers;
    private PreparedStatement getUserId;
    private PreparedStatement insertUser;
    private PreparedStatement updateUserName;
    private PreparedStatement updateUsersPassword;

    public UserDatasource() {
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING);
            codec = new Codec(ITERATIONS);
            queryUsers = connection.prepareStatement(QUERY_USERS);
            getUserId = connection.prepareStatement(GET_USER_ID);
            insertUser = connection.prepareStatement(INSERT_USER);
            updateUserName = connection.prepareStatement(UPDATE_USER_NAME);
            updateUsersPassword = connection.prepareStatement(UPDATE_USERS_PASSWORD);
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

    public boolean updateUserName(String oldName, String newName) {
        try {
            int id = getUserId(oldName);

            updateUserName.setString(1, newName);
            updateUserName.setInt(2, id);
            int affectedRows = updateUserName.executeUpdate();

            return affectedRows == 1;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(String name, String newPassword) {
        try {
            int id = getUserId(name);

            updateUsersPassword.setString(1, codec.generateHashedPassword(newPassword));
            updateUsersPassword.setInt(2, id);
            int affectedRows = updateUsersPassword.executeUpdate();

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

    private int getUserId(String name) {
        try {
            getUserId.setString(1, name);
            ResultSet results = getUserId.executeQuery();
            return results.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
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
