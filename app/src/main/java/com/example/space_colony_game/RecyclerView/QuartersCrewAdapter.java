package com.example.spaceapplication.RecyclerView;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spaceapplication.R;
import com.example.spaceapplication.model.CrewMember;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuartersCrewAdapter extends RecyclerView.Adapter<QuartersCrewAdapter.CrewViewHolder> {

    public interface OnRemoveClickListener {
        void onRemoveClicked(CrewMember crewMember);
    }

    private final Context context;
    private final OnRemoveClickListener removeClickListener;
    private final List<CrewMember> crewList = new ArrayList<>();

    public QuartersCrewAdapter(@NonNull Context context,
                               @NonNull List<CrewMember> initialList,
                               @NonNull OnRemoveClickListener removeClickListener) {
        this.context = context;
        this.removeClickListener = removeClickListener;
        this.crewList.addAll(initialList);
    }

    public void submitList(@NonNull List<CrewMember> newList) {
        crewList.clear();
        crewList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crew_card_quarters, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        CrewMember crew = crewList.get(position);

        String name = readString(crew, "UNKNOWN", "getName");
        String role = readString(crew, "UNKNOWN", "getRole");
        String status = normalizeStatus(readString(
                crew,
                "QUARTERS",
                "getStatus",
                "getLocation",
                "getCurrentStatus",
                "getCurrentLocation"
        ));

        int level = readInt(crew, 1, "getLevel");
        int currentHp = readInt(crew, 0, "getCurrentHp", "getHp");
        int maxHp = readInt(crew, Math.max(currentHp, 1), "getMaxHp", "getHpMax");
        int exp = readInt(crew, 0, "getExp", "getExperience");

        holder.tvCrewName.setText(name);
        holder.tvCrewRole.setText("ROLE: " + role.toUpperCase(Locale.US));
        holder.tvCrewStats.setText("LVL " + level + "   HP " + currentHp + "/" + maxHp + "   EXP " + exp);
        holder.tvCrewStatus.setText(status);

        int portraitResId = readInt(
                crew,
                0,
                "getPortraitResId",
                "getAvatarResId",
                "getImageResId",
                "getProfileImageResId"
        );

        if (portraitResId != 0) {
            holder.ivCrewPortrait.setImageResource(portraitResId);
        } else {
            holder.ivCrewPortrait.setImageResource(R.drawable.ic_launcher_foreground);
        }

        applyStatusChipStyle(holder.tvCrewStatus, status);

        holder.btnRemoveCrew.setOnClickListener(v -> removeClickListener.onRemoveClicked(crew));
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
        final TextView tvCrewStatus;
        final TextView btnRemoveCrew;

        CrewViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCrewPortrait = itemView.findViewById(R.id.ivCrewPortrait);
            tvCrewName = itemView.findViewById(R.id.tvCrewName);
            tvCrewRole = itemView.findViewById(R.id.tvCrewRole);
            tvCrewStats = itemView.findViewById(R.id.tvCrewStats);
            tvCrewStatus = itemView.findViewById(R.id.tvCrewStatus);
            btnRemoveCrew = itemView.findViewById(R.id.btnRemoveCrew);
        }
    }

    private void applyStatusChipStyle(@NonNull TextView chipView, @NonNull String status) {
        @ColorInt int fillColor;
        @ColorInt int strokeColor;

        switch (status) {
            case "TRAINING":
                fillColor = ContextCompat.getColor(context, R.color.dashboard_amber_soft);
                strokeColor = ContextCompat.getColor(context, R.color.dashboard_amber);
                break;

            case "HOSPITAL":
                fillColor = ContextCompat.getColor(context, R.color.dashboard_red_soft);
                strokeColor = ContextCompat.getColor(context, R.color.dashboard_red);
                break;

            case "WORKSHOP":
                fillColor = ContextCompat.getColor(context, R.color.dashboard_surface_3);
                strokeColor = ContextCompat.getColor(context, R.color.dashboard_cyan);
                break;

            case "MISSION":
                fillColor = ContextCompat.getColor(context, R.color.dashboard_purple_soft);
                strokeColor = ContextCompat.getColor(context, R.color.dashboard_purple);
                break;

            case "QUARTERS":
            default:
                fillColor = ContextCompat.getColor(context, R.color.dashboard_cyan_soft);
                strokeColor = ContextCompat.getColor(context, R.color.dashboard_cyan);
                break;
        }

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(dpToPx(18));
        drawable.setColor(fillColor);
        drawable.setStroke(dpToPx(1), strokeColor);

        chipView.setBackground(drawable);
        chipView.setTextColor(ContextCompat.getColor(context, R.color.dashboard_text_primary));
    }

    private String normalizeStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.trim().isEmpty()) {
            return "QUARTERS";
        }
        return rawStatus.trim().toUpperCase(Locale.US);
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    private String readString(CrewMember crew, String fallback, String... methodNames) {
        for (String methodName : methodNames) {
            try {
                Method method = crew.getClass().getMethod(methodName);
                Object value = method.invoke(crew);
                if (value != null) {
                    return value.toString();
                }
            } catch (Exception ignored) {
            }
        }
        return fallback;
    }

    private int readInt(CrewMember crew, int fallback, String... methodNames) {
        for (String methodName : methodNames) {
            try {
                Method method = crew.getClass().getMethod(methodName);
                Object value = method.invoke(crew);

                if (value instanceof Number) {
                    return ((Number) value).intValue();
                }

                if (value != null) {
                    return Integer.parseInt(value.toString());
                }
            } catch (Exception ignored) {
            }
        }
        return fallback;
    }
}