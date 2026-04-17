package com.example.space_colony_game.logic;

import com.example.space_colony_game.R;
import com.example.space_colony_game.model.CrewMember;
import com.example.space_colony_game.model.Mission;
import com.example.space_colony_game.model.MissionDifficulty;
import com.example.space_colony_game.model.MissionType;
import com.example.space_colony_game.model.Threat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MissionEngine {
    // Attributes

    private static MissionEngine instance;

    private final Random random = new Random();

    private final List<Mission> combatPool = new ArrayList<>();
    private final List<Mission> explorationPool = new ArrayList<>();
    private final List<Mission> repairPool = new ArrayList<>();

    private final Mission dragonMission;

    private List<Mission> currentMissionBoard = new ArrayList<>();
    private Mission selectedMission;
    private List<CrewMember> selectedCrew = new ArrayList<>();

    private MissionEngine() {
        buildMissionPools();

        dragonMission = new Mission(
                "dragon_final_boss",
                "DRAGON",
                MissionDifficulty.ELITE,
                MissionType.BOSS,
                new Threat("Dragon", 125, 16, 6),
                6,
                R.drawable.dragon,
                true
        );
    }

    public static MissionEngine getInstance() {
        if (instance == null) {
            instance = new MissionEngine();
        }
        return instance;
    }

    private void buildMissionPools() {
        combatPool.clear();
        explorationPool.clear();
        repairPool.clear();

        // Combat Mission type
        combatPool.add(new Mission("pirate_ambush", "PIRATE AMBUSH", MissionDifficulty.EASY, MissionType.COMBAT, new Threat("Pirates", 20, 5, 1), 2, R.drawable.pirate, false));

        combatPool.add(new Mission("alien_creature_encounter", "ALIEN CREATURE ENCOUNTER", MissionDifficulty.MEDIUM, MissionType.COMBAT, new Threat("Alien Creature", 30, 8, 2), 3, R.drawable.mission_combat, false));

        // Exploration Mission type
        explorationPool.add(new Mission("abandoned_space_station", "ABANDONED SPACE STATION", MissionDifficulty.EASY, MissionType.EXPLORATION, new Threat("Station Hazard", 22, 5, 1), 2, R.drawable.space_station, false));

        explorationPool.add(new Mission("comet_mining", "COMET MINING", MissionDifficulty.MEDIUM, MissionType.EXPLORATION, new Threat("Comet Instability", 28, 7, 2), 3, R.drawable.comet, false));

        explorationPool.add(new Mission("black_hole_survey", "BLACK HOLE SURVEY", MissionDifficulty.HARD, MissionType.EXPLORATION, new Threat("Black Hole Distortion", 42, 10, 4), 4, R.drawable.mission_exploration, false));

        // Repair Mission type
        repairPool.add(new Mission("reactor_meltdown", "REACTOR MELTDOWN", MissionDifficulty.EASY, MissionType.REPAIR, new Threat("Reactor Failure", 18, 6, 1), 2, R.drawable.mission_repair, false));

        repairPool.add(new Mission("navigation_system_failure", "NAVIGATION SYSTEM FAILURE", MissionDifficulty.MEDIUM, MissionType.REPAIR, new Threat("Navigation Failure", 34, 8, 2), 3, R.drawable.navigation, false));

        repairPool.add(new Mission("oxygen_leak", "OXYGEN LEAK", MissionDifficulty.HARD, MissionType.REPAIR, new Threat("Oxygen System Collapse", 40, 10, 3), 4, R.drawable.oxygen, false));
    }

    // Adding the mission to the mission control
    public List<Mission> generateMissionBoard() {
        List<Mission> board = new ArrayList<>();
        board.add(randomMission(combatPool));
        board.add(randomMission(explorationPool));
        board.add(randomMission(repairPool));
        Collections.shuffle(board, random);
        currentMissionBoard = board;
        return new ArrayList<>(currentMissionBoard);
    }

    // Listing the mission in the mission control
    public List<Mission> getCurrentMissionBoard() {
        if (currentMissionBoard.isEmpty()) {
            generateMissionBoard();
        }
        return new ArrayList<>(currentMissionBoard);
    }

    // Retrieving the Dragon mission
    public Mission getDragonMission() {
        return dragonMission;
    }

    public void setSelectedMission(Mission mission) {
        this.selectedMission = mission;
    }

    public Mission getSelectedMission() {
        return selectedMission;
    }

    public void setSelectedCrew(List<CrewMember> selectedCrew) {
        this.selectedCrew = new ArrayList<>(selectedCrew);
    }

    public List<CrewMember> getSelectedCrew() {
        return new ArrayList<>(selectedCrew);
    }

    // Validating the mission selection
    public String validateMissionSelection(Mission mission, List<CrewMember> crewList) {
        if (mission == null) {
            return "No mission selected.";
        }

        if (crewList == null || crewList.isEmpty()) {
            return "Select at least 1 crew member.";
        }

        if (crewList.size() > mission.getMaxCrew()) {
            return "This mission allows only " + mission.getMaxCrew() + " crew members.";
        }

        // mandatory pilot in missions check
        boolean hasPilot=false;

        for (CrewMember crewMember:crewList) {
            if (crewMember==null) {
                continue;
            }

            String roleText = String.valueOf(crewMember.getRole()).trim().toUpperCase(Locale.US);
            if (roleText.equals("PILOT")) {
                hasPilot=true;
                break;
            }
        }
// if pilot is not added in missions
        if (!hasPilot) {
            return "Every mission team must include at least 1 Pilot.";
        }

        return null;
    }

    // randomizing the missions from the mission pool of combat, repair and explore missions with varied difficulty levels
    private Mission randomMission(List<Mission> pool) {
        return pool.get(random.nextInt(pool.size()));
    }
}
