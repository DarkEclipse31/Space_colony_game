package com.example.space_colony_game.model;

import com.example.space_colony_game.R;

public class Engineer extends CrewMember {
    
    public Engineer(int id, String name, int profileImageId) {
        super(id, name, CrewRole.ENGINEER, 90, 8, 8, profileImageId);
    }

    public Engineer(int id, String name, int hp, int exp) {
        super(id, name, CrewRole.ENGINEER, 90, 8, 8, R.drawable.engineer_1);
        setHp(hp);
    }
}
