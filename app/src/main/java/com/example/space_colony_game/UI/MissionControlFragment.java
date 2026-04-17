package com.example.space_colony_game.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.space_colony_game.MainActivity;
import com.example.space_colony_game.R;
import com.example.space_colony_game.RecyclerView.MissionAdapter;
import com.example.space_colony_game.data.GameState;
import com.example.space_colony_game.logic.MissionEngine;
import com.example.space_colony_game.model.Mission;

import java.util.List;

public class MissionControlFragment extends Fragment {

    private TextView tvCoins;
    private RecyclerView rvMissionList;
    private MissionAdapter missionAdapter;

    public MissionControlFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mission_control, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCoins = view.findViewById(R.id.tvCoins);
        rvMissionList = view.findViewById(R.id.rvMissionList);

        // Setup Back Button
        Button btnBack = findBackButton(view);
        if (btnBack != null) {
            btnBack.setOnClickListener(v ->
                    ((MainActivity) requireActivity()).openFragment(new QuartersFragment(), false)
            );
        }

        // Setup RecyclerView
        rvMissionList.setLayoutManager(new LinearLayoutManager(requireContext()));
        missionAdapter = new MissionAdapter(this::onMissionClicked);
        rvMissionList.setAdapter(missionAdapter);

        refreshUi();
    }

    private void onMissionClicked(Mission mission) {
        ((MainActivity) requireActivity()).openFragment(
                MissionPartySetupFragment.newInstance(mission),
                true
        );
    }

    private void refreshUi() {
        GameState gameState = GameState.getInstance();
        tvCoins.setText(gameState.getCoins() + " COINS");

        List<Mission> missions = MissionEngine.getInstance().getCurrentMissionBoard();
        missionAdapter.submitList(missions);
    }

    @Nullable
    private Button findBackButton(@NonNull View root) {
        int[] candidateIds = new int[]{
                getIdByName("btnBackToQuartersFromMission"),
                getIdByName("btnBackFromMissionControl"),
                getIdByName("btnBackMissionControl"),
                getIdByName("btnBack")
        };

        for (int id : candidateIds) {
            if (id != 0) {
                View found = root.findViewById(id);
                if (found instanceof Button) {
                    return (Button) found;
                }
            }
        }

        return null;
    }

    private int getIdByName(@NonNull String name) {
        if (getContext() == null) return 0;
        return getResources().getIdentifier(name, "id", requireContext().getPackageName());
    }
}