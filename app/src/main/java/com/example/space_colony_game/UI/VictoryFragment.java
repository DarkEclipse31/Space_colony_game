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

public class VictoryFragment extends Fragment {

    private static final String ARG_DURATION = "arg_duration";
    private long durationMs;

    public VictoryFragment() {
    }

    public static VictoryFragment newInstance(long durationMs) {
        VictoryFragment fragment = new VictoryFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DURATION, durationMs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            durationMs = getArguments().getLong(ARG_DURATION);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_victory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvVictoryTime = view.findViewById(R.id.tvVictoryTime);
        Button btnBackToStart = view.findViewById(R.id.btnVictoryBackToStart);

        tvVictoryTime.setText("Time: " + SaveLoadManager.formatDuration(durationMs));

        btnBackToStart.setOnClickListener(v -> {
            // Return to StartFragment and clear backstack
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openFragment(new StartFragment(), false);
            }
        });
    }
}
