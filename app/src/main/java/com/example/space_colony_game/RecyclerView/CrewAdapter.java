package com.example.space_colony_game.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.space_colony_game.R;
import com.example.space_colony_game.model.CrewMember;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.CrewViewHolder> {

    public interface OnSelectionChangedListener {
        void onSelectionChanged(List<CrewMember> selectedCrew);
    }

    private final List<CrewMember> crewList = new ArrayList<>();
    private final Set<String> selectedCrewKeys = new HashSet<>();
    private final OnSelectionChangedListener listener;
    private int maxSelection = 1;

    public CrewAdapter(@NonNull OnSelectionChangedListener listener) {
        this.listener = listener;
    }

    public void submitList(@NonNull List<CrewMember> newList) {
        crewList.clear();
        crewList.addAll(newList);
        selectedCrewKeys.clear();
        notifyDataSetChanged();
        notifySelectionChanged();
    }

    public void setMaxSelection(int maxSelection) {
        this.maxSelection = maxSelection;
        notifyDataSetChanged();
    }

    public List<CrewMember> getSelectedCrew() {
        List<CrewMember> selected = new ArrayList<>();
        for (CrewMember crewMember : crewList) {
            if (selectedCrewKeys.contains(getCrewKey(crewMember))) {
                selected.add(crewMember);
            }
        }
        return selected;
    }

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crew_card_selectable, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        CrewMember crew = crewList.get(position);

        String name = crew.getName();
        String role = crew.getRole().getDisplayName();
        int level = crew.getLevel();
        int currentHp = crew.getHp();
        int maxHp = crew.getMaxHp();

        holder.tvCrewName.setText(name);
        holder.tvCrewRole.setText("ROLE: " + role.toUpperCase(Locale.US));
        holder.tvCrewStats.setText("LVL " + level + "   HP " + currentHp + "/" + maxHp);

        int portraitResId = crew.getProfileImageResId();

        if (portraitResId != 0) {
            holder.ivCrewPortrait.setImageResource(portraitResId);
        } else {
            holder.ivCrewPortrait.setImageResource(R.drawable.ic_launcher_foreground);
        }

        String crewKey = getCrewKey(crew);
        boolean isSelected = selectedCrewKeys.contains(crewKey);

        holder.cbSelectCrew.setOnCheckedChangeListener(null);
        holder.cbSelectCrew.setChecked(isSelected);

        boolean canSelectMore = isSelected || selectedCrewKeys.size() < maxSelection;
        holder.cbSelectCrew.setEnabled(canSelectMore);

        holder.cbSelectCrew.setOnCheckedChangeListener((CompoundButton buttonView, boolean checked) -> {
            if (checked) {
                if (selectedCrewKeys.size() >= maxSelection && !selectedCrewKeys.contains(crewKey)) {
                    buttonView.setChecked(false);
                    return;
                }
                selectedCrewKeys.add(crewKey);
            } else {
                selectedCrewKeys.remove(crewKey);
            }

            notifyDataSetChanged();
            notifySelectionChanged();
        });

        holder.itemView.setOnClickListener(v -> holder.cbSelectCrew.performClick());
    }

    @Override
    public int getItemCount() {
        return crewList.size();
    }

    static class CrewViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivCrewPortrait;
        final TextView tvCrewName;
        final TextView tvCrewRole;
        final TextView tvCrewStats;
        final CheckBox cbSelectCrew;

        public CrewViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelectCrew = itemView.findViewById(R.id.cbSelectCrew);
            ivCrewPortrait = itemView.findViewById(R.id.ivCrewPortraitSelect);
            tvCrewName = itemView.findViewById(R.id.tvCrewNameSelect);
            tvCrewRole = itemView.findViewById(R.id.tvCrewRoleSelect);
            tvCrewStats = itemView.findViewById(R.id.tvCrewStatsSelect);
        }
    }

    private String getCrewKey(CrewMember crew) {
        return String.valueOf(crew.getId());
    }

    private void notifySelectionChanged() {
        listener.onSelectionChanged(getSelectedCrew());
    }
}
