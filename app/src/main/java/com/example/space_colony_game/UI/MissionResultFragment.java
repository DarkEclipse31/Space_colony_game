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

import com.example.space_colony_game.MainActivity;
import com.example.space_colony_game.R;

public class MissionResultFragment extends Fragment {

    public MissionResultFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mission_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView tvStatus = view.findViewById(R.id.tvMissionResultStatus);
        TextView tvInfo = view.findViewById(R.id.tvMissionResultInfo);
        TextView tvSurvivors = view.findViewById(R.id.tvMissionResultSurvivors);
        Button btnBack = view.findViewById(R.id.btnBackToQuartersFromResult);

        tvStatus.setText("SUCCESS");
        tvInfo.setText("Mission reward details will appear here");
        tvSurvivors.setText("Crew battle result will appear here");

        btnBack.setOnClickListener(v ->
                ((MainActivity) requireActivity()).openFragment(new QuartersFragment(), false)
        );
    }
}