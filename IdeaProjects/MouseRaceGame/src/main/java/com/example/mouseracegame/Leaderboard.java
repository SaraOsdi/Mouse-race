package com.example.mouseracegame;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Leaderboard {

    private final Database database;

    public Leaderboard(Database database) {
        this.database = database;
    }

    public void savePlayerTime(String playerName, int time) {
        try (Connection connection = database.getConnection()) {
            String insertSQL = "INSERT INTO leaderboard (name, time) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setString(1, playerName);
                preparedStatement.setInt(2, time);
                preparedStatement.executeUpdate();
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
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topPlayers;
    }
}
