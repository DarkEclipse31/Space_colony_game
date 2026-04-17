package com.example.space_colony_game.model;

import java.io.Serializable;

public class Mission implements Serializable {

    private String id;
    private String title;
    private MissionDifficulty difficulty;
    private MissionType missionType;
    private Threat threat;
    private int maxCrew;
    private int imageResId;
    private int threatPortraitResId;
    private boolean dragonMission;
    private int rewardCoins;
    private int rewardExp;

    public Mission() {
    }

    public Mission(String id,String title, MissionDifficulty difficulty, MissionType missionType, Threat threat, int maxCrew, int imageResId, boolean dragonMission) {
        this(id, title, difficulty, missionType, threat, maxCrew, imageResId, imageResId, dragonMission);
    }

    public Mission(String id,String title, MissionDifficulty difficulty, MissionType missionType, Threat threat, int maxCrew, int imageResId, int threatPortraitResId, boolean dragonMission) {
        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.missionType = missionType;
        this.threat = threat;
        this.maxCrew = maxCrew;
        this.imageResId = imageResId;
        this.threatPortraitResId = threatPortraitResId;
        this.dragonMission = dragonMission;

        if (difficulty != null) {
            this.rewardCoins = difficulty.getRewardCoins();
            this.rewardExp = difficulty.getRewardExp();
        }
    }

    public String getId() {
        return id;
    }

    public Mission setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return title;
    }

    public Mission setTitle(String title) {
        this.title = title;
        return this;
    }

    public MissionDifficulty getDifficulty() {
        return difficulty;
    }

    public Mission setDifficulty(MissionDifficulty difficulty) {
        this.difficulty = difficulty;
        if (difficulty != null) {
            this.rewardCoins = difficulty.getRewardCoins();
            this.rewardExp = difficulty.getRewardExp();
        }
        return this;
    }

    public String getDifficultyText() {
        return difficulty != null ? difficulty.getDisplayName() : "";
    }

    public MissionType getMissionType() {
        return missionType;
    }

    public MissionType getType() {
        return missionType;
    }

    public Mission setMissionType(MissionType missionType) {
        this.missionType = missionType;
        return this;
    }

    public String getCategoryText() {
        return missionType != null ? missionType.getDisplayName() : "";
    }

    public Threat getThreat() {
        return threat;
    }

    public Mission setThreat(Threat threat) {
        this.threat = threat;
        return this;
    }

    public int getThreatHp() {
        return threat != null ? threat.getHp() : 0;
    }

    public int getThreatAttack() {
        return threat != null ? threat.getSkill() : 0;
    }

    public int getThreatRetaliate() {
        return threat != null ? threat.getResilience() : 0;
    }

    public int getThreatSkill() {
        return threat != null ? threat.getSkill() : 0;
    }

    public int getThreatResilience() {
        return threat != null ? threat.getResilience() : 0;
    }

    public int getMaxCrew() {
        return maxCrew;
    }

    public int getCrewLimit() {
        return maxCrew;
    }

    public Mission setMaxCrew(int maxCrew) {
        this.maxCrew = maxCrew;
        return this;
    }

    public int getImageResId() {
        return imageResId;
    }

    public Mission setImageResId(int imageResId) {
        this.imageResId = imageResId;
        return this;
    }

    public int getThreatPortraitResId() {
        return threatPortraitResId != 0 ? threatPortraitResId : imageResId;
    }

    public Mission setThreatPortraitResId(int threatPortraitResId) {
        this.threatPortraitResId = threatPortraitResId;
        return this;
    }

    public boolean isDragonMission() {
        return dragonMission;
    }

    public Mission setDragonMission(boolean dragonMission) {
        this.dragonMission = dragonMission;
        return this;
    }

    public int getRewardCoins() {
        return rewardCoins;
    }

    public Mission setRewardCoins(int rewardCoins) {
        this.rewardCoins = rewardCoins;
        return this;
    }

    public int getRewardExp() {
        return rewardExp;
    }

    public Mission setRewardExp(int rewardExp) {
        this.rewardExp = rewardExp;
        return this;
    }

    public String getActionLabel() {
        return missionType != null ? missionType.getActionLabel() : "START";
    }
}
