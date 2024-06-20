package com.example.mouseracegame;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardSQLiteDatabase implements LeaderboardRepository {

    private static final String DATABASE_URL = "jdbc:sqlite:sample.db";


    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    @Override
    public void clearLeaderboard() {
        try (Connection connection = getConnection()) {
            String deleteSQL = "DELETE FROM leaderboard";
            try (Statement statement = connection.createStatement()) {
                int rowsDeleted = statement.executeUpdate(deleteSQL);
                System.out.println("Cleared " + rowsDeleted + " row(s) from leaderboard.");
            }
        } catch (SQLException e) {
            System.out.println("failed to clear leaderboard due to " + e);
        }
    }

    @Override
    public void savePlayerTime(String playerName, int time) {
        try (Connection connection = getConnection()) {
            String insertSQL = "INSERT INTO leaderboard (name, time) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setString(1, playerName);
                preparedStatement.setInt(2, time);
                int rowsAffected = preparedStatement.executeUpdate();
                System.out.println("Inserted " + rowsAffected + " row(s) into leaderboard.");
            }
        } catch (SQLException e) {
            System.out.println("failed to insert into leaderboard due to " + e);
        }
    }

    @Override
    public List<String> getTopPlayers(int limit) {
        List<String> topPlayers = new ArrayList<>();
        try (Connection connection = getConnection()) {
            String selectSQL = "SELECT name, time FROM leaderboard ORDER BY time ASC LIMIT " + limit;
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectSQL)) {

                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int time = resultSet.getInt("time");
                    topPlayers.add(name + ": " + time + " seconds");
                    System.out.println("Retrieved: " + name + ": " + time + " seconds");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving top players: " + e);
        }
        return topPlayers;
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

