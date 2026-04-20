package com.example.space_colony_game.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.space_colony_game.MainActivity;
import com.example.space_colony_game.R;
import com.example.space_colony_game.RecyclerView.GadgetAdapter;
import com.example.space_colony_game.data.GameState;
import com.example.space_colony_game.logic.MissionEngine;
import com.example.space_colony_game.model.CrewMember;
import com.example.space_colony_game.model.CrewRole;
import com.example.space_colony_game.model.Gadget;
import com.example.space_colony_game.model.GadgetType;
import com.example.space_colony_game.model.Location;
import com.example.space_colony_game.model.Mission;
import com.example.space_colony_game.model.MissionType;
import com.example.space_colony_game.model.Threat;

import java.util.ArrayList;
import java.util.List;

/**
 * BattleFragment handles the logic and animations for combat between the crew and a threat.
 */
public class BattleFragment extends Fragment {

    private static final String ARG_MISSION = "arg_mission";
    private static final String ARG_CREW = "arg_crew";

    private static final int MEDIC_HEAL_AMOUNT = 10;
    private static final int SPECIALIST_ATTACK_BONUS = 2;
    private static final int SPECIALIST_EXTRA_EXP = 10;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());

    private Mission currentMission;
    private Threat enemyThreat;

    private int enemyCurrentHp;
    private int currentTurnIndex = 0;
    private boolean isActionLocked = false;

    private View battleViewRoot;
    private TextView labelBattleTitle;
    private TextView labelThreatName;
    private TextView labelThreatStats;
    private TextView labelThreatHpText;
    private ProgressBar barThreatHp;
    private ImageView imageThreatPortrait;
    private TextView labelTurnInfo;
    private TextView buttonAttack;
    private TextView buttonHeal;
    private TextView buttonGadgets;
    private HorizontalScrollView scrollCrewContainer;
    private LinearLayout layoutCrewStrip;

    private final List<BattleCrewState> battleCrewList = new ArrayList<>();

    public BattleFragment() {
    }

    public static BattleFragment newInstance(Mission mission) {
        BattleFragment fragment = new BattleFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MISSION, mission);
        fragment.setArguments(args);
        return fragment;
    }

    public static BattleFragment newInstance(Mission mission, ArrayList<CrewMember> crew) {
        BattleFragment fragment = new BattleFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MISSION, mission);
        args.putSerializable(ARG_CREW, crew);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.getArguments() != null) {
            Object missionData = this.getArguments().getSerializable(ARG_MISSION);
            if (missionData instanceof Mission) {
                this.currentMission = (Mission) missionData;
            }
        }

        if (this.currentMission == null) {
            this.currentMission = MissionEngine.getInstance().getSelectedMission();
        }

        if (this.currentMission != null) {
            this.enemyThreat = this.currentMission.getThreat();
            if (this.enemyThreat != null) {
                this.enemyCurrentHp = this.enemyThreat.getHp();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_battle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.battleViewRoot = view.findViewById(R.id.battleRoot);
        this.labelBattleTitle = view.findViewById(R.id.tvBattleTitle);
        this.labelThreatName = view.findViewById(R.id.tvThreatName);
        this.labelThreatStats = view.findViewById(R.id.tvThreatStats);
        this.labelThreatHpText = view.findViewById(R.id.tvThreatHp);
        this.barThreatHp = view.findViewById(R.id.progressThreatHp);
        this.imageThreatPortrait = view.findViewById(R.id.ivThreatPortrait);
        this.labelTurnInfo = view.findViewById(R.id.tvTurnInfo);
        this.buttonAttack = view.findViewById(R.id.btnAttack);
        this.buttonHeal = view.findViewById(R.id.btnHeal);
        this.buttonGadgets = view.findViewById(R.id.btnInventory);
        this.scrollCrewContainer = view.findViewById(R.id.hsvCrewStrip);
        this.layoutCrewStrip = view.findViewById(R.id.crewStrip);

        if (this.currentMission == null || this.enemyThreat == null) {
            Toast.makeText(this.requireContext(), "Battle data is missing.", Toast.LENGTH_SHORT).show();
            ((MainActivity) this.requireActivity()).openFragment(new MissionControlFragment(), false);
            return;
        }

        this.setupBackground();
        this.initializeCrewData();
        this.createCrewCards();
        this.setupEnemyUi();

        this.buttonAttack.setOnClickListener(v -> handleAttackAction());
        this.buttonHeal.setOnClickListener(v -> handleHealAction());
        this.buttonGadgets.setOnClickListener(v -> showGadgetInventory());

        this.startNextTurn();
    }

    private void setupBackground() {
        int backgroundImageId = R.drawable.battle;
        MissionType type = this.currentMission.getMissionType();
        
        if (type == MissionType.EXPLORATION) {
            backgroundImageId = R.drawable.explore;
        } else if (type == MissionType.REPAIR) {
            backgroundImageId = R.drawable.repair;
        } else if (type == MissionType.BOSS) {
            if (this.currentMission.getImageId() != 0) {
                backgroundImageId = this.currentMission.getImageId();
            }
        }
        
        this.battleViewRoot.setBackgroundResource(backgroundImageId);
        this.labelBattleTitle.setText(this.currentMission.getTitle());

        int portraitId = this.currentMission.getThreatPortraitId();
        if (portraitId == 0) {
            portraitId = this.currentMission.getImageId();
        }
        if (portraitId == 0) {
            portraitId = R.drawable.ic_launcher_foreground;
        }
        this.imageThreatPortrait.setImageResource(portraitId);
    }

    private void initializeCrewData() {
        this.battleCrewList.clear();
        List<CrewMember> chosenCrew = null;
        if (getArguments() != null) {
            chosenCrew = (List<CrewMember>) getArguments().getSerializable(ARG_CREW);
        }
        
        if (chosenCrew == null) {
            chosenCrew = MissionEngine.getInstance().getSelectedCrew();
        }
        
        if (chosenCrew == null) return;
        
        for (CrewMember member : chosenCrew) {
            BattleCrewState battleState = new BattleCrewState();
            battleState.originalCrew = member;
            battleState.name = member.getName();
            battleState.role = member.getRole();
            battleState.attack = member.getBaseAttack() + (member.getLevel() / 2);
            battleState.resilience = member.getResilience();
            battleState.maxHp = member.getMaxHp();
            battleState.currentHp = member.getHp();
            battleState.portraitId = member.getProfileImageId();
            this.battleCrewList.add(battleState);
        }
    }

    private void createCrewCards() {
        this.layoutCrewStrip.removeAllViews();
        for (int i = 0; i < this.battleCrewList.size(); i++) {
            BattleCrewState state = this.battleCrewList.get(i);
            LinearLayout cardLayout = (LinearLayout) LayoutInflater.from(requireContext()).inflate(R.layout.item_battle_crew, layoutCrewStrip, false);
            
            if (i > 0) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) cardLayout.getLayoutParams();
                lp.setMarginStart(convertDpToPx(10));
                cardLayout.setLayoutParams(lp);
            }

            ImageView portrait = cardLayout.findViewById(R.id.ivCrewPortrait);
            TextView name = cardLayout.findViewById(R.id.tvCrewName);
            TextView hpText = cardLayout.findViewById(R.id.tvCrewHp);
            ProgressBar pb = cardLayout.findViewById(R.id.progressCrewHp);

            portrait.setImageResource(state.portraitId);
            name.setText(state.name);
            pb.setMax(state.maxHp);
            pb.setProgress(state.currentHp);
            hpText.setText(state.currentHp + "/" + state.maxHp);

            cardLayout.setOnClickListener(v -> showCrewStatsDialog(state));

            state.cardRootView = cardLayout;
            state.labelHp = hpText;
            state.barHp = pb;

            this.layoutCrewStrip.addView(cardLayout);
        }
        this.updateCrewUi();
    }

    private void setupEnemyUi() {
        this.labelThreatName.setText(this.enemyThreat.getName());
        String statsInfo = "Skill: " + this.enemyThreat.getSkill() + "  Resilience: " + this.enemyThreat.getResilience();
        this.labelThreatStats.setText(statsInfo);
        this.barThreatHp.setMax(this.enemyThreat.getHp());
        this.updateEnemyUi();
    }

    private void updateEnemyUi() {
        int health = Math.max(0, this.enemyCurrentHp);
        this.barThreatHp.setProgress(health);
        this.labelThreatHpText.setText("Energy: " + health + " / " + this.enemyThreat.getHp());
    }

    private void updateCrewUi() {
        for (int i = 0; i < this.battleCrewList.size(); i++) {
            BattleCrewState state = this.battleCrewList.get(i);
            if (state.labelHp != null) state.labelHp.setText(state.currentHp + "/" + state.maxHp);
            if (state.barHp != null) state.barHp.setProgress(state.currentHp);
            if (state.cardRootView != null) {
                boolean active = (i == this.currentTurnIndex && state.currentHp > 0);
                state.cardRootView.setAlpha(state.currentHp > 0 ? 1.0f : 0.45f);
                float scale = active ? 1.18f : 1.0f;
                state.cardRootView.animate().scaleX(scale).scaleY(scale).setDuration(180).start();
            }
        }
    }

    private void startNextTurn() {
        if (this.enemyCurrentHp <= 0) {
            this.handleBattleEnd(true);
            return;
        }

        if (!checkAnyCrewAlive()) {
            this.handleBattleEnd(false);
            return;
        }

        while (this.battleCrewList.get(this.currentTurnIndex).currentHp <= 0) {
            this.currentTurnIndex = (this.currentTurnIndex + 1) % this.battleCrewList.size();
        }

        this.updateCrewUi();
        BattleCrewState actingMember = this.battleCrewList.get(this.currentTurnIndex);
        this.labelTurnInfo.setText(actingMember.name + "'s Turn");
        
        this.buttonAttack.setEnabled(true);
        this.buttonHeal.setEnabled(true);
        this.buttonGadgets.setEnabled(true);
        
        this.buttonHeal.setVisibility(actingMember.role == CrewRole.MEDIC ? View.VISIBLE : View.GONE);

        this.scrollCrewContainer.post(() -> {
            if (actingMember.cardRootView != null) {
                int scrollPos = actingMember.cardRootView.getLeft() - convertDpToPx(16);
                scrollCrewContainer.smoothScrollTo(Math.max(0, scrollPos), 0);
            }
        });
    }

    private void handleAttackAction() {
        if (this.isActionLocked || this.enemyCurrentHp <= 0) return;

        BattleCrewState attacker = this.battleCrewList.get(this.currentTurnIndex);
        lockActions();

        int damage = attacker.attack;
        if (isSpecialistForCurrentMission(attacker)) damage += SPECIALIST_ATTACK_BONUS;
        int finalDamage = Math.max(1, damage - this.enemyThreat.getResilience());

        this.enemyCurrentHp = Math.max(0, this.enemyCurrentHp - finalDamage);

        showAnnouncement(attacker.name + " attacked!", "-" + finalDamage, 0xFFFFCC00, () -> 
            animateCrewAttack(attacker, () -> {
                updateEnemyUi();
                if (enemyCurrentHp <= 0) handleBattleEnd(true);
                else startEnemyRetaliation();
            })
        );
    }

    private void handleHealAction() {
        if (this.isActionLocked) return;
        BattleCrewState healer = this.battleCrewList.get(this.currentTurnIndex);
        if (healer.role != CrewRole.MEDIC) return;

        List<BattleCrewState> targets = new ArrayList<>();
        for (BattleCrewState member : battleCrewList) {
            if (member.currentHp > 0 && member.currentHp < member.maxHp) targets.add(member);
        }

        if (targets.isEmpty()) {
            Toast.makeText(requireContext(), "No one needs healing!", Toast.LENGTH_SHORT).show();
            return;
        }
        showHealSelectionDialog(targets, healer);
    }

    private void showGadgetInventory() {
        if (isActionLocked) return;

        final List<Gadget> inventory = GameState.getInstance().getGadgets();
        if (inventory.isEmpty()) {
            Toast.makeText(requireContext(), "No gadgets in inventory!", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_gadget_selection, null);
        RecyclerView rvGadgets = dialogView.findViewById(R.id.rvGadgetSelection);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelGadget);

        final AlertDialog dialog = new AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar)
                .setView(dialogView).create();

        GadgetAdapter adapter = new GadgetAdapter(inventory, (gadget, pos) -> {
            dialog.dismiss();
            useGadgetInBattle(gadget);
        });
        
        rvGadgets.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvGadgets.setAdapter(adapter);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void useGadgetInBattle(Gadget gadget) {
        if (gadget.getUsesLeft() <= 0) return;
        
        lockActions();
        BattleCrewState actingMember = battleCrewList.get(currentTurnIndex);
        String message = actingMember.name + " uses " + gadget.getName();
        String effect = "";
        int color = 0xFFFFFFFF;

        switch (gadget.getType()) {
            case MEDICINE:
                actingMember.currentHp = Math.min(actingMember.maxHp, actingMember.currentHp + gadget.getEffectValue());
                effect = "+" + gadget.getEffectValue() + " HP";
                color = 0xFF00FF00;
                break;
            case RIFLE:
                int dmg = Math.max(1, (actingMember.attack + gadget.getEffectValue()) - enemyThreat.getResilience());
                enemyCurrentHp = Math.max(0, enemyCurrentHp - dmg);
                effect = "-" + dmg + " DMG";
                color = 0xFFFF7043;
                break;
            case ARMOUR:
                actingMember.resilience += gadget.getEffectValue();
                effect = "+" + gadget.getEffectValue() + " ARMOR";
                color = 0xFF42A5F5;
                break;
            case POTION:
                for (BattleCrewState s : battleCrewList) {
                    if (s.currentHp <= 0) {
                        s.currentHp = 10;
                        break;
                    }
                }
                effect = "REVIVE";
                color = 0xFFFFA726;
                break;
        }

        gadget.consumeOneUse();
        if (gadget.getUsesLeft() <= 0) {
            GameState.getInstance().getGadgets().remove(gadget);
        }

        boolean isHealingGadget = gadget.getType() == GadgetType.MEDICINE || gadget.getType() == GadgetType.POTION;

        showAnnouncement(message, effect, color, () -> {
            updateCrewUi();
            updateEnemyUi();
            
            if (enemyCurrentHp <= 0) {
                handleBattleEnd(true);
            } else {
                // Allow the crew member to take their turn after using a gadget
                isActionLocked = false;
                buttonAttack.setEnabled(true);
                buttonHeal.setVisibility(actingMember.role == com.example.space_colony_game.model.CrewRole.MEDIC ? View.VISIBLE : View.GONE);
                buttonHeal.setEnabled(actingMember.role == com.example.space_colony_game.model.CrewRole.MEDIC);
                buttonGadgets.setEnabled(false); // One gadget per turn
            }
        });
    }

    private void lockActions() {
        this.isActionLocked = true;
        this.buttonAttack.setEnabled(false);
        this.buttonHeal.setEnabled(false);
        this.buttonGadgets.setEnabled(false);
    }

    private void startEnemyRetaliation() {
        List<Integer> aliveCrewIndices = new ArrayList<>();
        for (int i = 0; i < this.battleCrewList.size(); i++) {
            if (this.battleCrewList.get(i).currentHp > 0) {
                aliveCrewIndices.add(i);
            }
        }

        if (aliveCrewIndices.isEmpty()) {
            this.handleBattleEnd(false);
            return;
        }

        // Randomly decide how many targets to attack (from 1 up to all alive members)
        java.util.Random r = new java.util.Random();
        int numTargets = r.nextInt(aliveCrewIndices.size()) + 1;
        
        // Shuffle to pick random targets and random order
        java.util.Collections.shuffle(aliveCrewIndices);
        List<Integer> targetsToAttack = new ArrayList<>(aliveCrewIndices.subList(0, numTargets));

        this.processEnemyAttackStep(targetsToAttack, 0);
    }

    private void processEnemyAttackStep(final List<Integer> targets, final int stepIndex) {
        if (this.enemyCurrentHp <= 0 || stepIndex >= targets.size()) {
            this.currentTurnIndex = (this.currentTurnIndex + 1) % this.battleCrewList.size();
            this.isActionLocked = false;
            this.startNextTurn();
            return;
        }

        BattleCrewState target = this.battleCrewList.get(targets.get(stepIndex));
        
        int dmg = Math.max(1, this.enemyThreat.getSkill() - target.resilience);
        target.currentHp = Math.max(0, target.currentHp - dmg);

        animateEnemyAttack(target, () -> {
            updateCrewUi();
            int nextDelay = 150 + new java.util.Random().nextInt(250);
            timerHandler.postDelayed(() -> processEnemyAttackStep(targets, stepIndex + 1), nextDelay);
        });
    }

    private void animateCrewAttack(final BattleCrewState attacker, final Runnable endAction) {
        attacker.cardRootView.animate().translationY(-convertDpToPx(35)).scaleX(1.25f).scaleY(1.25f).setDuration(150).withEndAction(() -> 
            shootVisualProjectile(attacker.cardRootView, imageThreatPortrait, () -> 
                showImpactExplosion(imageThreatPortrait, () -> 
                    shakeView(imageThreatPortrait, () -> 
                        attacker.cardRootView.animate().translationY(0).scaleX(1.18f).scaleY(1.18f).setDuration(200).withEndAction(endAction).start()
                    )
                )
            )
        ).start();
    }

    private void animateEnemyAttack(final BattleCrewState target, final Runnable endAction) {
        this.imageThreatPortrait.animate().translationY(convertDpToPx(35)).scaleX(1.12f).scaleY(1.12f).setDuration(150).withEndAction(() -> {
            shootVisualProjectile(imageThreatPortrait, target.cardRootView, () -> 
                showImpactExplosion(target.cardRootView, () -> 
                    shakeView(target.cardRootView, () -> 
                        imageThreatPortrait.animate().translationY(0).scaleX(1.0f).scaleY(1.0f).setDuration(200).withEndAction(endAction).start()
                    )
                )
            );
        }).start();
    }

    private void shootVisualProjectile(View from, View to, final Runnable endAction) {
        if (getView() == null) { endAction.run(); return; }
        ImageView proj = new ImageView(getContext());
        int size = convertDpToPx(20);
        proj.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        proj.setImageResource(R.drawable.flash);
        proj.setColorFilter(0xFFFFCC00);

        int[] fromPos = new int[2], toPos = new int[2], rootPos = new int[2];
        from.getLocationInWindow(fromPos); to.getLocationInWindow(toPos); getView().getLocationInWindow(rootPos);

        proj.setX(fromPos[0] - rootPos[0] + from.getWidth()/2f - size/2f);
        proj.setY(fromPos[1] - rootPos[1] + from.getHeight()/2f - size/2f);
        ((ViewGroup) getView()).addView(proj);

        proj.animate().translationX(toPos[0] - rootPos[0] + to.getWidth()/2f - size/2f)
                .translationY(toPos[1] - rootPos[1] + to.getHeight()/2f - size/2f)
                .setDuration(250).withEndAction(() -> {
                    ((ViewGroup) getView()).removeView(proj);
                    endAction.run();
                }).start();
    }

    private void showImpactExplosion(View impactView, final Runnable endAction) {
        if (getView() == null) { endAction.run(); return; }
        ImageView exp = new ImageView(getContext());
        int size = convertDpToPx(120);
        exp.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        exp.setImageResource(R.drawable.flash);

        int[] viewPos = new int[2], rootPos = new int[2];
        impactView.getLocationInWindow(viewPos); getView().getLocationInWindow(rootPos);

        exp.setX(viewPos[0] - rootPos[0] + impactView.getWidth()/2f - size/2f);
        exp.setY(viewPos[1] - rootPos[1] + impactView.getHeight()/2f - size/2f);
        exp.setAlpha(0f); exp.setScaleX(0.4f); exp.setScaleY(0.4f);
        ((ViewGroup) getView()).addView(exp);

        exp.animate().alpha(1f).scaleX(1.6f).scaleY(1.6f).rotation(45).setDuration(180).withEndAction(() -> 
            exp.animate().alpha(0f).scaleX(2.2f).scaleY(2.2f).rotation(90).setDuration(150).withEndAction(() -> {
                ((ViewGroup) getView()).removeView(exp);
                endAction.run();
            }).start()
        ).start();
    }

    private void showAnnouncement(String line1, String line2, int color2, final Runnable endAction) {
        if (getView() == null) { endAction.run(); return; }
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID; lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        layout.setLayoutParams(lp);

        TextView t1 = new TextView(getContext()); t1.setText(line1); t1.setTextSize(26); t1.setTextColor(0xFFFFFFFF); t1.setGravity(Gravity.CENTER);
        TextView t2 = new TextView(getContext()); t2.setText(line2); t2.setTextSize(40); t2.setTextColor(color2); t2.setGravity(Gravity.CENTER);
        layout.addView(t1); layout.addView(t2);
        ((ViewGroup) getView()).addView(layout);

        layout.setAlpha(0f); layout.setScaleX(0.7f); layout.setScaleY(0.7f);
        layout.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(300).withEndAction(() -> 
            layout.animate().alpha(0f).scaleX(1.2f).scaleY(1.2f).setStartDelay(700).setDuration(300).withEndAction(() -> {
                ((ViewGroup) getView()).removeView(layout);
                endAction.run();
            }).start()
        ).start();
    }

    private void animateHealVisual(BattleCrewState crewState, final Runnable endAction) {
        ObjectAnimator pulse = ObjectAnimator.ofPropertyValuesHolder(crewState.cardRootView,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.14f, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.14f, 1f),
                PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0.65f, 1f));
        pulse.setDuration(350);
        pulse.addListener(new AnimatorListenerAdapter() { @Override public void onAnimationEnd(Animator animation) { endAction.run(); } });
        pulse.start();
    }

    private void shakeView(View target, final Runnable endAction) {
        int dist = convertDpToPx(8);
        ObjectAnimator shake = ObjectAnimator.ofPropertyValuesHolder(target,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0, dist, -dist, dist/1.5f, -dist/1.5f, 0),
                PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0.75f, 1f));
        shake.setDuration(240);
        shake.addListener(new AnimatorListenerAdapter() { @Override public void onAnimationEnd(Animator animation) { endAction.run(); } });
        shake.start();
    }

    private boolean isSpecialistForCurrentMission(BattleCrewState crew) {
        CrewRole role = crew.role;
        MissionType type = currentMission.getMissionType();
        return (role == CrewRole.PILOT && type == MissionType.EXPLORATION) ||
               (role == CrewRole.ENGINEER && type == MissionType.REPAIR) ||
               (role == CrewRole.SOLDIER && (type == MissionType.COMBAT || type == MissionType.BOSS));
    }

    private boolean checkAnyCrewAlive() {
        for (BattleCrewState s : battleCrewList) if (s.currentHp > 0) return true;
        return false;
    }

    private void handleBattleEnd(final boolean isVictory) {
        lockActions();
        saveResultsToGame(isVictory);
        new AlertDialog.Builder(requireContext())
                .setTitle(isVictory ? "Victory" : "Defeat")
                .setMessage(isVictory ? "Mission complete!" : "Mission failed.")
                .setCancelable(false)
                .setPositiveButton("Continue", (dialog, which) -> {
                    MissionEngine.getInstance().generateMissionBoard();
                    ((MainActivity) requireActivity()).openFragment(new MissionControlFragment(), false);
                }).show();
    }

    private void saveResultsToGame(boolean isVictory) {
        for (BattleCrewState state : battleCrewList) {
            state.originalCrew.setHp(Math.max(0, state.currentHp));
            state.originalCrew.setLocation(state.currentHp <= 0 ? Location.HOSPITAL : Location.QUARTERS);
            if (isVictory && state.currentHp > 0) {
                int exp = currentMission.getRewardExp();
                if (isSpecialistForCurrentMission(state)) exp += SPECIALIST_EXTRA_EXP;
                state.originalCrew.addExp(exp);
            }
        }
        GameState game = GameState.getInstance();
        if (isVictory) { game.recordWin(); game.addCoins(currentMission.getRewardCoins()); }
        else game.recordLoss();
    }

    private void showCrewStatsDialog(BattleCrewState state) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_crew_stats_card);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        
        ImageView ivPortrait = dialog.findViewById(R.id.ivDialogPortrait);
        TextView tvName = dialog.findViewById(R.id.tvDialogName);
        TextView tvRole = dialog.findViewById(R.id.tvDialogRole);
        TextView tvHp = dialog.findViewById(R.id.tvDialogHp);
        ProgressBar pbHp = dialog.findViewById(R.id.pbDialogHp);
        TextView tvLevelXp = dialog.findViewById(R.id.tvDialogLevelXp);
        ProgressBar pbXp = dialog.findViewById(R.id.pbDialogXp);
        TextView tvAttack = dialog.findViewById(R.id.tvDialogAttack);
        TextView tvResilience = dialog.findViewById(R.id.tvDialogResilience);
        TextView tvRetaliation = dialog.findViewById(R.id.tvDialogRetaliation);
        TextView tvSkill = dialog.findViewById(R.id.tvDialogSkill);
        TextView btnClose = dialog.findViewById(R.id.btnDialogClose);

        ivPortrait.setImageResource(state.portraitId);
        tvName.setText(state.name);
        tvRole.setText("ROLE: " + state.role.getDisplayName());
        
        tvHp.setText("HP: " + state.currentHp + " / " + state.maxHp);
        pbHp.setMax(state.maxHp);
        pbHp.setProgress(state.currentHp);

        CrewMember crew = state.originalCrew;
        tvLevelXp.setText("LEVEL: " + crew.getLevel() + "   EXP: " + crew.getExp() + " / " + crew.getExpNeededForNextLevel());
        pbXp.setMax(crew.getExpNeededForNextLevel());
        pbXp.setProgress(crew.getExp());

        tvAttack.setText("ATTACK POWER: " + state.attack);
        tvResilience.setText("RESILIENCE: " + state.resilience);

        if (state.role == CrewRole.SCIENTIST) {
            tvAttack.setVisibility(View.GONE);
            tvResilience.setVisibility(View.GONE);
        } else {
            tvAttack.setVisibility(View.VISIBLE);
            tvResilience.setVisibility(View.VISIBLE);
        }

        // Retaliation immunity check removed per request
        tvRetaliation.setVisibility(View.GONE);

        String skillText = "SKILL: ";
        switch (state.role) {
            case SOLDIER:
                skillText += "Double damage in Combat missions";
                break;
            case MEDIC:
                skillText += "Heals crew (" + crew.getHealAmount() + " HP)";
                break;
            case PILOT:
                skillText += "Increased evasion";
                break;
            case ENGINEER:
                skillText += "Bonus repair speed";
                break;
            case SCIENTIST:
                skillText += "Bonus experiment yield";
                break;
            default:
                skillText += "Standard duties";
        }
        tvSkill.setText(skillText);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showHealSelectionDialog(List<BattleCrewState> targets, BattleCrewState healer) {
        View dv = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_heal_selection, null);
        LinearLayout container = dv.findViewById(R.id.healSelectionContainer);
        AlertDialog dialog = new AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar).setView(dv).create();
        for (BattleCrewState target : targets) {
            View item = LayoutInflater.from(requireContext()).inflate(R.layout.item_heal_choice, container, false);
            ((ImageView)item.findViewById(R.id.ivHealPortrait)).setImageResource(target.portraitId);
            ((TextView)item.findViewById(R.id.tvHealName)).setText(target.name);
            ((ProgressBar)item.findViewById(R.id.progressHealHp)).setMax(target.maxHp);
            ((ProgressBar)item.findViewById(R.id.progressHealHp)).setProgress(target.currentHp);
            ((TextView)item.findViewById(R.id.tvHealHpText)).setText(target.currentHp + " / " + target.maxHp);
            item.setOnClickListener(v -> { dialog.dismiss(); performHealAction(healer, target); });
            container.addView(item);
        }
        dialog.show();
    }

    private void performHealAction(BattleCrewState healer, BattleCrewState target) {
        lockActions();
        target.currentHp = Math.min(target.maxHp, target.currentHp + MEDIC_HEAL_AMOUNT);
        showAnnouncement(healer.name + " healed " + target.name, "+" + MEDIC_HEAL_AMOUNT, 0xFF00FF00, () -> {
            updateCrewUi();
            animateHealVisual(target, () -> {
                isActionLocked = false;
                startNextTurn();
            });
        });
    }

    private int convertDpToPx(int dp) {
        return (int) (dp * requireContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    private static class BattleCrewState {
        CrewMember originalCrew;
        String name; CrewRole role;
        int attack, resilience, maxHp, currentHp, portraitId;
        View cardRootView; TextView labelHp; ProgressBar barHp;
    }
}