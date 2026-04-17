package com.example.space_colony_game;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.space_colony_game.UI.StartFragment;
import com.example.space_colony_game.data.SaveLoadManager;
import com.example.space_colony_game.data.Storage;
import com.example.space_colony_game.model.Pilot;
import com.example.space_colony_game.model.Location;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onPause() {
        super.onPause();
        SaveLoadManager.saveGame(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SaveLoadManager.saveGame(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize game state
        if (!SaveLoadManager.loadGame(this)) {
            seedStarterCrew();
            SaveLoadManager.saveGame(this);
        }

        if (savedInstanceState == null) {
            openFragment(new StartFragment(), false);
        }
    }

    public void openFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    //Set the Starting player - Pilot, Coins

    private void seedStarterCrew() {
        Storage.getInstance().resetGameState();
        Storage.getInstance().getGameState().setCoins(300);

        int id = Storage.getInstance().nextId();
        Pilot nova = new Pilot(id, "Nova");
        nova.setLocation(Location.QUARTERS);

        Storage.getInstance().getGameState().getCrewMembers().add(nova);
    }
}
