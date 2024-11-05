package com.example.memorama;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;

public class JuegoActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private Button[] buttons;
    private int[] cardValues;
    private Button firstCard, secondCard;
    private int firstCardIndex, secondCardIndex, intentos, CartasAcertadas = 0, pares;
    public int score, high;
    private boolean isFlipping = false, gameFinished;
    AppCompatImageButton Volver;
    private AppCompatTextView scoreTextView, scoreTextViewBorder, HighScoreTextView, HighScoreViewBorder;
    SesionManager prefs;
    DataBaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_juego);
        dbHelper = new DataBaseHelper(this);
        Volver = findViewById(R.id.back);
        scoreTextView = findViewById(R.id.scoreactual);
        scoreTextViewBorder = findViewById(R.id.scoreactualborder);
        HighScoreTextView = findViewById(R.id.score);
        HighScoreViewBorder = findViewById(R.id.scoreborder);
        musicManager.init(this, R.raw.music_memorama);
        prefs = new SesionManager(this);
        verificarSesion();
        updateScore();
        //Recuperar el high score
        int highScore = HighScoreManager.getHighScore(this);
        //Verificar y actualizar
        HighScoreManager.checkAndUpdateHS(this,score);
        prefs.saveHighScore(highScore);
        //Actualizar en UI
        HighScoreViewBorder.setText(String.valueOf(highScore));
        HighScoreTextView.setText(String.valueOf(highScore));

        Volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarAdvertencia("Al salir generaras una nueva partida, ¿estas seguro de salir?");
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        gridLayout = findViewById(R.id.gridLayout);
        buttons = new Button[16];
        cardValues = new int[16];


        // Crear las cartas con pares de valores (2 veces cada valor)
        pares = 8;
        ArrayList<Integer> cards = new ArrayList<>();
        for (int i = 0; i < pares; i++) {
            cards.add(i);
            cards.add(i);
        }

        // Mezclar las cartas
        Collections.shuffle(cards);

        // Asignar los valores mezclados a las cartas
        for (int i = 0; i < 16; i++) {
            cardValues[i] = cards.get(i);
        }

        // Crear los botones en la cuadrícula
        for (int i = 0; i < 16; i++) {
            final int index = i;
            buttons[i] = new Button(this);
            buttons[i].setText("?");
            buttons[i].setTextSize(14);
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isFlipping) {
                        flipCard(buttons[index], index);
                    }
                }
            });
            gridLayout.addView(buttons[i]);
        }
    }

    private void flipCard(Button button, int index) {
        button.setText(String.valueOf(cardValues[index]));

        if (firstCard == null) {
            // Guardar la primera carta volteada
            firstCard = button;
            firstCardIndex = index;
        } else if (firstCard != null && secondCard == null && firstCard != button) {
            // Guardar la segunda carta volteada
            secondCard = button;
            secondCardIndex = index;

            // Bloquear nuevas interacciones mientras se valida la coincidencia
            isFlipping = true;

            // Verificar si coinciden las cartas
            if (cardValues[firstCardIndex] == cardValues[secondCardIndex]) {
                // Incrementar el puntaje basado en la cantidad de intentos
                if (intentos == 0) {
                    score += 20;  // Primer intento
                    Toast.makeText(this, "¡Acertaste! +20 puntos", Toast.LENGTH_SHORT).show();
                } else if (intentos == 1) {
                    score += 15;  // Segundo intento
                    Toast.makeText(this, "¡Acertaste! +15 puntos", Toast.LENGTH_SHORT).show();
                } else {
                    score += 10;  // Tercer intento o más
                    Toast.makeText(this, "¡Acertaste! +10 puntos", Toast.LENGTH_SHORT).show();
                }
                intentos = 0; // Reiniciar el contador de intentos

                firstCard.setEnabled(false);
                secondCard.setEnabled(false);
                resetCards();
                CartasAcertadas++;
                //Verifica si acerto todas
                if (CartasAcertadas == pares){
                    endGame();
                    gameFinished = true;
                }
            } else {
                // No coinciden, voltear las cartas de nuevo después de un pequeño retraso
                gridLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        firstCard.setText("?");
                        secondCard.setText("?");
                        resetCards();
                    }
                }, 1000);
                // Restar puntos por un intento
                score-= 5;
                Toast.makeText(this, "¡Fallaste! -5 puntos", Toast.LENGTH_SHORT).show();
                intentos++;
            }
            //Actualizar el puntaje
            updateScore();
        }
    }

    private void updateScore(){
        scoreTextView.setText(String.valueOf(score));
        scoreTextViewBorder.setText(String.valueOf(score));
    }

    private void resetCards() {
        firstCard = null;
        secondCard = null;
        isFlipping = false;
    }

    public int getScore(){
        return score;
    }
    //Cuadro de dialogo termino el juegp
    private void AlertaFin(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Juego Terminado")
                .setMessage("¡Enhorabuena has completado el juego! Tu puntaje es: " + score)
                .setCancelable(false)
                .setPositiveButton("Reiniciar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resetGame();
                    }
                })
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(JuegoActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void resetGame(){
        recreate();
    }

    private void endGame(){
        HighScoreManager.checkAndUpdateHS(this,score);
        updateScore();
        AlertaFin();
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
        if (gameFinished){
            endGame();
        }

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
        } else {
            // Hay una sesión activa, permitir que el usuario juegue o continúe
            Toast.makeText(this, "A jugar, " + username, Toast.LENGTH_SHORT).show();
        }
    }
    private void mostrarAdvertencia(String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Advertencia")
                .setMessage(mensaje)
                .setCancelable(false) // Desactiva el cierre al tocar fuera
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(JuegoActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Cierra el diálogo
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
