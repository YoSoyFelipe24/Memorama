package com.example.memorama;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

public class musicManager {
    private static MediaPlayer mediaPlayer;
    private static boolean isMusicEnabled;

    public static void init(Context context, int musicResId){
        if (mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(context, musicResId);
            mediaPlayer.setLooping(true);
            SharedPreferences preferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
            isMusicEnabled = preferences.getBoolean("music_enabled", true);
            if (isMusicEnabled){
                mediaPlayer.start();
            }
        }
    }
    public static void toggleMusic(Context context, boolean enable){
        SharedPreferences preferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("music_enabled", enable);
        editor.apply();

        isMusicEnabled = enable;
        if (isMusicEnabled){
            mediaPlayer.start();
        }else {
            mediaPlayer.pause();
        }
    }
    public static void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public static void resumeMusic() {
        if (mediaPlayer != null && isMusicEnabled) {
            mediaPlayer.start();
        }
    }

    public static void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
