package com.example.spaceapplication.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spaceapplication.MainActivity;
import com.example.spaceapplication.R;
import com.example.spaceapplication.data.SaveLoadManager;

public class StartFragment extends Fragment {

    private TextView tvHighScoreValue;

    public StartFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnStartGame = view.findViewById(R.id.btnStartGame);
        tvHighScoreValue = view.findViewById(R.id.tvHighScoreValue);

        tvHighScoreValue.setText("High Score: " + SaveLoadManager.getBestDragonWinText(requireContext()));

        btnStartGame.setOnClickListener(v ->
                ((MainActivity) requireActivity()).openFragment(new InstructionsFragment(), true)
        );
    }

    @Override
    public void onResume() {
        super.onResume();

        if (tvHighScoreValue != null) {
            tvHighScoreValue.setText("High Score: " + SaveLoadManager.getBestDragonWinText(requireContext()));
        }
    }
}