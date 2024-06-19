package com.example.mouseracegame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDatabase implements Database {

    private static final String DATABASE_URL = "jdbc:sqlite:sample.db";

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public void initializeDatabase() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS leaderboard (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "time INTEGER NOT NULL)";
            statement.executeUpdate(createTableSQL);
            System.out.println("Leaderboard table created or already exists.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
