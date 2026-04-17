package com.example.space_colony_game.logic;

import com.example.space_colony_game.data.GameState;
import com.example.space_colony_game.data.Storage;
import com.example.space_colony_game.model.CrewMember;
import com.example.space_colony_game.model.CrewRole;
import com.example.space_colony_game.model.Gadget;
import com.example.space_colony_game.model.GadgetType;
import com.example.space_colony_game.model.Location;
import com.example.space_colony_game.model.Scientist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class WorkshopManager {

    public static final boolean TEST_MODE=true;

    private static final Random random=new Random();

    private static final ArrayList<CraftingJob> activeJobs = new ArrayList<>();

    private WorkshopManager() {
    }

    public static class CraftingJob {
        private final int scientistId;
        private final String scientistName;
        private final GadgetType gadgetType;
        private final long endTimeMillis;
        private final int outputCount;
        private final Gadget gadgetTemplate;

        // Constructor
        public CraftingJob(int scientistId,
                           String scientistName,
                           GadgetType gadgetType,
                           long endTimeMillis,
                           int outputCount,
                           Gadget gadgetTemplate) {
            this.scientistId = scientistId;
            this.scientistName = scientistName;
            this.gadgetType = gadgetType;
            this.endTimeMillis = endTimeMillis;
            this.outputCount = outputCount;
            this.gadgetTemplate = gadgetTemplate;
        }

        // Getters and Setters
        public int getScientistId() {
            return scientistId;
        }

        public String getScientistName() {
            return scientistName;
        }

        public GadgetType getGadgetType() {
            return gadgetType;
        }

        public long getEndTimeMillis() {
            return endTimeMillis;
        }

        public int getOutputCount() {
            return outputCount;
        }

        public Gadget getGadgetTemplate() {
            return gadgetTemplate;
        }

        public boolean isComplete(long now) {
            return now >= endTimeMillis;
        }

        public long getRemainingMillis(long now) {
            return Math.max(0L, endTimeMillis - now);
        }
    }

    // Getting the list of scientist the player has purchased and is there in the crew list to activate working in workshop
    public static List<Scientist> getAvailableScientists() {
        List<Scientist> scientists = new ArrayList<>();

        for (CrewMember crewMember : Storage.getInstance().getAllCrewMembers()) {
            if (crewMember instanceof Scientist
                    && crewMember.getRole() == CrewRole.SCIENTIST
                    && crewMember.getHp() > 0
                    && crewMember.getLocation() == Location.QUARTERS
                    && !isScientistBusy(crewMember.getId())) {
                scientists.add((Scientist) crewMember);
            }
        }

        return scientists;
    }

    // making sure the scientist is not occupied in crafting other gadgets
    public static boolean isScientistBusy(int scientistId) {
        for (CraftingJob job : activeJobs) {
            if (job.getScientistId() == scientistId) {
                return true;
            }
        }
        return false;
    }

    public static List<CraftingJob> getActiveJobs() {
        return new ArrayList<>(activeJobs);
    }

    public static List<Gadget> getCraftedInventory() {
        return new ArrayList<>(GameState.getInstance().getGadgets());
    }

    // Assigning the gadget cost and assigning scientist to create the gadget
    public static boolean startCrafting(Scientist scientist, GadgetType gadgetType) {
        if (scientist == null || gadgetType == null) {
            return false;
        }

        if (scientist.getHp() <= 0) {
            return false;
        }

        if (scientist.getLocation() != Location.QUARTERS) {
            return false;
        }

        if (isScientistBusy(scientist.getId())) {
            return false;
        }

        Gadget gadget = createGadgetTemplate(gadgetType);
        if (gadget == null) {
            return false;
        }
        // deducting coins from the player's coin balance
        int cost = gadget.getCraftCost();
        if (!GameState.getInstance().spendCoins(cost)) {
            return false;
        }

        // Using long to assign time (functions as a timer)
        long now = System.currentTimeMillis();
        long durationMillis = getCraftDurationMillis(scientist, gadgetType, TEST_MODE);
        int outputCount = rollOutputCount(scientist);

        activeJobs.add(new CraftingJob(
                scientist.getId(),
                scientist.getName(),
                gadgetType,
                now + durationMillis,
                outputCount,
                gadget
        ));

        scientist.setLocation(Location.WORKSHOP);
        return true;
    }

    // Collecting the completed gadgets and assigning them back to the player's inventory
    public static int collectCompletedJobs() {
        long now = System.currentTimeMillis();
        int collected = 0;

        List<Gadget> sharedInventory = GameState.getInstance().getGadgets();

        Iterator<CraftingJob> iterator = activeJobs.iterator();
        while (iterator.hasNext()) {
            CraftingJob job = iterator.next();

            if (!job.isComplete(now)) {
                continue;
            }

            for (int i = 0; i < job.getOutputCount(); i++) {
                sharedInventory.add(copyGadget(job.getGadgetTemplate()));
                collected++;
            }

            CrewMember crewMember = findCrewById(job.getScientistId());
            if (crewMember != null) {
                crewMember.setLocation(Location.QUARTERS);
            }

            iterator.remove();
        }

        return collected;
    }

    public static long getCraftDurationMillis(Scientist scientist, GadgetType gadgetType, boolean testMode) {
        long baseMillis = getBaseCraftDurationMillis(gadgetType, testMode);
        int level = scientist.getLevel();

        long reduction = (long) ((level - 1) * (testMode ? 300L : 10_000L));
        long finalMillis = baseMillis - reduction;

        long minimumMillis = (long) (baseMillis * 0.4);

        if (finalMillis < minimumMillis) {
            finalMillis = minimumMillis;
        }

        return finalMillis;
    }

    public static int getDuplicateChancePercent(Scientist scientist) {
        int chance = 10 + ((scientist.getLevel() - 1) * 2);
        return Math.min(chance, 35);
    }

    private static int rollOutputCount(Scientist scientist) {
        int chance = getDuplicateChancePercent(scientist);
        int roll = random.nextInt(100) + 1;
        return roll <= chance ? 2 : 1;
    }

    private static long getBaseCraftDurationMillis(GadgetType gadgetType, boolean testMode) {
        if (testMode) {
            switch (gadgetType) {
                case MEDICINE:
                    return 5000L;
                case RIFLE:
                    return 6000L;
                case ARMOUR:
                    return 6000L;
                case POTION:
                    return 7000L;
                default:
                    return 5000L;
            }
        }

        switch (gadgetType) {
            case MEDICINE:
                return 120_000L;
            case RIFLE:
                return 150_000L;
            case ARMOUR:
                return 150_000L;
            case POTION:
                return 180_000L;
            default:
                return 120_000L;
        }
    }

    private static Gadget createGadgetTemplate(GadgetType gadgetType) {
        return new Gadget(gadgetType);
    }

    private static Gadget copyGadget(Gadget template) {
        Gadget copy = new Gadget(template.getType());
        copy.setUsesLeft(template.getUsesLeft());
        return copy;
    }

    private static CrewMember findCrewById(int crewId) {
        for (CrewMember crewMember : Storage.getInstance().getAllCrewMembers()) {
            if (crewMember.getId() == crewId) {
                return crewMember;
            }
        }
        return null;
    }
}