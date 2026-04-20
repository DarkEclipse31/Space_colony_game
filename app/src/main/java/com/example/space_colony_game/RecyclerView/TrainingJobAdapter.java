package com.example.space_colony_game.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.space_colony_game.R;
import com.example.space_colony_game.model.CrewMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrainingJobAdapter extends RecyclerView.Adapter<TrainingJobAdapter.TrainingJobViewHolder> {

    public interface OnTrainingJobActionListener {
        void onTrainingJobAction(CrewMember crewMember);
    }

    private final boolean testMode;
    private final OnTrainingJobActionListener listener;
    private final List<CrewMember> activeTrainingCrew = new ArrayList<>();

    public TrainingJobAdapter(boolean testMode, OnTrainingJobActionListener listener) {
        this.testMode = testMode;
        this.listener = listener;
    }

    public void submitList(List<CrewMember> crewList) {
        activeTrainingCrew.clear();
        if (crewList != null) {
            activeTrainingCrew.addAll(crewList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrainingJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_training_job, parent, false);
        return new TrainingJobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingJobViewHolder holder, int position) {
        CrewMember crewMember = activeTrainingCrew.get(position);
        long now = System.currentTimeMillis();
        long totalMillis = crewMember.getTrainingDurationMillis(testMode);
        long remainingMillis = crewMember.getRemainingTrainingMillis(now);

        holder.ivCrewPortrait.setImageResource(crewMember.getProfileImageId());
        holder.tvCrewName.setText(crewMember.getName());
        holder.tvCrewMeta.setText(
                "ROLE: " + crewMember.getRole().getDisplayName()
                        + "   LVL " + crewMember.getLevel()
                        + "   HP " + crewMember.getHp() + "/" + crewMember.getMaxHp()
        );

        if (crewMember.isTrainingComplete(now) || crewMember.isTrainingRewardReady()) {
            holder.tvTrainingTime.setText("Ready to collect EXP");
            holder.btnTrainingAction.setText("BACK");
            holder.btnTrainingAction.setEnabled(true);
            holder.pbTrainingProgress.setProgress(100);
        } else {
            holder.tvTrainingTime.setText("Time left: " + formatDuration(remainingMillis));
            holder.btnTrainingAction.setText("WAIT");
            holder.btnTrainingAction.setEnabled(true);
            holder.pbTrainingProgress.setProgress(calculateProgress(totalMillis, remainingMillis));
        }

        holder.btnTrainingAction.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrainingJobAction(crewMember);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrainingJobAction(crewMember);
            }
        });
    }

    @Override
    public int getItemCount() {
        return activeTrainingCrew.size();
    }

    private int calculateProgress(long totalMillis, long remainingMillis) {
        if (totalMillis <= 0L) {
            return 0;
        }

        long done = totalMillis - remainingMillis;
        if (done < 0L) {
            done = 0L;
        }
        if (done > totalMillis) {
            done = totalMillis;
        }

        return (int) ((done * 100L) / totalMillis);
    }

    private String formatDuration(long millis) {
        if (millis <= 0L) {
            return "READY";
        }

        long totalSeconds = Math.max(1L, millis / 1000L);

        if (testMode) {
            return totalSeconds + " sec";
        }

        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    static class TrainingJobViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCrewPortrait;
        TextView tvCrewName;
        TextView tvCrewMeta;
        TextView tvTrainingTime;
        ProgressBar pbTrainingProgress;
        Button btnTrainingAction;

        TrainingJobViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCrewPortrait = itemView.findViewById(R.id.ivTrainingCrewPortrait);
            tvCrewName = itemView.findViewById(R.id.tvTrainingCrewName);
            tvCrewMeta = itemView.findViewById(R.id.tvTrainingCrewMeta);
            tvTrainingTime = itemView.findViewById(R.id.tvTrainingTime);
            pbTrainingProgress = itemView.findViewById(R.id.pbTrainingProgress);
            btnTrainingAction = itemView.findViewById(R.id.btnTrainingAction);
        }
    }
}