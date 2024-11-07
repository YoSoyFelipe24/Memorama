package com.example.memorama;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    private androidx.gridlayout.widget.GridLayout gridLayout;
    private ImageButton[] buttons;
    private int[] cardValues;
    private ImageButton firstCard, secondCard;
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
        buttons = new ImageButton[16];
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
        // Número de columnas en el GridLayout
        int numColumns = 4;

// Configura el GridLayout para que tenga el número de columnas deseadas
        gridLayout.setColumnCount(numColumns);

// Usa ViewTreeObserver para obtener el ancho del GridLayout después de que se haya renderizado
        gridLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Obtener el ancho del GridLayout
                int gridWidth = gridLayout.getWidth();
                int gridHeight = gridLayout.getHeight(); // Altura del GridLayout

                // Calcular el tamaño de cada botón en función del ancho del GridLayout y el número de columnas
                int buttonSize = gridWidth / numColumns; // Ajusta el valor de margen si es necesario
                int rowHeight = gridHeight / 4; // Ajuste de tamaño vertical para las filas

                // Crear los botones en la cuadrícula
                for (int i = 0; i < 16; i++) {
                    final int index = i;
                    buttons[i] = new ImageButton(JuegoActivity.this);
                    buttons[i].setImageResource(R.drawable.card_back); // Imagen de dorso de la carta
                    buttons[i].setBackgroundColor(Color.TRANSPARENT);
                    buttons[i].setScaleType(ImageButton.ScaleType.FIT_CENTER);

                    // Configurar el tamaño y márgenes de cada botón
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = buttonSize;
                    params.height = rowHeight;
                    buttons[i].setLayoutParams(params);

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

                // Eliminar el listener para que el código no se ejecute varias veces
                gridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void flipCard(ImageButton button, int index) {
        int cardValue = cardValues[index];
        int resourceId = getResources().getIdentifier("card_" + cardValue, "drawable", getPackageName());
        button.setImageResource(resourceId); // Cambia a la imagen correspondiente

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
                // En caso de que las cartas no coincidan
                gridLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Restaura la imagen de las cartas al dorso
                        firstCard.setImageResource(R.drawable.card_back); // Cambia "card_back" al nombre de tu imagen de dorso
                        firstCard.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
                        secondCard.setImageResource(R.drawable.card_back);
                        secondCard.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);

                        // Habilitar las cartas para ser clickeadas nuevamente
                        firstCard.setEnabled(true);
                        secondCard.setEnabled(true);

                        // Restablece las cartas seleccionadas y el estado de volteo
                        resetCards();
                    }
                }, 1000); // Retraso de 1 segundo (puedes ajustar el tiempo)
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
