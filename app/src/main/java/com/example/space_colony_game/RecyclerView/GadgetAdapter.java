package com.example.space_colony_game.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.space_colony_game.R;
import com.example.space_colony_game.model.Gadget;
import com.example.space_colony_game.model.GadgetType;

import java.util.List;

public class GadgetAdapter extends RecyclerView.Adapter<GadgetAdapter.GadgetViewHolder> {

    public interface OnGadgetUseListener {
        void onUseGadget(Gadget gadget, int position);
    }

    private List<Gadget> gadgetList;
    private OnGadgetUseListener listener;

    public GadgetAdapter(List<Gadget> gadgetList) {
        this.gadgetList = gadgetList;
    }

    public GadgetAdapter(List<Gadget> gadgetList, OnGadgetUseListener listener) {
        this.gadgetList = gadgetList;
        this.listener = listener;
    }

    public static class GadgetViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGadgetImage;
        TextView tvGadgetName;
        TextView tvGadgetType;
        TextView tvGadgetValue;
        TextView tvUsesLeft;
        Button btnUse;

        public GadgetViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGadgetImage = itemView.findViewById(R.id.ivGadgetImage);
            tvGadgetName = itemView.findViewById(R.id.tvGadgetName);
            tvGadgetType = itemView.findViewById(R.id.tvGadgetType);
            tvGadgetValue = itemView.findViewById(R.id.tvGadgetValue);
            tvUsesLeft = itemView.findViewById(R.id.tvUsesLeft);
            btnUse = itemView.findViewById(R.id.btnUse);
        }
    }

    @NonNull
    @Override
    public GadgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gadget, parent, false);
        return new GadgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GadgetViewHolder holder, int position) {
        Gadget gadget = gadgetList.get(position);
        Context context = holder.itemView.getContext();

        holder.ivGadgetImage.setImageResource(gadget.getIconId());
        holder.tvGadgetName.setText(gadget.getName());
        holder.tvGadgetType.setText("Type: " + gadget.getType().getDisplayName());

        // Use more descriptive labels instead of "Effect"
        String label;
        switch (gadget.getType()) {
            case MEDICINE:
                label = "Healing: +";
                break;
            case RIFLE:
                label = "Attack Boost: +";
                break;
            case ARMOUR:
                label = "Armor Rating: ";
                break;
            case POTION:
                label = "Revive Power: ";
                break;
            default:
                label = "Power: ";
                break;
        }
        holder.tvGadgetValue.setText(label + gadget.getEffectValue());

        holder.tvUsesLeft.setText("Uses: " + gadget.getUsesLeft());

        if (gadget.getType() == GadgetType.RIFLE) {
            holder.tvGadgetType.setTextColor(Color.parseColor("#FF7043"));
        } else if (gadget.getType() == GadgetType.MEDICINE) {
            holder.tvGadgetType.setTextColor(Color.parseColor("#66BB6A"));
        } else if (gadget.getType() == GadgetType.ARMOUR) {
            holder.tvGadgetType.setTextColor(Color.parseColor("#42A5F5"));
        } else if (gadget.getType() == GadgetType.POTION) {
            holder.tvGadgetType.setTextColor(Color.parseColor("#FFA726"));
        } else {
            holder.tvGadgetType.setTextColor(Color.WHITE);
        }

        if (gadget.getUsesLeft() <= 0) {
            holder.btnUse.setEnabled(false);
            holder.btnUse.setText("USED");
            holder.btnUse.setAlpha(0.4f);
        } else {
            holder.btnUse.setEnabled(true);
            holder.btnUse.setText("USE");
            holder.btnUse.setAlpha(1.0f);
        }

        if (listener != null) {
            holder.btnUse.setVisibility(View.VISIBLE);
            holder.btnUse.setOnClickListener(v -> {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onUseGadget(gadget, pos);
                }
            });
        } else {
            holder.btnUse.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return gadgetList == null ? 0 : gadgetList.size();
    }

    public void updateList(List<Gadget> newList) {
        this.gadgetList = newList;
        notifyDataSetChanged();
    }

    public void addGadget(Gadget gadget) {
        gadgetList.add(gadget);
        notifyItemInserted(gadgetList.size() - 1);
    }

    public void removeGadget(int position) {
        gadgetList.remove(position);
        notifyItemRemoved(position);
    }

    public void refreshGadget(int position) {
        notifyItemChanged(position);
    }
}