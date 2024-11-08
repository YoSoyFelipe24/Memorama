package com.example.memorama;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class Monedas extends AppCompatActivity {

    AppCompatImageButton Volver, cerrar;
    SesionManager prefs;
    AppCompatTextView Money, borde;
    RelativeLayout ventana_ruleta;
    AppCompatButton ruleta, diario, girar;
    int money;
    private final int[] bonuses = {50, 5, 20, 5, 1, 5, 10, 5};  // Los bonos en monedas
    private int currentBonus;  // Bono que se ganará en esta tirada
    private int rotationAngle = 0;  // Ángulo de rotación de la ruleta
    AppCompatImageView rouletteImage;
    CountDownTimer timer;
    private static final long TIEMPO_CONGELACION = 2 * 60 * 1000; // 2 minutos en milisegundos

    TextView bonusCoinsText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_monedas);
        musicManager.init(this, R.raw.music_memorama);
        Volver = findViewById(R.id.back);
        prefs = new SesionManager(this); //Trae los datos del usuario
        money = prefs.getMoney();
        Money = findViewById(R.id.money);
        borde = findViewById(R.id.moneyborder);
        Money.setText(String.valueOf(money));
        borde.setText(String.valueOf(money));
        //ruleta inicializacion
        ventana_ruleta = findViewById(R.id.ventana_ruleta);
        ruleta = findViewById(R.id.RuletaButton);
        diario = findViewById(R.id.Diario);
        cerrar = ventana_ruleta.findViewById(R.id.cambiar);
        girar = ventana_ruleta.findViewById(R.id.spinButton);
        bonusCoinsText = ventana_ruleta.findViewById(R.id.bonusCoinsText);
        rouletteImage = ventana_ruleta.findViewById(R.id.rouletteImage);

        verificarSesion();

        Volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Monedas.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //bonus diario
        diario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.addMoney(10);
                Toast.makeText(Monedas.this,"Obtuviste 10 monedas", Toast.LENGTH_SHORT).show();
            }

        });

        //mostrar ruleta
        ruleta.setOnClickListener(view -> showRuletaOverlay());
        //ocultar ruleta
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideRuletaOverlay();
                recreate();
            }
        });
        //Girar ruleta
        girar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinRoulette();
                iniciarTiempoDeCongelacion();
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

    //ruleta
    private void showRuletaOverlay() {
        ventana_ruleta.setVisibility(View.VISIBLE);
        ruleta.setVisibility(View.GONE);
        diario.setVisibility(View.GONE);
    }

    private void hideRuletaOverlay() {
        ventana_ruleta.setVisibility(View.GONE);
        ruleta.setVisibility(View.VISIBLE);
        diario.setVisibility(View.VISIBLE);
    }
    @SuppressLint("SetTextI18n")
    private void spinRoulette() {
        // Elegir un bono aleatorio
        Random random = new Random();
        currentBonus = bonuses[random.nextInt(bonuses.length)];

        // Calcular el ángulo de rotación de la ruleta
        int spinAngle = random.nextInt(360) + 720; // Girar al menos dos veces (720 grados)

        // Animación de rotación
        RotateAnimation rotate = new RotateAnimation(rotationAngle, rotationAngle + spinAngle,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(2000);  // Duración de 2 segundos
        rotate.setFillAfter(true);  // Mantener el ángulo después de la rotación

        // Iniciar la animación
        rouletteImage.startAnimation(rotate);

        // Actualizar el ángulo actual
        rotationAngle += spinAngle;

        // Mostrar el bono de monedas al final de la animación
        new Handler().postDelayed(() -> {
            bonusCoinsText.setText("Monedas ganadas: " + currentBonus);
            prefs.addMoney(currentBonus);
        }, 2000);  // 2 segundos para esperar a que termine la animación
    }
    private void iniciarTiempoDeCongelacion() {
        girar.setEnabled(false); // Bloquea el botón de ruleta

        timer = new CountDownTimer(TIEMPO_CONGELACION, 1000) {
            public void onTick(long millisUntilFinished) {
                girar.setText("Espera: " + millisUntilFinished / 1000 + "s"); // Opcional: muestra el tiempo restante
            }

            public void onFinish() {
                girar.setText("Activar Ruleta");
                girar.setEnabled(true); // Vuelve a habilitar el botón
            }
        }.start();
    }
}