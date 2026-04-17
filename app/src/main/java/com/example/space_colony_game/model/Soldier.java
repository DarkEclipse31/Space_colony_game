package com.example.space_colony_game.model;

import com.example.space_colony_game.R;

public class Soldier extends CrewMember {

    public Soldier(int id, String name, int profileImageResId) {
        super(id, name, CrewRole.SOLDIER, 100, 15, 5, profileImageResId);
    }

    public Soldier(int id, String name, int hp, int exp) {
        super(id, name, CrewRole.SOLDIER, 100, 15, 5, R.drawable.soldier_1);
        setHp(hp);
    }

    // Soldiers get bonus attack in Combat missions
    public int getAttackPower(MissionType missionType) {
        if (missionType == MissionType.COMBAT) {
            return getCurrentAttack() * 2;
        }
        return getCurrentAttack();
    }

    @Override
    public String toString() {
        return "[SOLDIER] " + super.toString();
    }
}
