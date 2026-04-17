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
import com.example.space_colony_game.model.CrewRole;
import com.example.space_colony_game.model.Mission;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MissionPartySetupFragment extends Fragment {

    private static final String ARG_MISSION = "arg_mission";

    private Mission mission;

    private TextView tvMissionTitle;
    private TextView tvMissionMeta;
    private TextView tvMissionThreat;
    private TextView tvMissionCrewLimit;
    private TextView tvSelectedCount;
    private RecyclerView rvSelectableCrew;

    private MissionSetupCrewAdapter crewAdapter;
    private final ArrayList<CrewMember> selectedCrew = new ArrayList<>();

    public MissionPartySetupFragment() {
    }

    public static MissionPartySetupFragment newInstance(Mission mission) {
        MissionPartySetupFragment fragment = new MissionPartySetupFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MISSION, mission);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Object value = getArguments().getSerializable(ARG_MISSION);
            if (value instanceof Mission) {
                mission = (Mission) value;
            }
        }

        if (mission == null) {
            mission = MissionEngine.getInstance().getSelectedMission();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mission_party_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvMissionTitle = view.findViewById(R.id.tvMissionTitle);
        tvMissionMeta = view.findViewById(R.id.tvMissionMeta);
        tvMissionThreat = view.findViewById(R.id.tvMissionThreat);
        tvMissionCrewLimit = view.findViewById(R.id.tvMissionCrewLimit);
        tvSelectedCount = view.findViewById(R.id.tvSelectedCount);
        rvSelectableCrew = view.findViewById(R.id.rvSelectableCrew);

        if (mission == null) {
            Toast.makeText(requireContext(), "Mission data missing.", Toast.LENGTH_SHORT).show();
            ((MainActivity) requireActivity()).openFragment(new MissionControlFragment(), false);
            return;
        }

        rvSelectableCrew.setLayoutManager(new LinearLayoutManager(requireContext()));

        crewAdapter = new MissionSetupCrewAdapter(updatedSelection -> {
            selectedCrew.clear();
            selectedCrew.addAll(updatedSelection);
            tvSelectedCount.setText("Selected: " + selectedCrew.size() + " / " + mission.getMaxCrew());
        });

        crewAdapter.setMaxSelection(mission.getMaxCrew());
        rvSelectableCrew.setAdapter(crewAdapter);

        view.findViewById(R.id.btnBackMissionControl).setOnClickListener(v ->
                ((MainActivity) requireActivity()).openFragment(new MissionControlFragment(), false)
        );

        view.findViewById(R.id.btnStartMission).setOnClickListener(v -> startMission());

        bindMissionData();
        loadSelectableCrew();
    }

    private void bindMissionData() {
        if (mission == null) {
            Toast.makeText(requireContext(), "Mission data missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        tvMissionTitle.setText(mission.getTitle());
        tvMissionMeta.setText(
                "Difficulty: " + mission.getDifficultyText()
                        + " | Category: " + mission.getCategoryText()
        );
        tvMissionThreat.setText(
                "Threat HP: " + mission.getThreatHp()
                        + " | Skill: " + mission.getThreatSkill()
                        + " | Resilience: " + mission.getThreatResilience()
        );
        tvMissionCrewLimit.setText("Allowed Crew: 1 to " + mission.getMaxCrew());
        tvSelectedCount.setText("Selected: 0 / " + mission.getMaxCrew());
    }

    private void loadSelectableCrew() {
        List<CrewMember> allCrew = Storage.getInstance().getAllCrewMembers();
        List<CrewMember> selectableCrew = new ArrayList<>();

        for (CrewMember crewMember : allCrew) {
            if (isCrewSelectable(crewMember)) {
                selectableCrew.add(crewMember);
            }
        }

        crewAdapter.submitList(selectableCrew);
    }

    private boolean isCrewSelectable(CrewMember crewMember) {
        if (crewMember == null) {
            return false;
        }

        if (crewMember.getRole() == CrewRole.SCIENTIST) {
            return false;
        }

        int currentHp = readInt(crewMember, crewMember.getHp(), "getCurrentHp", "getHp", "getEnergy");
        if (currentHp <= 0) {
            return false;
        }

        String status = readString(
                crewMember,
                crewMember.getLocation() == null ? "" : crewMember.getLocation().name(),
                "getLocation",
                "getStatus",
                "getCurrentLocation",
                "getCurrentStatus"
        ).trim().toUpperCase(Locale.US);

        return !status.equals("MISSION")
                && !status.equals("HOSPITAL")
                && !status.equals("TRAINING");
    }

    private void startMission() {
        if (mission == null) {
            Toast.makeText(requireContext(), "Mission data missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCrew.isEmpty()) {
            Toast.makeText(requireContext(), "Select at least 1 crew member.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCrew.size() > mission.getMaxCrew()) {
            Toast.makeText(requireContext(), "Too many crew selected for this mission.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!hasPilot(selectedCrew)) {
            Toast.makeText(requireContext(), "Every mission team must include at least 1 Pilot.", Toast.LENGTH_SHORT).show();
            return;
        }

        MissionEngine.getInstance().setSelectedMission(mission);
        MissionEngine.getInstance().setSelectedCrew(new ArrayList<>(selectedCrew));

        ((MainActivity) requireActivity()).openFragment(
                BattleFragment.newInstance(mission, new ArrayList<>(selectedCrew)),
                true
        );
    }

    private boolean hasPilot(List<CrewMember> crewList) {
        for (CrewMember crewMember : crewList) {
            if (crewMember.getRole() == CrewRole.PILOT) {
                return true;
            }
        }
        return false;
    }

    private String readString(CrewMember crew, String fallback, String... methodNames) {
        for (String methodName : methodNames) {
            try {
                Method method = crew.getClass().getMethod(methodName);
                Object value = method.invoke(crew);
                if (value != null) {
                    return value.toString();
                }
            } catch (Exception ignored) {
            }
        }
        return fallback;
    }

    private int readInt(CrewMember crew, int fallback, String... methodNames) {
        for (String methodName : methodNames) {
            try {
                Method method = crew.getClass().getMethod(methodName);
                Object value = method.invoke(crew);

                if (value instanceof Number) {
                    return ((Number) value).intValue();
                }

                if (value != null) {
                    return Integer.parseInt(value.toString());
                }
            } catch (Exception ignored) {
            }
        }
        return fallback;
    }
}
