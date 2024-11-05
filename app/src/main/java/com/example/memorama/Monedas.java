package com.example.memorama;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Monedas extends AppCompatActivity {

    AppCompatImageButton Volver;
    SesionManager prefs;
    AppCompatTextView Money, borde;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_monedas);
        musicManager.init(this, R.raw.music_memorama);
        Volver = findViewById(R.id.back);
        prefs= new SesionManager(this);
        int money = prefs.getMoney();
        Money = findViewById(R.id.money);
        borde = findViewById(R.id.Border_TusMoney);
        Money.setText(String.valueOf(money));
        borde.setText(String.valueOf(money));

        verificarSesion();

        Volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Monedas.this, MainActivity.class);
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
    public void verificarSesion() {
        String username = prefs.getUsername();

        if (username == null) {
            // No hay sesión activa, redirigir al login o mostrar una alerta
            Toast.makeText(this, "Por favor, inicia sesión", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, configuracion.class);
            startActivity(intent);
        } else {
            // Hay una sesión activa, permitir que el usuario juegue o continúe
            Toast.makeText(this, "Bienvenido, " + username, Toast.LENGTH_SHORT).show();
        }
    }
}