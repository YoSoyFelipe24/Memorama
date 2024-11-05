package com.example.memorama;

public class User {
    private String username;
    private byte[] profileImage; // Puedes cambiar esto a un tipo diferente si prefieres
    private int highScore, money;


    public User(String username, byte[] profileImage, int highScore, int money) {
        this.username = username;
        this.profileImage = profileImage;
        this.highScore = highScore;
        this.money = money;
    }

    public String getUser() {
        return username;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public int getHighScore() {
        return highScore;
    }
    public int getMoney() {
        return money;
    }
}
