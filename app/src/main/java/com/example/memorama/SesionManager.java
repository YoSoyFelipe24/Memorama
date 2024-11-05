package com.example.memorama;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

public class SesionManager {
    private final Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;  // Declarar sin inicializar
    private static final String IS_LOGGED_IN = "isLoggedIn";

    public SesionManager(Context context) {
        this.context = context;

        // Asegúrate de que el contexto no sea nulo
        if (context != null) {
            sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();  // Inicializar el editor aquí
        } else {
            throw new NullPointerException("Context is null");
        }
    }

    public void saveUserDetails(String username, byte[] profileImage, int highScore, int money) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString("username", username);
        editor.putString("profile_image", Base64.encodeToString(profileImage, Base64.DEFAULT));
        editor.putInt("high_score", highScore);
        editor.putInt("money", money);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false); // Devuelve false si no ha iniciado sesión
    }

    public String getUsername() {
        return sharedPreferences.getString("username", null);
    }

    public byte[] getProfileImage() {
        String imageString = sharedPreferences.getString("profile_image", null);
        return imageString != null ? Base64.decode(imageString, Base64.DEFAULT) : null;
    }

    public int getHighScore() {
        return sharedPreferences.getInt("high_score", 0);
    }

    public void saveHighScore(int highScore) {
        editor.putInt("high_score", highScore);
        editor.apply();
    }

    public  int getMoney(){
        return sharedPreferences.getInt("money", 0);
    }
    public void saveMoney(int money) {
        editor.putInt("money", money);
        editor.apply();
    }

    public void clearUserDetails() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}