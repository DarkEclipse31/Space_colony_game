
package com.example.space_colony_game.model;

import java.io.Serializable;

// Gadgets crafted in the workshop
public class Gadget implements Serializable {

    private final GadgetType type;
    private final String name;
    private final int effectValue;
    private final int craftCost;
    private final int iconResId;
    private int usesLeft;


    public Gadget(GadgetType type) {
        this.type = type;
        this.name = type.getDisplayName();
        this.effectValue = type.getEffectValue();
        this.craftCost = type.getCraftCost();
        this.iconResId = type.getIconResId();
        this.usesLeft = 1;
    }

    public GadgetType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getEffectValue() {
        return effectValue;
    }

    public int getCraftCost() {
        return craftCost;
    }

    public int getIconResId() {
        return iconResId;
    }


    // uses remaining
    public int getUsesLeft() {
        return usesLeft;
    }

    public void setUsesLeft(int usesLeft) {
        this.usesLeft = Math.max(0, usesLeft);
    }

    // Uses the gadget once
    public void consumeOneUse() {
        if (usesLeft > 0) {
            usesLeft--;
        }
    }

    // descriptions
    public String getDescription() {
        switch (type) {
            case MEDICINE:
                return "Heal +" + effectValue + " HP";
            case RIFLE:
                return "Attack +" + effectValue + " for one mission";
            case ARMOUR:
                return "Reduce retaliation by " + effectValue + " for one mission";
            case POTION:
                return "Revive 1 ally in battle";
            default:
                return "";
        }
    }

    @Override
    public String toString() {
        return name + " (" + getDescription() + ")";
    }
}
