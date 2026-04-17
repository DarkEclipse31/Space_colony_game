package com.example.space_colony_game.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.space_colony_game.MainActivity;
import com.example.space_colony_game.R;
import com.example.space_colony_game.RecyclerView.QuartersCrewAdapter;
import com.example.space_colony_game.data.GameState;
import com.example.space_colony_game.data.SaveLoadManager;
import com.example.space_colony_game.data.Storage;
import com.example.space_colony_game.model.CrewMember;
import com.example.space_colony_game.model.Location;
import com.example.space_colony_game.model.Pilot;

import java.util.ArrayList;
import java.util.List;

public class QuartersFragment extends Fragment {

    private TextView tvCoins;
    private TextView tvMissionCount;
    private TextView tvTotalCrewCount;
    private TextView tvWins;
    private TextView tvLosses;
    private RecyclerView rvCrew;

    private QuartersCrewAdapter crewAdapter;

    public QuartersFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quarters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCoins = view.findViewById(R.id.tvCoins);
        tvMissionCount = view.findViewById(R.id.tvMissionCount);
        tvTotalCrewCount = view.findViewById(R.id.tvTotalCrewCount);
        tvWins = view.findViewById(R.id.tvWins);
        tvLosses = view.findViewById(R.id.tvLosses);
        rvCrew = view.findViewById(R.id.rvCrew);

        View btnHelp = view.findViewById(R.id.btnShowHelp);
        if (btnHelp != null) {
            btnHelp.setOnClickListener(v -> 
                ((MainActivity) requireActivity()).openFragment(new InstructionsFragment(), true)
            );
        }

        View btnReset = view.findViewById(R.id.btnResetGame);
        if (btnReset != null) {
            btnReset.setOnClickListener(v -> showResetConfirmation());
        }

        rvCrew.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCrew.setNestedScrollingEnabled(false);

        crewAdapter = new QuartersCrewAdapter(
                requireContext(),
                new ArrayList<>(),
                this::showDeleteConfirmation
        );
        rvCrew.setAdapter(crewAdapter);

        view.findViewById(R.id.navLunaris).setOnClickListener(v ->
                ((MainActivity) requireActivity()).openFragment(new RecruitFragment(), true)
        );

        view.findViewById(R.id.navTraining).setOnClickListener(v ->
                ((MainActivity) requireActivity()).openFragment(new TrainingFragment(), true)
        );

        view.findViewById(R.id.navHospital).setOnClickListener(v ->
                ((MainActivity) requireActivity()).openFragment(new HospitalFragment(), true)
        );

        view.findViewById(R.id.navWorkshop).setOnClickListener(v ->
                ((MainActivity) requireActivity()).openFragment(new WorkshopFragment(), true)
        );

        view.findViewById(R.id.navMission).setOnClickListener(v ->
                ((MainActivity) requireActivity()).openFragment(new MissionControlFragment(), true)
        );

        view.findViewById(R.id.navAethra).setOnClickListener(v ->
                ((MainActivity) requireActivity()).openFragment(new DragonFragment(), true)
        );

        refreshUi();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUi();
    }

    private void refreshUi() {
        GameState gameState = GameState.getInstance();
        Storage storage = Storage.getInstance();

        tvCoins.setText(gameState.getCoins() + " COINS");
        tvMissionCount.setText(String.valueOf(gameState.getTotalMissions()));
        tvTotalCrewCount.setText(String.valueOf(storage.getTotalCrewCount()));
        tvWins.setText(String.valueOf(gameState.getTotalWins()));
        tvLosses.setText(String.valueOf(gameState.getTotalLosses()));

        List<CrewMember> allCrew = storage.getAllCrewMembers();
        crewAdapter.submitList(allCrew);
    }

    private void showDeleteConfirmation(CrewMember crewMember) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Crew Member")
                .setMessage("Delete " + crewMember.getName() + "?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = Storage.getInstance().deleteCrewMember(crewMember.getId());

                    if (!deleted) {
                        Toast.makeText(requireContext(),
                                "Cannot delete the last Pilot.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(requireContext(),
                            crewMember.getName() + " deleted.",
                            Toast.LENGTH_SHORT).show();

                    refreshUi();
                })
                .show();
    }

    private void showResetConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Reset Game")
                .setMessage("Are you sure you want to reset all progress? This cannot be undone.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Reset", (dialog, which) -> showFinalResetConfirmation())
                .show();
    }

    private void showFinalResetConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("WARNING")
                .setMessage("Restart the game")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> performReset())
                .show();
    }

    private void performReset() {
        SaveLoadManager.deleteSave(requireContext());
        Storage.getInstance().resetGameState();

        GameState state = GameState.getInstance();
        state.setCoins(300);
        int id = Storage.getInstance().nextId();
        Pilot nova = new Pilot(id, "Nova");
        nova.setLocation(Location.QUARTERS);
        Storage.getInstance().addCrewMember(nova);

        SaveLoadManager.saveGame(requireContext());
        Toast.makeText(requireContext(), "Game reset.", Toast.LENGTH_SHORT).show();
        
        // Return to Start Screen after reset
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openFragment(new StartFragment(), false);
        }
    }
}
