package com.example.space_colony_game.data.model;

import java.io.Serializable;

public class Threat implements Serializable {

    private String name;
    private int hp;
    private int skill;
    private int resilience;

    public Threat() {}

    public Threat(String name, int hp, int skill, int resilience) {
        this.name = name;
        this.hp = hp;
        this.skill = skill;
        this.resilience = resilience;
    }

    public String getName() {
        return name;
    }

    public Threat setName(String name) {
        this.name = name;
        return this;
    }

    public int getHp() {
        return hp;
    }

    public Threat setHp(int hp) {
        this.hp = hp;
        return this;
    }

    public int getSkill() {
        return skill;
    }

    public Threat setSkill(int skill) {
        this.skill = skill;
        return this;
    }

    public int getResilience() {
        return resilience;
    }

    public Threat setResilience(int resilience) {
        this.resilience = resilience;
        return this;
    }

    // Compatibility aliases for older code
    public int getAttack() {
        return skill;
    }

    public int getRetaliate() {
        return resilience;
    }

    public Threat setAttack(int attack) {
        this.skill = attack;
        return this;
    }

    public Threat setRetaliate(int retaliate) {
        this.resilience = retaliate;
        return this;
    }
}