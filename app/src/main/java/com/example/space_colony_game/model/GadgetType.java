package com.example.space_colony_game.model;

import com.example.spaceapplication.R;

import java.io.Serializable;


// Defining what type of gadgets actually exist in the game
public enum GadgetType implements Serializable {
    MEDICINE("Medicine", 40, 15, 120_000L, R.drawable.medicine),
    RIFLE("Rifle", 70, 8, 150_000L, R.drawable.rifel),
    ARMOUR("Armour", 70, 5, 150_000L, R.drawable.armor),
    POTION("Potion", 100, 1, 180_000L, R.drawable.potion);

    private final String displayName;
    private final int craftCost;
    private final int effectValue;
    private final long baseCraftMillis;
    private final int iconResId;

    GadgetType(String displayName, int craftCost, int effectValue, long baseCraftMillis, int iconResId) {
        this.displayName = displayName;
        this.craftCost = craftCost;
        this.effectValue = effectValue;
        this.baseCraftMillis = baseCraftMillis;
        this.iconResId = iconResId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getCraftCost() {
        return craftCost;
    }

    public int getEffectValue() {
        return effectValue;
    }

    public long getBaseCraftMillis() {
        return baseCraftMillis;
    }

    public int getIconResId() {
        return iconResId;
    }
}