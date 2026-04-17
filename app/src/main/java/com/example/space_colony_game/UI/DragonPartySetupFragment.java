package com.example.space_colony_game.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.space_colony_game.MainActivity;
import com.example.space_colony_game.R;
import com.example.space_colony_game.RecyclerView.MissionSetupCrewAdapter;
import com.example.space_colony_game.data.Storage;
import com.example.space_colony_game.logic.MissionEngine;
import com.example.space_colony_game.model.CrewMember;
import com.example.space_colony_game.model.Location;
import com.example.space_colony_game.model.Mission;

import java.util.ArrayList;
import java.util.List;

public class DragonPartySetupFragment extends Fragment {

    private Mission dragonMission;

    private TextView tvMissionTitle;
    private TextView tvMissionMeta;
    private TextView tvMissionThreat;
    private TextView tvMissionCrewLimit;
    private TextView tvSelectedCount;
    private RecyclerView rvSelectableCrew;

    private MissionSetupCrewAdapter crewAdapter;

    public DragonPartySetupFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dragonMission = MissionEngine.getInstance().getDragonMission();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dragon_party_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.tvMissionTitle = view.findViewById(R.id.tvDragonSetupTitle);
        this.tvMissionMeta = view.findViewById(R.id.tvDragonMeta);
        this.tvMissionThreat = view.findViewById(R.id.tvDragonThreat);
        this.tvMissionCrewLimit = view.findViewById(R.id.tvDragonCrewLimit);
        this.tvSelectedCount = view.findViewById(R.id.tvDragonSelectedCount);
        this.rvSelectableCrew = view.findViewById(R.id.rvDragonSelectableCrew);

        this.rvSelectableCrew.setLayoutManager(new LinearLayoutManager(this.requireContext()));

        this.crewAdapter = new MissionSetupCrewAdapter(new MissionSetupCrewAdapter.OnSelectionChangedListener() {
            @Override
            public void onSelectionChanged(List<CrewMember> selectedCrew) {
                tvSelectedCount.setText("Selected: " + selectedCrew.size() + " / " + dragonMission.getMaxCrew());
            }
        });
        
        this.crewAdapter.setMaxSelection(this.dragonMission.getMaxCrew());
        this.rvSelectableCrew.setAdapter(this.crewAdapter);

        view.findViewById(R.id.btnBackDragonSetup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).openFragment(new DragonFragment(), false);
            }
        });

        view.findViewById(R.id.btnStartDragonBattle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDragonBattle();
            }
        });

        this.bindMissionData();
        this.loadSelectableCrew();
    }

    private void bindMissionData() {
        if (this.dragonMission == null) {
            Toast.makeText(this.requireContext(), "Dragon mission data missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        this.tvMissionTitle.setText("ELITE BOSS: " + this.dragonMission.getTitle());
        this.tvMissionMeta.setText("Difficulty: ELITE | Category: BOSS FIGHT");
        this.tvMissionThreat.setText(
                "Dragon HP: " + this.dragonMission.getThreatHp()
                        + " | Power: " + this.dragonMission.getThreatSkill()
                        + " | Armor: " + this.dragonMission.getThreatResilience()
        );
        this.tvMissionCrewLimit.setText("Allowed Crew: 1 to " + this.dragonMission.getMaxCrew());
        this.tvSelectedCount.setText("Selected: 0 / " + this.dragonMission.getMaxCrew());
    }

    private void loadSelectableCrew() {
        List<CrewMember> allCrew = Storage.getInstance().getAllCrewMembers();
        List<CrewMember> selectableCrew = new ArrayList<>();

        for (int i = 0; i < allCrew.size(); i = i + 1) {
            CrewMember crewMember = allCrew.get(i);
            if (this.isCrewAvailable(crewMember) == true) {
                selectableCrew.add(crewMember);
            }
        }

        this.crewAdapter.submitList(selectableCrew);
    }

    private boolean isCrewAvailable(CrewMember crewMember) {
        if (crewMember.getHp() <= 0) {
            return false;
        }
        
        Location loc = crewMember.getLocation();
        if (loc == Location.QUARTERS) {
            return true;
        }
        
        return false;
    }

    private void startDragonBattle() {
        List<CrewMember> selectedCrew = this.crewAdapter.getSelectedCrew();
        String validationMessage = MissionEngine.getInstance().validateMissionSelection(this.dragonMission, selectedCrew);

        if (validationMessage != null) {
            Toast.makeText(this.requireContext(), validationMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        MissionEngine.getInstance().setSelectedMission(this.dragonMission);
        MissionEngine.getInstance().setSelectedCrew(selectedCrew);

        ((MainActivity) this.requireActivity()).openFragment(
                DragonBattleFragment.newInstance(this.dragonMission),
                true
        );
    }
}
