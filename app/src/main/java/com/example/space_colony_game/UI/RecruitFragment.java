package com.example.space_colony_game.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.space_colony_game.MainActivity;
import com.example.space_colony_game.R;
import com.example.space_colony_game.RecyclerView.RecruitCandidateAdapter;
import com.example.space_colony_game.data.GameState;
import com.example.space_colony_game.data.SaveLoadManager;
import com.example.space_colony_game.data.Storage;
import com.example.space_colony_game.logic.RecruitmentManager;
import com.example.space_colony_game.model.CrewMember;
import com.example.space_colony_game.model.RecruitCandidate;

import java.util.ArrayList;
import java.util.List;

public class RecruitFragment extends Fragment {

    private TextView tvRecruitCoins;
    private RecyclerView rvRecruitCandidates;
    private RecruitCandidateAdapter adapter;
    private final List<RecruitCandidate> candidateList = new ArrayList<>();

    public RecruitFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recruit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvRecruitCoins = view.findViewById(R.id.tvRecruitCoins);
        rvRecruitCandidates = view.findViewById(R.id.rvRecruitCandidates);
        Button btnBack = view.findViewById(R.id.btnBackFromRecruit);

        loadRandomCandidates();

        adapter = new RecruitCandidateAdapter(candidateList, (candidate, position) -> {
            if (!Storage.getInstance().canRecruitMore()) {
                Toast.makeText(requireContext(), "Crew limit reached (8)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Use the actual cost from the candidate instead of a hardcoded 200
            int cost = candidate.getCost();
            if (!GameState.getInstance().spendCoins(cost)) {
                Toast.makeText(requireContext(), "Not enough coins", Toast.LENGTH_SHORT).show();
                return;
            }

            CrewMember newCrew = Storage.getInstance().createCrewMember(
                    candidate.getName(),
                    candidate.getRole(),
                    candidate.getProfileImageResId()
            );

            if (newCrew == null) {
                Toast.makeText(requireContext(), "Could not recruit crew", Toast.LENGTH_SHORT).show();
                GameState.getInstance().addCoins(cost);
                return;
            }

            newCrew.addExp((candidate.getLevel() - 1) * 40);
            
            // FIX: Add the new crew member to the storage list!
            Storage.getInstance().addCrewMember(newCrew);
            
            // Save game state so the new crew member is persisted
            SaveLoadManager.saveGame(requireContext());

            adapter.removeAt(position);
            updateCoins();

            Toast.makeText(
                    requireContext(),
                    candidate.getName() + " Recruited",
                    Toast.LENGTH_SHORT
            ).show();
        });

        rvRecruitCandidates.setAdapter(adapter);

        btnBack.setOnClickListener(v ->
                ((MainActivity) requireActivity()).openFragment(new QuartersFragment(), false)
        );

        updateCoins();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCoins();
    }

    private void loadRandomCandidates() {
        candidateList.clear();
        candidateList.addAll(RecruitmentManager.generateRandomRecruitPage());
    }

    private void updateCoins() {
        tvRecruitCoins.setText(GameState.getInstance().getCoins() + " COINS");
    }
}