package com.example.memorama;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "users";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_HIGH_SCORE = "high_score";
    private static final String COLUMN_PROFILE_IMAGE = "profile_image";
    private static final String COLUMN_MONEY = "money";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_HIGH_SCORE + " INTEGER DEFAULT 0, " +
                COLUMN_PROFILE_IMAGE + " BLOB, " +
                COLUMN_MONEY + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean addUser(String username, String password, byte[] profileImage) {
        if (checkUser(username, password)) {
            return false; // El usuario ya existe
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password); // Considera hashear la contraseña
        values.put(COLUMN_PROFILE_IMAGE, profileImage);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1; // Devuelve true si la inserción fue exitosa
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{"*"},
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);
        boolean exists = cursor.getCount() > 0; // Retorna true si el usuario existe
        cursor.close();
        return exists;
    }

    public Bitmap getProfileImage(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_PROFILE_IMAGE},
                COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            byte[] imageBytes = cursor.getBlob(0); // Obtén el BLOB
            cursor.close();
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }

        if (cursor != null) {
            cursor.close();
        }
        return null; // O maneja el caso en que no se encontró la imagen
    }

    public User getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME, COLUMN_PROFILE_IMAGE, COLUMN_HIGH_SCORE, COLUMN_MONEY},
                COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String user = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
            @SuppressLint("Range") byte[] profileImage = cursor.getBlob(cursor.getColumnIndex(COLUMN_PROFILE_IMAGE));
            @SuppressLint("Range") int highScore = cursor.getInt(cursor.getColumnIndex(COLUMN_HIGH_SCORE));
            @SuppressLint("Range") int money = cursor.getInt(cursor.getColumnIndex(COLUMN_MONEY));

            cursor.close();
            return new User(user, profileImage, highScore, money);
        }

        if (cursor != null) {
            cursor.close();
        }
        return null; // Retorna null si no se encuentra el usuario
    }
}
