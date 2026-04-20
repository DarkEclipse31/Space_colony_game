package com.example.space_colony_game.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.space_colony_game.R;
import com.example.space_colony_game.data.Storage;
import com.example.space_colony_game.logic.WorkshopManager;
import com.example.space_colony_game.model.CrewMember;
import com.example.space_colony_game.model.GadgetType;
import com.example.space_colony_game.model.Scientist;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WorkshopJobAdapter extends RecyclerView.Adapter<WorkshopJobAdapter.WorkshopJobViewHolder> {

    private final List<WorkshopManager.CraftingJob> jobs = new ArrayList<>();

    public void submitJobs(List<WorkshopManager.CraftingJob> newJobs) {
        jobs.clear();
        if (newJobs != null) {
            jobs.addAll(newJobs);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkshopJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crafting_job, parent, false);
        return new WorkshopJobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkshopJobViewHolder holder, int position) {
        WorkshopManager.CraftingJob job = jobs.get(position);

        holder.tvJobScientist.setText(job.getScientistName());
        holder.tvJobOutput.setText("x" + job.getOutputCount());
        holder.ivJobGadgetIcon.setImageResource(job.getGadgetType().getIconId());
        holder.tvJobGadget.setText(job.getGadgetType().getDisplayName());

        long now = System.currentTimeMillis();
        long remainingMillis = job.getRemainingMillis(now);
        long totalDurationMillis = getTotalDurationMillis(job);

        holder.tvJobTime.setText(formatDuration(remainingMillis));

        holder.pbJobProgress.setMax(100);
        holder.pbJobProgress.setProgress(calculateProgressPercent(totalDurationMillis, remainingMillis));
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    private long getTotalDurationMillis(WorkshopManager.CraftingJob job) {
        CrewMember crewMember = Storage.getInstance().getCrewMemberById(job.getScientistId());

        if (crewMember instanceof Scientist) {
            return WorkshopManager.getCraftDurationMillis(
                    (Scientist) crewMember,
                    job.getGadgetType(),
                    WorkshopManager.TEST_MODE
            );
        }

        return getFallbackBaseDuration(job.getGadgetType());
    }

    private long getFallbackBaseDuration(GadgetType gadgetType) {
        if (WorkshopManager.TEST_MODE) {
            switch (gadgetType) {
                case MEDICINE:
                    return 5000L;
                case RIFLE:
                    return 6000L;
                case ARMOUR:
                    return 6000L;
                case POTION:
                    return 7000L;
                default:
                    return 5000L;
            }
        }

        switch (gadgetType) {
            case MEDICINE:
                return 120_000L;
            case RIFLE:
                return 150_000L;
            case ARMOUR:
                return 150_000L;
            case POTION:
                return 180_000L;
            default:
                return 120_000L;
        }
    }

    private int calculateProgressPercent(long totalMillis, long remainingMillis) {
        if (totalMillis <= 0L) {
            return 0;
        }

        long completedMillis = totalMillis - remainingMillis;
        if (completedMillis < 0L) {
            completedMillis = 0L;
        }
        if (completedMillis > totalMillis) {
            completedMillis = totalMillis;
        }

        return (int) ((completedMillis * 100L) / totalMillis);
    }

    private String formatDuration(long millis) {
        if (millis <= 0L) {
            return "READY";
        }

        long totalSeconds = Math.max(1L, millis / 1000L);

        if (WorkshopManager.TEST_MODE) {
            return totalSeconds + " sec";
        }

        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    static class WorkshopJobViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobScientist;
        TextView tvJobOutput;
        ImageView ivJobGadgetIcon;
        TextView tvJobGadget;
        TextView tvJobTime;
        ProgressBar pbJobProgress;

        WorkshopJobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobScientist = itemView.findViewById(R.id.tvJobScientist);
            tvJobOutput = itemView.findViewById(R.id.tvJobOutput);
            ivJobGadgetIcon = itemView.findViewById(R.id.ivJobGadgetIcon);
            tvJobGadget = itemView.findViewById(R.id.tvJobGadget);
            tvJobTime = itemView.findViewById(R.id.tvJobTime);
            pbJobProgress = itemView.findViewById(R.id.pbJobProgress);
        }
    }
}