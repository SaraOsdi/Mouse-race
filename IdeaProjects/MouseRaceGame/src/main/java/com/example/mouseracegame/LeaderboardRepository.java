package com.example.mouseracegame;

import java.util.List;

public interface LeaderboardRepository {
    void clearLeaderboard();
    void savePlayerTime(String playerName, int time);
    List<String> getTopPlayers(int limit);
}
