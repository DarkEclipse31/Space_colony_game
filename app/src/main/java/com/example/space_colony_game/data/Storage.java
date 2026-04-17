package com.example.space_colony_game.data;

import com.example.spaceapplication.model.CrewMember;
import com.example.spaceapplication.model.CrewRole;
import com.example.spaceapplication.model.Engineer;
import com.example.spaceapplication.model.Location;
import com.example.spaceapplication.model.Medic;
import com.example.spaceapplication.model.Pilot;
import com.example.spaceapplication.model.Scientist;
import com.example.spaceapplication.model.Soldier;

import java.util.ArrayList;
import java.util.List;

// control and organize game data

public class Storage {

    public static final int MAX_CREW_COUNT = 8;

    // only one storage instance allowed to exist
    private static Storage instance;

    private GameState gameState;

    // unique id for crew members so that we don't mix them up (our names & pfps are limited)
    private int idCounter = 10;

    private Storage() {
        gameState = new GameState();
    }

    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    public GameState getGameState() {
        if (gameState == null) {
            gameState = new GameState();
        }
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    // Crew member IDs
    public int nextId() {
        return ++idCounter;
    }

    public int getCurrentIdCounter() {
        return idCounter;
    }

    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    public void resetGameState() {
        gameState = new GameState();
        idCounter = 10;
    }

    public List<CrewMember> getAllCrewMembers() {
        return getGameState().getCrewMembers();
    }

    public int getTotalCrewCount() {
        return getAllCrewMembers().size();
    }

    public boolean canRecruitMore() {
        return getTotalCrewCount() < MAX_CREW_COUNT;
    }

    public CrewMember createCrewMember(String name, CrewRole role, int profileImageResId) {
        if (name == null || name.trim().isEmpty() || role == null) {
            return null;
        }

        if (!canRecruitMore()) {
            return null;
        }

        int id = nextId();
        String cleanName = name.trim();

        switch (role) {
            case PILOT:
                return new Pilot(id, cleanName, profileImageResId);
            case ENGINEER:
                return new Engineer(id, cleanName, profileImageResId);
            case MEDIC:
                return new Medic(id, cleanName, profileImageResId);
            case SCIENTIST:
                return new Scientist(id, cleanName, profileImageResId);
            case SOLDIER:
                return new Soldier(id, cleanName, profileImageResId);
            default:
                return null;
        }
    }

    public void addCrewMember(CrewMember crewMember) {
        if (crewMember == null) {
            return;
        }
        getAllCrewMembers().add(crewMember);
    }

    public boolean deleteCrewMember(int crewId) {
        List<CrewMember> list = getAllCrewMembers();
        for (int i = 0; i < list.size(); i++) {
            CrewMember crewMember = list.get(i);
            if (crewMember != null && crewMember.getId() == crewId) {
                // Prevent deleting the last Pilot
                if (crewMember.getRole() == CrewRole.PILOT) {
                    int pilotCount = 0;
                    for (CrewMember cm : list) {
                        if (cm.getRole() == CrewRole.PILOT) pilotCount++;
                    }
                    if (pilotCount <= 1) return false;
                }
                list.remove(i);
                return true;
            }
        }
        return false;
    }

    public CrewMember getCrewMemberById(int crewId) {
        for (CrewMember crewMember : getAllCrewMembers()) {
            if (crewMember != null && crewMember.getId() == crewId) {
                return crewMember;
            }
        }
        return null;
    }

    // location --> where they are in the game world
    public boolean moveCrewMember(int crewId, Location newLocation) {
        CrewMember crewMember = getCrewMemberById(crewId);
        if (crewMember == null || newLocation == null) {
            return false;
        }
        crewMember.setLocation(newLocation);
        return true;
    }

    public int getCoins() {
        return getGameState().getCoins();
    }

    public void setCoins(int coins) {
        getGameState().setCoins(coins);
    }
}
