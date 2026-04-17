package com.example.space_colony_game.util;

import com.example.space_colony_game.R;
import com.example.space_colony_game.model.CrewRole;

import java.util.Random;

public class CrewImageResolver {
    private static final Random RANDOM = new Random();

    public static int getRandomImageRes(CrewRole role) {
        int[] images;

        switch (role) {
            case ENGINEER:
                images = new int[]{
                        R.drawable.engineer_1,
                        R.drawable.engineer_2,
                        R.drawable.engineer_3,
                        R.drawable.engineer_4,
                        R.drawable.engineer_5
                };
                break;

            case MEDIC:
                images = new int[]{
                        R.drawable.medic_1,
                        R.drawable.medic_2,
                        R.drawable.medic_3
                };
                break;

            case PILOT:
                images = new int[]{
                        R.drawable.pilot_1,
                        R.drawable.pilot_2,
                        R.drawable.pilot_3,
                        R.drawable.pilot_4,
                        R.drawable.pilot_5
                };
                break;

            case SCIENTIST:
                images = new int[]{
                        R.drawable.scientist_1,
                        R.drawable.scientist_2,
                        R.drawable.scientist_3
                };
                break;

            case SOLDIER:
            default:
                images = new int[]{
                        R.drawable.soldier_1,
                        R.drawable.soldier_2,
                        R.drawable.soldier_3,
                        R.drawable.soldier_4,
                        R.drawable.soldier_5,
                        R.drawable.soldier_6,
                        R.drawable.soldier_7
                };
                break;
        }

        return images[RANDOM.nextInt(images.length)];
    }
}