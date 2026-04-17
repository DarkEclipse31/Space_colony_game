package com.example.spaceapplication.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spaceapplication.R;
import com.example.spaceapplication.model.CrewMember;
import com.example.spaceapplication.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrainingCrewAdapter extends RecyclerView.Adapter<TrainingCrewAdapter.TrainingCrewViewHolder> {

    public interface OnTrainingActionClickListener {
        void onTrainingAction(CrewMember crewMember);
    }

    private final List<CrewMember> crewList;
    private final OnTrainingActionClickListener listener;
    private final boolean testMode;
    private int currentTrainingCount;

    public TrainingCrewAdapter(List<CrewMember> crewList,
                               boolean testMode,
                               OnTrainingActionClickListener listener) {
        this.crewList = new ArrayList<>(crewList);
        this.testMode = testMode;
        this.listener = listener;
        this.currentTrainingCount = 0;
    }

    public void setCrewList(List<CrewMember> newCrewList) {
        crewList.clear();
        crewList.addAll(newCrewList);
        notifyDataSetChanged();
    }

    public void setCurrentTrainingCount(int currentTrainingCount) {
        this.currentTrainingCount = currentTrainingCount;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrainingCrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_training_crew, parent, false);
        return new TrainingCrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingCrewViewHolder holder, int position) {
        CrewMember crewMember = crewList.get(position);
        long now = System.currentTimeMillis();

        holder.ivTrainingCrewProfile.setImageResource(crewMember.getProfileImageResId());
        holder.tvTrainingCrewName.setText(crewMember.getName().toUpperCase());
        holder.tvTrainingCrewRole.setText("ROLE: " + crewMember.getRole().getDisplayName().toUpperCase());
        holder.tvTrainingCrewStats.setText(
                "LVL " + crewMember.getLevel()
                        + "   HP " + crewMember.getHp() + "/" + crewMember.getMaxHp()
                        + "   EXP " + crewMember.getExp()
        );

        if (crewMember.getLocation() == Location.QUARTERS) {
            holder.tvTrainingCountdown.setText("DURATION: 3 sec");

            if (currentTrainingCount >= 5) {
                holder.btnTrainingAction.setText("FULL");
                holder.btnTrainingAction.setEnabled(false);
                holder.btnTrainingAction.setAlpha(0.6f);
                holder.btnTrainingAction.setOnClickListener(null);
            } else {
                holder.btnTrainingAction.setText("TRAIN");
                holder.btnTrainingAction.setEnabled(true);
                holder.btnTrainingAction.setAlpha(1.0f);
                holder.btnTrainingAction.setOnClickListener(v -> listener.onTrainingAction(crewMember));
            }

        } else if (crewMember.getLocation() == Location.TRAINING) {

            if (crewMember.isTrainingComplete(now) || crewMember.isTrainingRewardReady()) {
                holder.tvTrainingCountdown.setText("Training complete (+10 EXP)");
                holder.btnTrainingAction.setText("RETURN");
                holder.btnTrainingAction.setEnabled(true);
                holder.btnTrainingAction.setAlpha(1.0f);
                holder.btnTrainingAction.setOnClickListener(v -> listener.onTrainingAction(crewMember));

            } else if (crewMember.isTrainingInProgress(now)) {
                holder.tvTrainingCountdown.setText(formatRemainingTime(crewMember.getRemainingTrainingMillis(now)));
                holder.btnTrainingAction.setText("WAIT");
                holder.btnTrainingAction.setEnabled(false);
                holder.btnTrainingAction.setAlpha(0.6f);
                holder.btnTrainingAction.setOnClickListener(null);

            } else {
                holder.tvTrainingCountdown.setText("Ready");
                holder.btnTrainingAction.setText("RETURN");
                holder.btnTrainingAction.setEnabled(true);
                holder.btnTrainingAction.setAlpha(1.0f);
                holder.btnTrainingAction.setOnClickListener(v -> listener.onTrainingAction(crewMember));
            }
        }
    }

    @Override
    public int getItemCount() {
        return crewList.size();
    }

    private String formatTrainingMinutes(double minutes) {
        if (minutes == (long) minutes) {
            return String.format(Locale.getDefault(), "%d min", (long) minutes);
        }
        return String.format(Locale.getDefault(), "%.1f min", minutes);
    }

    private String formatRemainingTime(long remainingMillis) {
        if (testMode) {
            long seconds = Math.max(1, remainingMillis / 1000L);
            return "Time left: " + seconds + " sec";
        }

        long totalSeconds = Math.max(1, remainingMillis / 1000L);
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        return String.format(Locale.getDefault(), "Time left: %02d:%02d", minutes, seconds);
    }

    static class TrainingCrewViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTrainingCrewProfile;
        TextView tvTrainingCrewName;
        TextView tvTrainingCrewRole;
        TextView tvTrainingCrewStats;
        TextView tvTrainingCountdown;
        Button btnTrainingAction;

        public TrainingCrewViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTrainingCrewProfile = itemView.findViewById(R.id.ivTrainingCrewProfile);
            tvTrainingCrewName = itemView.findViewById(R.id.tvTrainingCrewName);
            tvTrainingCrewRole = itemView.findViewById(R.id.tvTrainingCrewRole);
            tvTrainingCrewStats = itemView.findViewById(R.id.tvTrainingCrewStats);
            tvTrainingCountdown = itemView.findViewById(R.id.tvTrainingCountdown);
            btnTrainingAction = itemView.findViewById(R.id.btnTrainingAction);
        }
    }
}