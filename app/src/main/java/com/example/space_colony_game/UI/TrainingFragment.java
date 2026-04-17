package com.example.space_colony_game.UI;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.space_colony_game.MainActivity;
import com.example.space_colony_game.R;
import com.example.space_colony_game.RecyclerView.TrainingCrewAdapter;
import com.example.space_colony_game.RecyclerView.TrainingJobAdapter;
import com.example.space_colony_game.data.GameState;
import com.example.space_colony_game.data.SaveLoadManager;
import com.example.space_colony_game.data.Storage;
import com.example.space_colony_game.model.CrewMember;
import com.example.space_colony_game.model.Location;

import java.util.ArrayList;
import java.util.List;

public class TrainingFragment extends Fragment {

    private static final boolean TEST_MODE = true;
    private static final int MAX_TRAINING_CREW = 5;
    private static final int TRAINING_EXP_REWARD = 10;

    private TextView tvTrainingCoins;
    private TextView tvTrainingSummary;
    private TextView tvNoAvailableCrew;
    private TextView tvNoTrainingJobs;

    private RecyclerView rvTrainingCrew;
    private RecyclerView rvTrainingJobs;

    private TrainingCrewAdapter trainingCrewAdapter;
    private TrainingJobAdapter trainingJobAdapter;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            refreshUi();
            timerHandler.postDelayed(this, 1000L);
        }
    };

    public TrainingFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTrainingCoins = view.findViewById(R.id.tvTrainingCoins);
        tvTrainingSummary = view.findViewById(R.id.tvTrainingSummary);
        tvNoAvailableCrew = view.findViewById(R.id.tvNoAvailableCrew);
        tvNoTrainingJobs = view.findViewById(R.id.tvNoTrainingJobs);

        rvTrainingCrew = view.findViewById(R.id.rvTrainingCrew);
        rvTrainingJobs = view.findViewById(R.id.rvTrainingJobs);

        Button btnBack = view.findViewById(R.id.btnBackFromTraining);

        rvTrainingCrew.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTrainingCrew.setNestedScrollingEnabled(false);

        rvTrainingJobs.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTrainingJobs.setNestedScrollingEnabled(false);

        trainingCrewAdapter = new TrainingCrewAdapter(
                new ArrayList<>(),
                TEST_MODE,
                this::handleTrainingAction
        );
        rvTrainingCrew.setAdapter(trainingCrewAdapter);

        trainingJobAdapter = new TrainingJobAdapter(TEST_MODE, this::handleTrainingJobAction);
        rvTrainingJobs.setAdapter(trainingJobAdapter);

        btnBack.setOnClickListener(v ->
                ((MainActivity) requireActivity()).openFragment(new QuartersFragment(), false)
        );

        refreshUi();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUi();
        timerHandler.post(timerRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void refreshUi() {
        tvTrainingCoins.setText(GameState.getInstance().getCoins() + " COINS");

        List<CrewMember> availableCrew = getAvailableCrewForTraining();
        List<CrewMember> activeTrainingCrew = getActiveTrainingCrew();
        int currentTrainingCount = activeTrainingCrew.size();

        trainingCrewAdapter.setCrewList(availableCrew);
        trainingCrewAdapter.setCurrentTrainingCount(currentTrainingCount);
        trainingJobAdapter.submitList(activeTrainingCrew);

        if (availableCrew.isEmpty()) {
            tvNoAvailableCrew.setVisibility(View.VISIBLE);
            rvTrainingCrew.setVisibility(View.GONE);
        } else {
            tvNoAvailableCrew.setVisibility(View.GONE);
            rvTrainingCrew.setVisibility(View.VISIBLE);
        }

        if (activeTrainingCrew.isEmpty()) {
            tvNoTrainingJobs.setVisibility(View.VISIBLE);
            rvTrainingJobs.setVisibility(View.GONE);
        } else {
            tvNoTrainingJobs.setVisibility(View.GONE);
            rvTrainingJobs.setVisibility(View.VISIBLE);
        }

        tvTrainingSummary.setText(
                "Training Slots: " + currentTrainingCount + "/" + MAX_TRAINING_CREW +
                        "\nBase Time: 3 sec" +
                        "\nReward: +10 EXP"
        );
    }

    private List<CrewMember> getAvailableCrewForTraining() {
        List<CrewMember> allCrew = Storage.getInstance().getAllCrewMembers();
        List<CrewMember> filtered = new ArrayList<>();

        for (CrewMember crewMember : allCrew) {
            if (crewMember.getLocation() == Location.QUARTERS) {
                filtered.add(crewMember);
            }
        }

        return filtered;
    }

    private List<CrewMember> getActiveTrainingCrew() {
        List<CrewMember> allCrew = Storage.getInstance().getAllCrewMembers();
        List<CrewMember> filtered = new ArrayList<>();

        for (CrewMember crewMember : allCrew) {
            if (crewMember.getLocation() == Location.TRAINING) {
                filtered.add(crewMember);
            }
        }

        return filtered;
    }

    private int getCurrentTrainingCount() {
        return getActiveTrainingCrew().size();
    }

    private void handleTrainingAction(CrewMember crewMember) {
        long now = System.currentTimeMillis();

        if (crewMember.getLocation() != Location.QUARTERS) {
            return;
        }

        if (getCurrentTrainingCount() >= MAX_TRAINING_CREW) {
            Toast.makeText(requireContext(),
                    "Training is full. Maximum is 5 crew.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Storage.getInstance().moveCrewMember(crewMember.getId(), Location.TRAINING);
        crewMember.startTraining(now, TEST_MODE);

        // Save immediately so training state survives app close
        SaveLoadManager.saveGame(requireContext());

        Toast.makeText(requireContext(),
                crewMember.getName() + " started training.",
                Toast.LENGTH_SHORT).show();

        refreshUi();
    }

    private void handleTrainingJobAction(CrewMember crewMember) {
        long now = System.currentTimeMillis();

        if (crewMember.getLocation() != Location.TRAINING) {
            return;
        }

        if (crewMember.isTrainingComplete(now) || crewMember.isTrainingRewardReady()) {
            crewMember.markTrainingRewardReady();
            crewMember.addExp(TRAINING_EXP_REWARD);
            crewMember.clearTrainingState();
            Storage.getInstance().moveCrewMember(crewMember.getId(), Location.QUARTERS);

            // Save immediately so EXP and location persist
            SaveLoadManager.saveGame(requireContext());

            Toast.makeText(requireContext(),
                    crewMember.getName() + " returned and gained " + TRAINING_EXP_REWARD + " EXP.",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(),
                    crewMember.getName() + " is still training.",
                    Toast.LENGTH_SHORT).show();
        }

        refreshUi();
    }
}