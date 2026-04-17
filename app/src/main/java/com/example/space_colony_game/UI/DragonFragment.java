package com.example.space_colony_game.UI;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.space_colony_game.MainActivity;
import com.example.space_colony_game.R;
import com.example.space_colony_game.data.GameState;

public class DragonFragment extends Fragment {

    private TextView tvDragonCoins;

    public DragonFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dragon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.tvDragonCoins = view.findViewById(R.id.tvDragonCoins);

        Button btnBack = view.findViewById(R.id.btnBackFromDragon);
        Button btnFightDragon = view.findViewById(R.id.btnFightDragon);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).openFragment(new QuartersFragment(), false);
            }
        });

        btnFightDragon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).openFragment(new DragonPartySetupFragment(), true);
            }
        });

        this.updateCoins();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCoins();
    }

    private void updateCoins() {
        this.tvDragonCoins.setText(GameState.getInstance().getCoins() + " COINS");
    }
}