package com.example.space_colony_game.data;

import com.example.space_colony_game.model.CrewMember;
import com.example.space_colony_game.model.Gadget;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Store progress, serializable so its loadable and savable
public class GameState implements Serializable {
    private int coins;
    private int totalMissions;
    private int totalWins;
    private int totalLosses;
    private boolean starterPilotGiven; // first crew member

    private List<CrewMember> crewMembers;
    private List<Gadget> gadgets;

    // Initial stats for fresh game
    public GameState() {
        this.coins = 300;
        this.totalMissions = 0;
        this.totalWins = 0;
        this.totalLosses = 0;
        this.starterPilotGiven = false;
        this.crewMembers = new ArrayList<>();
        this.gadgets = new ArrayList<>();
    }

  // fetch current gamestate from Storage
    public static GameState getInstance() {
        return Storage.getInstance().getGameState();
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = Math.max(0, coins);
    }

    // COIN
    public void addCoins(int amount) {
        if (amount > 0) {
            coins += amount;
        }
    }

    public boolean spendCoins(int amount) {
        if (amount <= 0) return false;
        if (coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }

// CREW
    public List<CrewMember> getCrewMembers() {
        if (crewMembers == null) {
            crewMembers = new ArrayList<>();
        }
        return crewMembers;
    }

    public void setCrewMembers(List<CrewMember> crewMembers) {
        this.crewMembers = crewMembers != null ? crewMembers : new ArrayList<>();
    }

    // GADGET
    public List<Gadget> getGadgets() {
        if (gadgets == null) {
            gadgets = new ArrayList<>();
        }
        return gadgets;
    }

    public void setGadgets(List<Gadget> gadgets) {
        this.gadgets = gadgets != null ? gadgets : new ArrayList<>();
    }

    // MISSION
    public int getTotalMissions() { return totalMissions; }
    public void setTotalMissions(int totalMissions) { this.totalMissions = totalMissions; }

    public int getTotalWins() { return totalWins; }
    public void setTotalWins(int totalWins) { this.totalWins = totalWins; }

    public int getTotalLosses() { return totalLosses; }
    public void setTotalLosses(int totalLosses) { this.totalLosses = totalLosses; }

    public void recordWin() {
        totalMissions++;
        totalWins++;
    }

    public void recordLoss() {
        totalMissions++;
        totalLosses++;
    }

    public boolean isStarterPilotGiven() { return starterPilotGiven; }
    public void setStarterPilotGiven(boolean starterPilotGiven) { this.starterPilotGiven = starterPilotGiven; }
}
