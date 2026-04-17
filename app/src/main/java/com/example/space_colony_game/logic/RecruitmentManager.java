package com.example.space_colony_game.logic;

import com.example.space_colony_game.R;
import com.example.space_colony_game.model.CrewRole;
import com.example.space_colony_game.model.RecruitCandidate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecruitmentManager {
    // Randomizing the names from name pool
    private static final Random random = new Random();
    private static final String[] NAMES = {"Aria", "Bax", "Cyrus", "Dara", "Echo", "Finn", "Gia", "Halen"};

    // generating random crew members and their stats to recruit in recruit page - lunaris
    public static List<RecruitCandidate> generateRandomRecruitPage() {
        List<RecruitCandidate> candidates = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String name = NAMES[random.nextInt(NAMES.length)];
            CrewRole role = CrewRole.values()[random.nextInt(CrewRole.values().length)];
            int level=1+random.nextInt(3);
            int hp=50+(level*5);
            int attack=5+level;
            int resilience=2+(level/2);
            
            // Cost of crew: 100*level(Level 1=100,Level 5 = 500)
            int cost = level * 100;

            // randomizing the crew images from the given few images.
            int imageResId=getRandomImageForRole(role);

            candidates.add(new RecruitCandidate(name, role, level, hp, hp, attack, resilience, cost, imageResId));
        }
        return candidates;
    }

    // randomizing the crew images from the given few images.
    private static int getRandomImageForRole(CrewRole role) {
        switch (role) {
            case PILOT:
                int pIdx =1+random.nextInt(5);
                switch (pIdx) {
                    case 1: return R.drawable.pilot_1;
                    case 2: return R.drawable.pilot_2;
                    case 3: return R.drawable.pilot_3;
                    case 4: return R.drawable.pilot_4;
                    default: return R.drawable.pilot_5;
                }
            case MEDIC:
                int mIdx = 1 + random.nextInt(3);
                switch (mIdx) {
                    case 1: return R.drawable.medic_1;
                    case 2: return R.drawable.medic_2;
                    default: return R.drawable.medic_3;
                }
            case SOLDIER:
                int sIdx = 1 + random.nextInt(7);
                switch (sIdx) {
                    case 1: return R.drawable.soldier_1;
                    case 2: return R.drawable.soldier_2;
                    case 3: return R.drawable.soldier_3;
                    case 4: return R.drawable.soldier_4;
                    case 5: return R.drawable.soldier_5;
                    case 6: return R.drawable.soldier_6;
                    default: return R.drawable.soldier_7;
                }
            case ENGINEER:
                int eIdx = 1 + random.nextInt(5);
                switch (eIdx) {
                    case 1: return R.drawable.engineer_1;
                    case 2: return R.drawable.engineer_2;
                    case 3: return R.drawable.engineer_3;
                    case 4: return R.drawable.engineer_4;
                    default: return R.drawable.engineer_5;
                }
            case SCIENTIST:
                int scIdx = 1 + random.nextInt(3);
                switch (scIdx) {
                    case 1: return R.drawable.scientist_1;
                    case 2: return R.drawable.scientist_2;
                    default: return R.drawable.scientist_3;
                }
            default:
                return R.drawable.pilot_1;
        }
    }
}
