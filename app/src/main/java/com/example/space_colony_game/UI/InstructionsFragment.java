package com.example.space_colony_game.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.space_colony_game.MainActivity;
import com.example.space_colony_game.R;

public class InstructionsFragment extends Fragment {

    public InstructionsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_instructions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnGotIt = view.findViewById(R.id.btnGotIt);
        btnGotIt.setOnClickListener(v -> 
            ((MainActivity) requireActivity()).openFragment(new QuartersFragment(), false)
        );
    }
}