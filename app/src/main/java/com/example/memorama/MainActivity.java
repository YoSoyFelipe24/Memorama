package com.example.memorama;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    AppCompatButton playButton,settingButton, exitButton;
    AppCompatImageButton moneyButton;
    AppCompatTextView HighScoreViewBorder, HighScoreTextView, Monedas;
    SesionManager prefsManager;
    int money, highScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        prefsManager = new SesionManager(this);
        if (prefsManager.isLoggedIn()) {
            // Mostrar detalles del usuario
            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
            highScore = prefsManager.getHighScore();
            money = prefsManager.getMoney();
        } else {
            // No hay sesión iniciada, redirigir a pantalla de inicio de sesión o manejarlo de otra forma
            Toast.makeText(this, "No has iniciado sesión", Toast.LENGTH_SHORT).show();
        }
        playButton = findViewById(R.id.PLAY);
        settingButton = findViewById(R.id.Config);
        exitButton = findViewById(R.id.Exit);
        HighScoreViewBorder = findViewById(R.id.scoreborder);
        HighScoreTextView = findViewById(R.id.score);
        moneyButton = findViewById(R.id.buttonmoneda);
        Monedas = findViewById(R.id.moneytext);
        musicManager.init(this, R.raw.music_memorama);
        //Actualizar en UI
        HighScoreViewBorder.setText(String.valueOf(highScore));
        HighScoreTextView.setText(String.valueOf(highScore));
        Monedas.setText(String.valueOf(money));


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, JuegoActivity.class);
                startActivity(intent);
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, configuracion.class);
                startActivity(intent);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });

        moneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Monedas.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        musicManager.release();
    }
    @Override
    protected void onPause(){
        super.onPause();
        musicManager.pauseMusic();
    }
    @Override
    protected void onResume(){
        super.onResume();
        musicManager.resumeMusic();
    }
}