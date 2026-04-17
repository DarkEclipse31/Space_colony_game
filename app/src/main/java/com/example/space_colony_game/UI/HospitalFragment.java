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
import androidx.recyclerview.widget.RecyclerView;

import com.example.space_colony_game.MainActivity;
import com.example.space_colony_game.R;
import com.example.space_colony_game.RecyclerView.HospitalCrewAdapter;
import com.example.space_colony_game.data.GameState;
import com.example.space_colony_game.data.Storage;
import com.example.space_colony_game.model.CrewMember;
import com.example.space_colony_game.model.Location;

import java.util.ArrayList;
import java.util.List;

public class HospitalFragment extends Fragment {

    private static final boolean TEST_MODE = true;
    private static final int REVIVE_COST = 100;

    private TextView tvHospitalCoins;
    private TextView tvHospitalSummary;
    private RecyclerView rvHospitalCrew;
    private HospitalCrewAdapter hospitalCrewAdapter;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            refreshUi();
            timerHandler.postDelayed(this, 1000L);
        }
    };

    public HospitalFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hospital, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvHospitalCoins = view.findViewById(R.id.tvHospitalCoins);
        tvHospitalSummary = view.findViewById(R.id.tvHospitalSummary);
        rvHospitalCrew = view.findViewById(R.id.rvHospitalCrew);

        Button btnBack = view.findViewById(R.id.btnBackFromHospital);

        hospitalCrewAdapter = new HospitalCrewAdapter(
                new ArrayList<>(),
                REVIVE_COST,
                TEST_MODE,
                this::handleHospitalAction
        );
        rvHospitalCrew.setAdapter(hospitalCrewAdapter);

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
        tvHospitalCoins.setText(GameState.getInstance().getCoins() + " COINS");

        List<CrewMember> hospitalCrew = getCrewNeedingTreatment();
        hospitalCrewAdapter.setCrewList(hospitalCrew);

        int injuredCount = 0;
        int defeatedCount = 0;

        for (CrewMember crewMember : hospitalCrew) {
            if (crewMember.getHp() <= 0) {
                defeatedCount++;
            } else {
                injuredCount++;
            }
        }

        if (hospitalCrew.isEmpty()) {
            tvHospitalSummary.setText("No crew currently need treatment.");
        } else {
            tvHospitalSummary.setText(
                    "Injured: " + injuredCount +
                            "\nDefeated: " + defeatedCount +
                            "\nHeal base time: 5 sec" +
                            "\nRevive: " + REVIVE_COST + " coins"
            );
        }
    }

    private List<CrewMember> getCrewNeedingTreatment() {
        List<CrewMember> allCrew = Storage.getInstance().getAllCrewMembers();
        List<CrewMember> result = new ArrayList<>();

        for (CrewMember crewMember : allCrew) {
            if (crewMember.getHp() < crewMember.getMaxHp() || crewMember.getLocation() == Location.HOSPITAL) {
                result.add(crewMember);
            }
        }

        return result;
    }

    private void handleHospitalAction(CrewMember crewMember) {
        long now = System.currentTimeMillis();

        if (crewMember.getHp() <= 0) {
            if (!GameState.getInstance().spendCoins(REVIVE_COST)) {
                Toast.makeText(requireContext(), "Not enough coins to revive.", Toast.LENGTH_SHORT).show();
                return;
            }

            crewMember.clearHealingState();
            int reviveHp = Math.max(10, (int) Math.ceil(crewMember.getMaxHp() * 0.3));
            crewMember.heal(reviveHp);
            crewMember.setLocation(Location.QUARTERS);

            Toast.makeText(requireContext(),
                    crewMember.getName() + " revived successfully.",
                    Toast.LENGTH_SHORT).show();

            refreshUi();
            return;
        }

        if (crewMember.isHealingComplete(now) || crewMember.isHealingReady()) {
            crewMember.markHealingReady();
            crewMember.restoreToFullHp();
            crewMember.clearHealingState();
            crewMember.setLocation(Location.QUARTERS);

            Toast.makeText(requireContext(),
                    crewMember.getName() + " healed successfully.",
                    Toast.LENGTH_SHORT).show();

            refreshUi();
            return;
        }

        if (crewMember.isHealingInProgress(now)) {
            Toast.makeText(requireContext(),
                    crewMember.getName() + " is still healing.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        crewMember.setLocation(Location.HOSPITAL);
        crewMember.startHealing(now, TEST_MODE);

        Toast.makeText(requireContext(),
                crewMember.getName() + " started healing.",
                Toast.LENGTH_SHORT).show();

        refreshUi();
    }
}