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

import java.io.File;

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
                COLUMN_PROFILE_IMAGE + " TEXT, " +
                COLUMN_MONEY + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean addUser(String username, String password, String imagePath) {
        if (checkUser(username, password)) {
            return false; // El usuario ya existe
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PROFILE_IMAGE, imagePath);  // Guardamos la ruta de la imagen

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1; // Devuelve true si la inserción fue exitosa
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_USERS, new String[]{"*"},
                    COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                    new String[]{username, password}, null, null, null);
            boolean exists = cursor.getCount() > 0;
            return exists;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    public Bitmap getProfileImage(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_PROFILE_IMAGE},
                COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int profileImageColumnIndex = cursor.getColumnIndex(COLUMN_PROFILE_IMAGE);
            if (profileImageColumnIndex != -1) {
                byte[] imageBytes = cursor.getBlob(profileImageColumnIndex); // Obtén el BLOB
                cursor.close();
                return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length); // Decodifica el BLOB a Bitmap
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return null; // Si no se encuentra la imagen, retornamos null
    }

    public User getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME, COLUMN_PROFILE_IMAGE, COLUMN_HIGH_SCORE, COLUMN_MONEY},
                COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String user = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
            byte[] profileImage = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE));
            int highScore = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HIGH_SCORE));
            int money = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MONEY));

            cursor.close();
            db.close();
            return new User(user, profileImage, highScore, money);
        }

        if (cursor != null) cursor.close();
        db.close();
        return null;
    }
}
