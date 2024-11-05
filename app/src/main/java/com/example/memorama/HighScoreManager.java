package com.example.memorama;

import android.content.Context;
import android.content.SharedPreferences;

public class HighScoreManager {
    private static final String PREFS_NAME = "game_prefs";
    private static final String HIGH_SCORE_KEY = "high_score";
    //0btener
    public static int getHighScore(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(HIGH_SCORE_KEY, 0);
    }
    //Actualizar
    public static void checkAndUpdateHS(Context context, int score){
        int highScore = getHighScore(context);
        if (score > highScore){
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(HIGH_SCORE_KEY, score);
            editor.apply();
        }
    }
}
