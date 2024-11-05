/*
package com.example.memorama;
import android.content.DialogInterface;
import android.os.Bundle;
package com.example.memorama;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;

public class difcultad() {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dificultad);


    final int[] numCartas = new int[1];
    String[] modos = {"facil", "intermedio", "dificil"};
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Selecciona el modo de juego")
            .setItems(modos, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            numCartas[0] = 12; // 6 pares
                            break;
                        case 1:
                            numCartas[0] = 20; // 10 pares
                            break;
                        case 2:
                            numCartas[0] = 28; // 14 pares
                            break;
                    }
                }
            });
    AlertDialog dialog = builder.create();
    dialog.setCancelable(false);
    dialog.show();
}

    private void setContentView(int activityDificultad) {
    }*/