package com.example.space_colony_game.model;

import java.io.Serializable;

public enum MissionDifficulty implements Serializable {
    EASY("Easy", 20, 70),
    MEDIUM("Medium", 40, 90),
    HARD("Hard", 60, 100),
    ELITE("Elite", 100, 200);

    private final String displayName;
    private final int rewardExp;
    private final int rewardCoins;

    MissionDifficulty(String displayName, int rewardExp, int rewardCoins) {
        this.displayName = displayName;
        this.rewardExp = rewardExp;
        this.rewardCoins = rewardCoins;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getRewardExp() {
        return rewardExp;
    }

    public int getRewardCoins() {
        return rewardCoins;
    }

    @Override
    public String toString() {
        return displayName;
    }
}