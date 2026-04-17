package com.example.space_colony_game.data.model;

import java.io.Serializable;

// base template for Crew (inc. health, attack stats, level ups, time-based stats)

public abstract class CrewMember implements Serializable {
    public static final int MAX_LEVEL = 5;

    private final int id;
    private final String name;
    private final CrewRole role;
    private final int profileImageResId;

    private Location location;
    private int level;
    private int exp;
    private int hp;
    private int maxHp;
    private int baseAttack;
    private int resilience;

    private long trainingEndTimeMillis;
    private boolean trainingRewardReady;

    private long healingEndTimeMillis;
    private boolean healingReady;

    protected CrewMember(
            int id,
            String name,
            CrewRole role,
            int maxHp,
            int baseAttack,
            int resilience,
            int profileImageResId
    ) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.profileImageResId = profileImageResId;

        this.location = Location.QUARTERS;
        this.level = 1;
        this.exp = 0;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.baseAttack = baseAttack;
        this.resilience = resilience;

        this.trainingEndTimeMillis = 0L;
        this.trainingRewardReady = false;

        this.healingEndTimeMillis = 0L;
        this.healingReady = false;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CrewRole getRole() {
        return role;
    }

    public int getProfileImageResId() {
        return profileImageResId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getResilience() {
        return resilience;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void setHp(int hp) {
        this.hp = hp;
        if (this.hp < 0) {
            this.hp = 0;
        }
        if (this.hp > this.maxHp) {
            this.hp = this.maxHp;
        }
    }

    public void takeDamage(int rawDamage) {
        int damageTaken = rawDamage - this.resilience;
        if (damageTaken < 0) {
            damageTaken = 0;
        }
        
        this.hp = this.hp - damageTaken;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    public void heal(int amount) {
        if (amount <= 0) {
            return;
        }
        
        this.hp = this.hp + amount;
        if (this.hp > this.maxHp) {
            this.hp = this.maxHp;
        }
    }

    public void restoreToFullHp() {
        hp = maxHp;
    }

    public int getLevelAttackBonus() {
        return level / 2;
    }

    public int getCurrentAttack() {
        // Base attack - no extra level bonus here to keep combat challenging
        return baseAttack;
    }

    public int getAttackForMission(MissionType missionType) {
        return getCurrentAttack() + getMissionAttackBonus(missionType);
    }

    public void addExp(int amount) {
        if (amount <= 0) {
            return;
        }

        exp = exp + amount;

        while (level < MAX_LEVEL && exp >= getExpNeededForNextLevel()) {
            exp = exp - getExpNeededForNextLevel();
            level = level + 1;
            
            // Restricted stat increases to maintain challenge
            maxHp = maxHp + 4; 
            baseAttack = baseAttack + 1;
            if (level % 5 == 0) {
                resilience = resilience + 1;
            }
            
            hp = hp + 4; // Add HP bonus instead of full restore on level up
        }
    }

    public int getExpNeededForNextLevel() {
        return level * 40;
    }

    public int getMissionAttackBonus(MissionType missionType) {
        return 0;
    }

    public boolean isImmuneToRetaliation(MissionType missionType) {
        return false;
    }

    public int getMissionExpBonus(MissionType missionType) {
        return 0;
    }

    public boolean canHeal() {
        return false;
    }

    public int getHealAmount() {
        return 0;
    }

    public boolean canCraftGadgets() {
        return false;
    }

    public double getTrainingDurationMinutes() {
        return 3.0 / 60.0;
    }

    public long getTrainingDurationMillis(boolean testMode) {
        if (testMode) {
            return 3000L;
        }

        return 3000L;
    }

    public void startTraining(long currentTimeMillis, boolean testMode) {
        trainingEndTimeMillis = currentTimeMillis + getTrainingDurationMillis(testMode);
        trainingRewardReady = false;
    }

    public boolean isTrainingInProgress(long currentTimeMillis) {
        return trainingEndTimeMillis > currentTimeMillis && !trainingRewardReady;
    }

    public boolean isTrainingComplete(long currentTimeMillis) {
        return trainingEndTimeMillis > 0 && currentTimeMillis >= trainingEndTimeMillis;
    }

    public long getRemainingTrainingMillis(long currentTimeMillis) {
        if (!isTrainingInProgress(currentTimeMillis)) {
            return 0L;
        }
        return trainingEndTimeMillis - currentTimeMillis;
    }

    public void markTrainingRewardReady() {
        trainingRewardReady = true;
    }

    public boolean isTrainingRewardReady() {
        return trainingRewardReady;
    }

    public void clearTrainingState() {
        trainingEndTimeMillis = 0L;
        trainingRewardReady = false;
    }

    /**
     * Hospital healing:
     * - base time = 2 minutes
     * - if level >= 10, add 20 seconds per level above 9
     *
     * Test mode uses short seconds instead of minutes.
     */
    public long getHealingDurationMillis(boolean testMode) {
        if (testMode) {
            if (level < 10) {
                return 5000L;
            }
            return 5000L + ((level - 9) * 1000L);
        }

        if (level < 10) {
            return 120_000L;
        }
        return 120_000L + ((level - 9) * 20_000L);
    }

    public void startHealing(long currentTimeMillis, boolean testMode) {
        healingEndTimeMillis = currentTimeMillis + getHealingDurationMillis(testMode);
        healingReady = false;
    }

    public boolean isHealingInProgress(long currentTimeMillis) {
        return healingEndTimeMillis > currentTimeMillis && !healingReady;
    }

    public boolean isHealingComplete(long currentTimeMillis) {
        return healingEndTimeMillis > 0 && currentTimeMillis >= healingEndTimeMillis;
    }

    public long getRemainingHealingMillis(long currentTimeMillis) {
        if (!isHealingInProgress(currentTimeMillis)) {
            return 0L;
        }
        return healingEndTimeMillis - currentTimeMillis;
    }

    public void markHealingReady() {
        healingReady = true;
    }

    public boolean isHealingReady() {
        return healingReady;
    }

    public void clearHealingState() {
        healingEndTimeMillis = 0L;
        healingReady = false;
    }
}