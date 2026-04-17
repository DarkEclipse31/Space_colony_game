package com.example.space_colony_game.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.space_colony_game.model.CrewMember;
import com.example.space_colony_game.model.CrewRole;
import com.example.space_colony_game.model.Engineer;
import com.example.space_colony_game.model.Medic;
import com.example.space_colony_game.model.Pilot;
import com.example.space_colony_game.model.Scientist;
import com.example.space_colony_game.model.Soldier;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;


// save and load game
// uses gson (converts java objects to json text and back - stored into SharedPreferences)

public class SaveLoadManager {

    private static final String TAG = "SaveLoadManager";

    // File name for saved data
    private static final String PREFS_NAME = "celestix_prefs";
    private static final String KEY_GAME_STATE = "game_state";
    private static final String KEY_ID_COUNTER = "id_counter";
    private static final String KEY_BEST_DRAGON_WIN_MS = "best_dragon_win_ms";

    // Gson conversion
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(CrewMember.class, new CrewMemberAdapter())
            .create();

    private SaveLoadManager() {
    }

    public static void saveGame(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String json = gson.toJson(Storage.getInstance().getGameState());
        editor.putString(KEY_GAME_STATE, json);
        editor.putInt(KEY_ID_COUNTER, Storage.getInstance().getCurrentIdCounter());


        boolean success = editor.commit(); // immediate save
        if (!success) {
            Log.e(TAG, "Failed to save game state");
        }
    }

    public static boolean loadGame(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_GAME_STATE, null);

        if (json == null) {
            return false;
        }

        try {
            GameState loaded = gson.fromJson(json, GameState.class);
            int savedIdCounter = prefs.getInt(KEY_ID_COUNTER, 10);

            Storage.getInstance().setGameState(loaded);
            Storage.getInstance().setIdCounter(savedIdCounter);
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Failed to load game state", e);
            return false;
        }
    }

    public static boolean hasSave(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.contains(KEY_GAME_STATE);
    }

    public static void deleteSave(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_GAME_STATE)
                .remove(KEY_ID_COUNTER)
                .commit();
    }

    // Saves best completion time for dragon
    public static void saveBestDragonWin(Context context, long durationMs) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long currentBest = prefs.getLong(KEY_BEST_DRAGON_WIN_MS, Long.MAX_VALUE);
        if (durationMs < currentBest) {
            prefs.edit().putLong(KEY_BEST_DRAGON_WIN_MS, durationMs).apply();
        }
    }

    public static String getBestDragonWinText(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long bestMs = prefs.getLong(KEY_BEST_DRAGON_WIN_MS, 0L);
        if (bestMs <= 0L) {
            return "No record yet";
        }
        return (bestMs / 1000L) + " seconds";
    }

    public static String formatDuration(long millis) {
        long totalSeconds = Math.max(0L, millis / 1000L);
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        long seconds = totalSeconds % 60L;

        if (hours > 0L) {
            return java.util.Locale.getDefault() != null
                    ? String.format(java.util.Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
                    : String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        return java.util.Locale.getDefault() != null
                ? String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }

    private static class CrewMemberAdapter implements JsonDeserializer<CrewMember> {
        @Override
        public CrewMember deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement roleElement = jsonObject.get("role");

            if (roleElement == null) {
                throw new JsonParseException("Missing 'role' field in CrewMember JSON");
            }

            CrewRole role = context.deserialize(roleElement, CrewRole.class);
            Class<? extends CrewMember> crewType;

            switch (role) {
                case PILOT:
                    crewType = Pilot.class;
                    break;
                case ENGINEER:
                    crewType = Engineer.class;
                    break;
                case MEDIC:
                    crewType = Medic.class;
                    break;
                case SCIENTIST:
                    crewType = Scientist.class;
                    break;
                case SOLDIER:
                    crewType = Soldier.class;
                    break;
                default:
                    throw new JsonParseException("Unknown role: " + role);
            }

            // Convert JSON into correct object type
            return context.deserialize(json, crewType);
        }
    }
}
