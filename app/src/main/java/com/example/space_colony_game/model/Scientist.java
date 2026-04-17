package com.example.space_colony_game.model;

import com.example.space_colony_game.R;
import java.util.ArrayList;
import java.util.List;

public class Scientist extends CrewMember {
    private List<Gadget> inventory;
    
    public Scientist(int id, String name, int profileImageResId) {
        super(id, name, CrewRole.SCIENTIST, 70, 0, 0, profileImageResId);
        this.inventory = new ArrayList<>();
    }
    
    public Scientist(int id, String name, int hp, int exp) {
        super(id, name, CrewRole.SCIENTIST, 70, 0, 0, R.drawable.scientist_1);
        this.inventory = new ArrayList<>();
        setHp(hp);
    }
    
    public void craftGadget(Gadget gadget) {
        inventory.add(gadget);
    }
    
    public List<Gadget> getInventory() {
        return inventory;
    }
}
