package com.example.space_colony_game.model;

import java.io.Serializable;

public enum MissionType implements Serializable {
    COMBAT("Combat", "FIGHT"),
    EXPLORATION("Exploration", "EXPLORE"),
    REPAIR("Repair", "REPAIR"),
    BOSS("Boss", "FIGHT");

    private final String displayName;
    private final String actionLabel;

    MissionType(String displayName, String actionLabel) {
        this.displayName = displayName;
        this.actionLabel = actionLabel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    @Override
    public String toString() {
        return displayName;
    }
}