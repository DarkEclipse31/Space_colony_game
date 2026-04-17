package com.example.space_colony_game.data.model;

import com.example.spaceapplication.R;

public class Engineer extends CrewMember {
    
    public Engineer(int id, String name, int profileImageResId) {
        super(id, name, CrewRole.ENGINEER, 90, 8, 8, profileImageResId);
    }

    public Engineer(int id, String name, int hp, int exp) {
        super(id, name, CrewRole.ENGINEER, 90, 8, 8, R.drawable.engineer_1);
        setHp(hp);
    }
}
