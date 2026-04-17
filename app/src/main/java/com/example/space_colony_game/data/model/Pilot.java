package com.example.space_colony_game.data.model;

import com.example.spaceapplication.R;

public class Pilot extends CrewMember {

    public Pilot(int id, String name, int profileImageResId) {
        super(id, name, CrewRole.PILOT, 80, 10, 3, profileImageResId);
    }

    public Pilot(int id, String name) {
        this(id, name, R.drawable.pilot_1);
    }

    public Pilot(int id, String name, int hp, int exp) {
        super(id, name, CrewRole.PILOT, 80, 10, 3, R.drawable.pilot_1);
        setHp(hp);
    }

    // Pilots get bonus attack in Exploration missions
    public int getAttackPower(MissionType missionType) {
        if (missionType == MissionType.EXPLORATION) {
            return getCurrentAttack() * 2; // doubled attack in exploration
        }
        return getCurrentAttack();
    }

    @Override
    public String toString() {
        return "[PILOT] " + super.toString();
    }
}