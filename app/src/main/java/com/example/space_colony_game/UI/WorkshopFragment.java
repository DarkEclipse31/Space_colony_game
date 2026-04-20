package com.example.space_colony_game.UI;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.space_colony_game.MainActivity;
import com.example.space_colony_game.R;
import com.example.space_colony_game.RecyclerView.WorkshopJobAdapter;
import com.example.space_colony_game.data.GameState;
import com.example.space_colony_game.logic.WorkshopManager;
import com.example.space_colony_game.model.Gadget;
import com.example.space_colony_game.model.GadgetType;
import com.example.space_colony_game.model.Scientist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class WorkshopFragment extends Fragment {

    private TextView tvWorkshopCoins;
    private TextView tvWorkshopInfo;
    private TextView tvWorkshopSelectedScientist;
    private TextView tvWorkshopSelectedGadget;
    private TextView tvWorkshopNoJobs;
    private ImageView ivWorkshopPreview;
    private RecyclerView rvWorkshopJobs;

    private WorkshopJobAdapter workshopJobAdapter;

    private List<Scientist> availableScientists;
    private int selectedScientistIndex = 0;
    private GadgetType selectedGadgetType = GadgetType.MEDICINE;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            refreshUi();
            timerHandler.postDelayed(this, 1000L);
        }
    };

    public WorkshopFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workshop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvWorkshopCoins = view.findViewById(R.id.tvWorkshopCoins);
        tvWorkshopInfo = view.findViewById(R.id.tvWorkshopInfo);
        tvWorkshopSelectedScientist = view.findViewById(R.id.tvWorkshopSelectedScientist);
        tvWorkshopSelectedGadget = view.findViewById(R.id.tvWorkshopSelectedGadget);
        tvWorkshopNoJobs = view.findViewById(R.id.tvWorkshopNoJobs);
        ivWorkshopPreview = view.findViewById(R.id.ivWorkshopPreview);
        rvWorkshopJobs = view.findViewById(R.id.rvWorkshopJobs);

        rvWorkshopJobs.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvWorkshopJobs.setNestedScrollingEnabled(false);
        workshopJobAdapter = new WorkshopJobAdapter();
        rvWorkshopJobs.setAdapter(workshopJobAdapter);

        Button btnBack = view.findViewById(R.id.btnBackFromWorkshop);
        Button btnPrevScientist = view.findViewById(R.id.btnPrevScientist);
        Button btnNextScientist = view.findViewById(R.id.btnNextScientist);
        Button btnMedicine = view.findViewById(R.id.btnGadgetMedicine);
        Button btnRifle = view.findViewById(R.id.btnGadgetRifle);
        Button btnArmour = view.findViewById(R.id.btnGadgetArmour);
        Button btnPotion = view.findViewById(R.id.btnGadgetPotion);
        Button btnGenerate = view.findViewById(R.id.btnGenerateGadget);
        Button btnRetrieve = view.findViewById(R.id.btnRetrieveGadget);

        btnBack.setOnClickListener(v ->
                ((MainActivity) requireActivity()).openFragment(new QuartersFragment(), false)
        );

        btnPrevScientist.setOnClickListener(v -> {
            if (availableScientists == null || availableScientists.isEmpty()) {
                return;
            }
            selectedScientistIndex--;
            if (selectedScientistIndex < 0) {
                selectedScientistIndex = availableScientists.size() - 1;
            }
            refreshUi();
        });

        btnNextScientist.setOnClickListener(v -> {
            if (availableScientists == null || availableScientists.isEmpty()) {
                return;
            }
            selectedScientistIndex++;
            if (selectedScientistIndex >= availableScientists.size()) {
                selectedScientistIndex = 0;
            }
            refreshUi();
        });

        btnMedicine.setOnClickListener(v -> {
            selectedGadgetType = GadgetType.MEDICINE;
            refreshUi();
        });

        btnRifle.setOnClickListener(v -> {
            selectedGadgetType = GadgetType.RIFLE;
            refreshUi();
        });

        btnArmour.setOnClickListener(v -> {
            selectedGadgetType = GadgetType.ARMOUR;
            refreshUi();
        });

        btnPotion.setOnClickListener(v -> {
            selectedGadgetType = GadgetType.POTION;
            refreshUi();
        });

        btnGenerate.setOnClickListener(v -> startCrafting());
        btnRetrieve.setOnClickListener(v -> retrieveFinishedItems());

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
        tvWorkshopCoins.setText(GameState.getInstance().getCoins() + " COINS");

        availableScientists = WorkshopManager.getAvailableScientists();
        if (availableScientists != null && !availableScientists.isEmpty()) {
            if (selectedScientistIndex >= availableScientists.size()) {
                selectedScientistIndex = 0;
            }
        } else {
            selectedScientistIndex = 0;
        }

        Scientist selectedScientist = getSelectedScientist();
        if (selectedScientist == null) {
            tvWorkshopSelectedScientist.setText("Scientist: None available");
            tvWorkshopInfo.setText(
                    "Only Scientists can craft gadgets.\n" + "Each Scientist can craft 1 gadget.\n" +
                            "No available Scientist right now."
            );
        } else {
            int chance = WorkshopManager.getDuplicateChancePercent(selectedScientist);
            long duration = WorkshopManager.getCraftDurationMillis(
                    selectedScientist,
                    selectedGadgetType,
                    WorkshopManager.TEST_MODE
            );

            tvWorkshopSelectedScientist.setText(
                    "Scientist: " + selectedScientist.getName()
                            + " | LV " + selectedScientist.getLevel()
                            + " | Duplicate: " + chance + "%"
            );

            tvWorkshopInfo.setText(
                    "Only Scientists can craft gadgets.\n"
                            + "Each Scientist can craft 1 gadget.\n" + "Time decreases with level.\n"
                            + "Duplicate chance increases with level.\n" + "Craft time: "
                            + formatDuration(duration)
            );
        }

        Gadget preview = createPreviewGadget(selectedGadgetType);

        String valueLabel;
        switch (preview.getType()) {
            case MEDICINE:
                valueLabel = "Heal: +";
                break;
            case RIFLE:
                valueLabel = "Attack: +";
                break;
            case ARMOUR:
                valueLabel = "Protection: ";
                break;
            case POTION:
                valueLabel = "Revive: ";
                break;
            default:
                valueLabel = "Value: ";
                break;
        }

        tvWorkshopSelectedGadget.setText(
                preview.getName()
                        + " | Cost: " + preview.getCraftCost()
                        + " | " + valueLabel + preview.getEffectValue()
        );
        ivWorkshopPreview.setImageResource(preview.getIconId());

        refreshActiveJobsSection();
    }

    private void refreshActiveJobsSection() {
        List<WorkshopManager.CraftingJob> jobs = new ArrayList<>(WorkshopManager.getActiveJobs());

        Collections.sort(jobs, new Comparator<WorkshopManager.CraftingJob>() {
            @Override
            public int compare(WorkshopManager.CraftingJob job1, WorkshopManager.CraftingJob job2) {
                long now = System.currentTimeMillis();
                long left1 = job1.getRemainingMillis(now);
                long left2 = job2.getRemainingMillis(now);
                return Long.compare(left1, left2);
            }
        });

        if (jobs.isEmpty()) {
            tvWorkshopNoJobs.setVisibility(View.VISIBLE);
            rvWorkshopJobs.setVisibility(View.GONE);
            workshopJobAdapter.submitJobs(new ArrayList<>());
        } else {
            tvWorkshopNoJobs.setVisibility(View.GONE);
            rvWorkshopJobs.setVisibility(View.VISIBLE);
            workshopJobAdapter.submitJobs(jobs);
        }
    }

    private void startCrafting() {
        Scientist scientist = getSelectedScientist();
        if (scientist == null) {
            Toast.makeText(requireContext(), "No available Scientist.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = WorkshopManager.startCrafting(scientist, selectedGadgetType);
        if (!success) {
            Toast.makeText(requireContext(),
                    "Unable to start crafting. Check coins or scientist availability.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(requireContext(),
                scientist.getName() + " started crafting " + selectedGadgetType.name(),
                Toast.LENGTH_SHORT).show();
        refreshUi();
    }

    private void retrieveFinishedItems() {
        int collected = WorkshopManager.collectCompletedJobs();

        if (collected <= 0) {
            Toast.makeText(
                    requireContext(),
                    "No completed gadgets to retrieve yet.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Toast.makeText(
                requireContext(),
                "Retrieved " + collected + " gadget(s).",
                Toast.LENGTH_SHORT
        ).show();

        refreshUi();
    }

    private Scientist getSelectedScientist() {
        if (availableScientists == null || availableScientists.isEmpty()) {
            return null;
        }
        return availableScientists.get(selectedScientistIndex);
    }

    private Gadget createPreviewGadget(GadgetType type) {
        return new Gadget(type);
    }

    private String formatDuration(long millis) {
        long totalSeconds = Math.max(1, millis / 1000L);

        if (WorkshopManager.TEST_MODE) {
            return totalSeconds + " sec";
        }

        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}