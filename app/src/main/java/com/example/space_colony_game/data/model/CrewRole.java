package com.example.space_colony_game.data.model;

public enum CrewRole {
    PILOT("Pilot"),
    MEDIC("Medic"),
    SCIENTIST("Scientist"),
    ENGINEER("Engineer"),
    SOLDIER("Soldier");

    private final String displayName;

    CrewRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
