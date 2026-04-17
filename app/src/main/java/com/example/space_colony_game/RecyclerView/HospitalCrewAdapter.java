package com.example.space_colony_game.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.space_colony_game.R;
import com.example.space_colony_game.model.CrewMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HospitalCrewAdapter extends RecyclerView.Adapter<HospitalCrewAdapter.HospitalCrewViewHolder> {

    public interface OnHospitalActionClickListener {
        void onHospitalAction(CrewMember crewMember);
    }

    private final List<CrewMember> crewList;
    private final OnHospitalActionClickListener listener;
    private final int reviveCost;
    private final boolean testMode;

    public HospitalCrewAdapter(List<CrewMember> crewList,
                               int reviveCost,
                               boolean testMode,
                               OnHospitalActionClickListener listener) {
        this.crewList = new ArrayList<>(crewList);
        this.reviveCost = reviveCost;
        this.testMode = testMode;
        this.listener = listener;
    }

    public void setCrewList(List<CrewMember> newCrewList) {
        crewList.clear();
        crewList.addAll(newCrewList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HospitalCrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hospital_crew, parent, false);
        return new HospitalCrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalCrewViewHolder holder, int position) {
        CrewMember crewMember = crewList.get(position);
        long now = System.currentTimeMillis();

        holder.ivHospitalCrewProfile.setImageResource(crewMember.getProfileImageResId());
        holder.tvHospitalCrewName.setText(crewMember.getName().toUpperCase(Locale.getDefault()));
        holder.tvHospitalCrewRole.setText(
                "ROLE: " + crewMember.getRole().getDisplayName().toUpperCase(Locale.getDefault())
        );
        holder.tvHospitalCrewStats.setText(
                "LVL " + crewMember.getLevel()
                        + "   HP " + crewMember.getHp() + "/" + crewMember.getMaxHp()
                        + "   EXP " + crewMember.getExp()
        );

        if (crewMember.getHp() <= 0) {
            holder.tvHospitalStatus.setText("Defeated | Revive cost: " + reviveCost + " coins");
            holder.btnHospitalAction.setText("REVIVE");
            holder.btnHospitalAction.setEnabled(true);
            holder.btnHospitalAction.setAlpha(1.0f);
            holder.btnHospitalAction.setOnClickListener(v -> listener.onHospitalAction(crewMember));
            return;
        }

        if (crewMember.isHealingComplete(now) || crewMember.isHealingReady()) {
            holder.tvHospitalStatus.setText("Healing complete");
            holder.btnHospitalAction.setText("RETURN");
            holder.btnHospitalAction.setEnabled(true);
            holder.btnHospitalAction.setAlpha(1.0f);
            holder.btnHospitalAction.setOnClickListener(v -> listener.onHospitalAction(crewMember));
            return;
        }

        if (crewMember.isHealingInProgress(now)) {
            holder.tvHospitalStatus.setText(formatRemainingTime(crewMember.getRemainingHealingMillis(now)));
            holder.btnHospitalAction.setText("WAIT");
            holder.btnHospitalAction.setEnabled(false);
            holder.btnHospitalAction.setAlpha(0.6f);
            holder.btnHospitalAction.setOnClickListener(null);
            return;
        }

        holder.tvHospitalStatus.setText(getHealTimeLabel(crewMember));
        holder.btnHospitalAction.setText("HEAL");
        holder.btnHospitalAction.setEnabled(true);
        holder.btnHospitalAction.setAlpha(1.0f);
        holder.btnHospitalAction.setOnClickListener(v -> listener.onHospitalAction(crewMember));
    }

    @Override
    public int getItemCount() {
        return crewList.size();
    }

    private String getHealTimeLabel(CrewMember crewMember) {
        long millis = crewMember.getHealingDurationMillis(testMode);

        if (testMode) {
            long seconds = Math.max(1, millis / 1000L);
            return "Heal time: " + seconds + " sec";
        }

        long totalSeconds = Math.max(1, millis / 1000L);
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;

        return String.format(Locale.getDefault(), "Heal time: %02d:%02d", minutes, seconds);
    }

    private String formatRemainingTime(long remainingMillis) {
        if (testMode) {
            long seconds = Math.max(1, remainingMillis / 1000L);
            return "Time left: " + seconds + " sec";
        }

        long totalSeconds = Math.max(1, remainingMillis / 1000L);
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;

        return String.format(Locale.getDefault(), "Time left: %02d:%02d", minutes, seconds);
    }

    static class HospitalCrewViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHospitalCrewProfile;
        TextView tvHospitalCrewName;
        TextView tvHospitalCrewRole;
        TextView tvHospitalCrewStats;
        TextView tvHospitalStatus;
        Button btnHospitalAction;

        public HospitalCrewViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHospitalCrewProfile = itemView.findViewById(R.id.ivHospitalCrewProfile);
            tvHospitalCrewName = itemView.findViewById(R.id.tvHospitalCrewName);
            tvHospitalCrewRole = itemView.findViewById(R.id.tvHospitalCrewRole);
            tvHospitalCrewStats = itemView.findViewById(R.id.tvHospitalCrewStats);
            tvHospitalStatus = itemView.findViewById(R.id.tvHospitalStatus);
            btnHospitalAction = itemView.findViewById(R.id.btnHospitalAction);
        }
    }
}
