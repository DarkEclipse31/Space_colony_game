package com.example.space_colony_game.model;

import com.example.space_colony_game.R;

public class Medic extends CrewMember {
    private static final int HEAL_AMOUNT = 10;

    public Medic(int id, String name, int profileImageId) {
        super(id, name, CrewRole.MEDIC, 60, 2, 1, profileImageId);
    }

    public Medic(int id, String name, int hp, int exp) {
        super(id, name, CrewRole.MEDIC, 60, 2, 1, R.drawable.medic_1);
        setHp(hp);
    }

    // Medic can choose to heal instead of attack during battle
    public void healCrewMember(CrewMember target) {
        target.heal(HEAL_AMOUNT);
    }

    public int getHealAmount() {
        return HEAL_AMOUNT;
    }

    @Override
    public String toString() {
        return "[MEDIC] " + super.toString();
    }
}