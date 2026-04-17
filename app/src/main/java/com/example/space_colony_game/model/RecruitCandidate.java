package com.example.space_colony_game.model;

public class RecruitCandidate {

    private final String name;
    private final CrewRole role;
    private final int level;
    private final int hp;
    private final int maxHp;
    private final int attack;
    private final int resilience;
    private final int cost;
    private final int profileImageResId;

    public RecruitCandidate(
            String name,
            CrewRole role,
            int level,
            int hp,
            int maxHp,
            int attack,
            int resilience,
            int cost,
            int profileImageResId
    ) {
        this.name = name;
        this.role = role;
        this.level = level;
        this.hp = hp;
        this.maxHp = maxHp;
        this.attack = attack;
        this.resilience = resilience;
        this.cost = cost;
        this.profileImageResId = profileImageResId;
    }

    public String getName() {
        return name;
    }

    public CrewRole getRole() {
        return role;
    }

    public int getLevel() {
        return level;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttack() {
        return attack;
    }

    public int getResilience() {
        return resilience;
    }

    public int getCost() {
        return cost;
    }

    public int getProfileImageResId() {
        return profileImageResId;
    }
}
