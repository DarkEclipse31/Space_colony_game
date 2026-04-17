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
import com.example.space_colony_game.logic.WorkshopManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CraftingJobAdapter extends RecyclerView.Adapter<CraftingJobAdapter.JobViewHolder> {

    private List<WorkshopManager.CraftingJob> jobList = new ArrayList<>();
    private final boolean testMode;

    public CraftingJobAdapter(boolean testMode) {
        this.testMode = testMode;
    }

    public void updateJobs(List<WorkshopManager.CraftingJob> newList) {
        this.jobList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crafting_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        WorkshopManager.CraftingJob job = jobList.get(position);
        holder.tvJobScientist.setText(job.getScientistName().toUpperCase());
        holder.tvJobGadget.setText(job.getGadgetType().getDisplayName());
        holder.ivJobGadgetIcon.setImageResource(job.getGadgetType().getIconResId());
        holder.tvJobOutput.setText("Yield: x" + job.getOutputCount());

        long now = System.currentTimeMillis();
        long remaining = job.getRemainingMillis(now);
        holder.tvJobTime.setText(formatDuration(remaining));

        // Calculate progress
        long total = job.getEndTimeMillis() - (job.getEndTimeMillis() - (testMode ? 5000L : 120000L)); // Approximation
        // Better: we don't have start time in job. Let's just use remaining.
        if (remaining <= 0) {
            holder.pbJobProgress.setProgress(100);
            holder.tvJobTime.setText("READY");
            holder.tvJobTime.setTextColor(0xFF66BB6A);
        } else {
            holder.tvJobTime.setTextColor(0xFFFFFFFF);
            // Since we don't have start time, we can't show real percentage unless we add it to Job.
            // For now, let's just pulse or show it's working.
            holder.pbJobProgress.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    private String formatDuration(long millis) {
        long totalSeconds = Math.max(0, millis / 1000L);
        if (testMode && totalSeconds < 60) {
            return totalSeconds + "s";
        }
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobScientist, tvJobGadget, tvJobTime, tvJobOutput;
        ImageView ivJobGadgetIcon;
        ProgressBar pbJobProgress;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobScientist = itemView.findViewById(R.id.tvJobScientist);
            tvJobGadget = itemView.findViewById(R.id.tvJobGadget);
            tvJobTime = itemView.findViewById(R.id.tvJobTime);
            tvJobOutput = itemView.findViewById(R.id.tvJobOutput);
            ivJobGadgetIcon = itemView.findViewById(R.id.ivJobGadgetIcon);
            pbJobProgress = itemView.findViewById(R.id.pbJobProgress);
        }
    }
}
