package com.example.mouseracegame;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Leaderboard {

    private final Database database;

    public Leaderboard(Database database) {
        this.database = database;
    }

    public void clearLeaderboard() {
        try (Connection connection = database.getConnection()) {
            String deleteSQL = "DELETE FROM leaderboard";
            try (Statement statement = connection.createStatement()) {
                int rowsDeleted = statement.executeUpdate(deleteSQL);
                System.out.println("Cleared " + rowsDeleted + " row(s) from leaderboard.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePlayerTime(String playerName, int time) {
        try (Connection connection = database.getConnection()) {
            String insertSQL = "INSERT INTO leaderboard (name, time) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setString(1, playerName);
                preparedStatement.setInt(2, time);
                int rowsAffected = preparedStatement.executeUpdate();
                System.out.println("Inserted " + rowsAffected + " row(s) into leaderboard.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getTopPlayers() {
        List<String> topPlayers = new ArrayList<>();
        try (Connection connection = database.getConnection()) {
            String selectSQL = "SELECT name, time FROM leaderboard ORDER BY time ASC LIMIT 3";
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
            e.printStackTrace();
        }
        return topPlayers;
    }
}
