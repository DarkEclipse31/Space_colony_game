package com.example.space_colony_game.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.space_colony_game.R;
import com.example.space_colony_game.model.Mission;

import java.util.ArrayList;
import java.util.List;

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.MissionViewHolder> {

    public interface OnMissionClickListener {
        void onMissionClicked(Mission mission);
    }

    private final List<Mission> missionList = new ArrayList<>();
    private final OnMissionClickListener listener;

    public MissionAdapter(@NonNull OnMissionClickListener listener) {
        this.listener = listener;
    }

    public void submitList(@NonNull List<Mission> newList) {
        missionList.clear();
        missionList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mission_card, parent, false);
        return new MissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MissionViewHolder holder, int position) {
        Mission mission = missionList.get(position);

        holder.ivMissionImage.setImageResource(mission.getImageResId());
        holder.tvMissionTitle.setText(mission.getTitle());
        holder.tvMissionMeta.setText(
                "Difficulty: " + mission.getDifficultyText()
                        + " | Category: " + mission.getCategoryText()
        );
        holder.tvMissionStats.setText(
                "HP: " + mission.getThreatHp()
                        + " | Attack: " + mission.getThreatAttack()
                        + " | Retaliate: " + mission.getThreatRetaliate()
                        + " | Crew " + mission.getMaxCrew()
        );
        holder.btnMissionAction.setText(mission.getActionLabel());

        holder.itemView.setOnClickListener(v -> listener.onMissionClicked(mission));
        holder.btnMissionAction.setOnClickListener(v -> listener.onMissionClicked(mission));
    }

    @Override
    public int getItemCount() {
        return missionList.size();
    }

    static class MissionViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivMissionImage;
        final TextView tvMissionTitle;
        final TextView tvMissionMeta;
        final TextView tvMissionStats;
        final TextView btnMissionAction;

        MissionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMissionImage = itemView.findViewById(R.id.ivMissionImage);
            tvMissionTitle = itemView.findViewById(R.id.tvMissionTitle);
            tvMissionMeta = itemView.findViewById(R.id.tvMissionMeta);
            tvMissionStats = itemView.findViewById(R.id.tvMissionStats);
            btnMissionAction = itemView.findViewById(R.id.btnMissionAction);
        }
    }
}
