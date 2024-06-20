package com.example.mouseracegame;


import java.util.List;

public class Leaderboard {

    private final LeaderboardRepository leaderboardRepository;

    public Leaderboard(LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    public void clearLeaderboard() {
        leaderboardRepository.clearLeaderboard();
    }

    public void savePlayerTime(String playerName, int time) {
        leaderboardRepository.savePlayerTime(playerName, time);
    }

    public List<String> getTopPlayers(int limit) {
        return leaderboardRepository.getTopPlayers(limit);
    }
}
