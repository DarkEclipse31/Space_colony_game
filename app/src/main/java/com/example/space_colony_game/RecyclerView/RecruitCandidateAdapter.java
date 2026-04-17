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
import com.example.spaceapplication.model.RecruitCandidate;

import java.util.ArrayList;
import java.util.List;

public class RecruitCandidateAdapter extends RecyclerView.Adapter<RecruitCandidateAdapter.RecruitViewHolder> {

    public interface OnRecruitClickListener {
        void onRecruitClick(RecruitCandidate candidate, int position);
    }

    private final List<RecruitCandidate> candidateList;
    private final OnRecruitClickListener listener;

    public RecruitCandidateAdapter(List<RecruitCandidate> candidateList, OnRecruitClickListener listener) {
        this.candidateList = new ArrayList<>(candidateList);
        this.listener = listener;
    }

    public void setCandidateList(List<RecruitCandidate> newList) {
        candidateList.clear();
        candidateList.addAll(newList);
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        if (position >= 0 && position < candidateList.size()) {
            candidateList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public RecruitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recruit_candidate, parent, false);
        return new RecruitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecruitViewHolder holder, int position) {
        RecruitCandidate candidate = candidateList.get(position);

        holder.ivRecruitProfile.setImageResource(candidate.getProfileImageResId());
        holder.tvRecruitName.setText(candidate.getName().toUpperCase());
        holder.tvRecruitRole.setText("ROLE: " + candidate.getRole().getDisplayName().toUpperCase());
        
        if (candidate.getRole() == com.example.spaceapplication.model.CrewRole.SCIENTIST) {
            holder.tvRecruitStats.setText("LVL " + candidate.getLevel() + " | HP " + candidate.getHp());
        } else {
            holder.tvRecruitStats.setText(
                    "LVL " + candidate.getLevel()
                            + " | HP " + candidate.getHp()
                            + " | ATTACK " + candidate.getAttack()
                            + " | RESILIENCE " + candidate.getResilience()
            );
        }

        holder.tvRecruitCost.setText(candidate.getCost() + " COINS");

        holder.btnAddCrew.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecruitClick(candidate, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return candidateList.size();
    }

    static class RecruitViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecruitProfile;
        TextView tvRecruitName;
        TextView tvRecruitRole;
        TextView tvRecruitStats;
        TextView tvRecruitCost;
        Button btnAddCrew;

        public RecruitViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecruitProfile = itemView.findViewById(R.id.ivRecruitProfile);
            tvRecruitName = itemView.findViewById(R.id.tvRecruitName);
            tvRecruitRole = itemView.findViewById(R.id.tvRecruitRole);
            tvRecruitStats = itemView.findViewById(R.id.tvRecruitStats);
            tvRecruitCost = itemView.findViewById(R.id.tvRecruitCost);
            btnAddCrew = itemView.findViewById(R.id.btnAddCrew);
        }
    }
}