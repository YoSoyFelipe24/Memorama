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

public class configuracion extends AppCompatActivity {

    AppCompatToggleButton toggleMusic, Notificacion;
    AppCompatImageButton Volver, regresarregis, Regresarini;
    AppCompatTextView HighScoreViewBorder, HighScoreTextView;
    AppCompatButton Cuenta, Iniciar, Registrar, Dificultad, Registro, FotoButton;
    RelativeLayout ventana_cuenta, ventana_registrar;
    AppCompatImageView Perfil;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri profileImageUri;
    DataBaseHelper dbHelper;
    SesionManager preferencesManager;
    TextView usernameText;
    ImageView profileImageView;
    AppCompatButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuracion);
        preferencesManager = new SesionManager(this);
        dbHelper = new DataBaseHelper(this);
        Volver = findViewById(R.id.back);
        Dificultad = findViewById(R.id.button_dificultad);
        usernameText = findViewById(R.id.user_name);
        profileImageView = findViewById(R.id.avatar);
        logoutButton = findViewById(R.id.Cerrar_Sesion);
        Cuenta = findViewById(R.id.button_cuenta);
        ventana_cuenta = findViewById(R.id.ventana_cuenta);
        ventana_registrar = findViewById(R.id.ventana_registrar);
        preferencesManager = new SesionManager(this); // Inicializa tu clase UserPreferences
        Notificacion = findViewById(R.id.button_noti);
        toggleMusic = findViewById(R.id.music_toggle);
        HighScoreViewBorder = findViewById(R.id.scoreborder);
        HighScoreTextView = findViewById(R.id.score);
        //Recuperar el high score
        int highScore = HighScoreManager.getHighScore(this);
        //Actualizar en UI
        HighScoreViewBorder.setText(String.valueOf(highScore));
        HighScoreTextView.setText(String.valueOf(highScore));

        mostrarDetallesUsuario(); // Muestra los detalles del usuario

        boolean isMusicEnabled = getSharedPreferences("app_settings", MODE_PRIVATE)
                .getBoolean("music_enabled", true);
        toggleMusic.setChecked(isMusicEnabled);

        toggleMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            musicManager.toggleMusic(this, isChecked);
            updateMusic(isChecked);
        });

        Cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar la ventana flotante
                showLoginOverlay();
            }
        });

        // Configurar el botón de iniciar sesión dentro de la ventana flotante
        Iniciar = ventana_cuenta.findViewById(R.id.login_button);
        Registrar = ventana_cuenta.findViewById(R.id.register_button);
        Regresarini = ventana_cuenta.findViewById(R.id.back_iniciar);
        Regresarini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLoginOverlay();
            }
        });
        Iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para iniciar sesión
                AppCompatEditText username = ventana_cuenta.findViewById(R.id.username);
                AppCompatEditText password = ventana_cuenta.findViewById(R.id.password);

                String user = username.getText().toString();
                String pass = password.getText().toString();
                IniciarSesion(user,pass);

                // Ocultar la ventana flotante después de iniciar sesión
                hideLoginOverlay();
                recreate();
            }
        });
        Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegistrar();
            }
        });
        //lo mismo en registrar
        Registro = ventana_registrar.findViewById(R.id.register_button);
        Perfil = ventana_registrar.findViewById(R.id.foto);
        FotoButton = ventana_registrar.findViewById(R.id.button_foto);
        regresarregis = ventana_registrar.findViewById(R.id.back_registrar);
        regresarregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideRegistrar();
            }
        });
        FotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrarUser();
                hideRegistrar();
            }
        });
        Notificacion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateNoti(isChecked);
        });

        Volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(configuracion.this, MainActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.clearUserDetails();
                mostrarDetallesUsuario(); // Actualiza la interfaz después de cerrar sesión
                Toast.makeText(getApplicationContext(), "Has cerrado sesión", Toast.LENGTH_SHORT).show();
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
    private void updateMusic(boolean isChecked){
        if(isChecked){
            Toast.makeText(this, "¡Musica activada!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "¡Musica desactivada!", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateNoti(boolean isChecked){
        if(isChecked){
            Toast.makeText(this, "¡Notificaciones Activadas!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "¡Notificaciones Desactivadas!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoginOverlay() {
        ventana_cuenta.setVisibility(View.VISIBLE);
        Cuenta.setVisibility(View.GONE);
        toggleMusic.setVisibility(View.GONE);
        Notificacion.setVisibility(View.GONE);
        Dificultad.setVisibility(View.GONE);
    }

    private void hideLoginOverlay() {
        ventana_cuenta.setVisibility(View.GONE);
        Cuenta.setVisibility(View.VISIBLE);
        toggleMusic.setVisibility(View.VISIBLE);
        Notificacion.setVisibility(View.VISIBLE);
        Dificultad.setVisibility(View.VISIBLE);
    }
    private void showRegistrar() {
        ventana_registrar.setVisibility(View.VISIBLE);
        ventana_cuenta.setVisibility(View.GONE);
        Cuenta.setVisibility(View.GONE);
        toggleMusic.setVisibility(View.GONE);
        Notificacion.setVisibility(View.GONE);
        Dificultad.setVisibility(View.GONE);
    }

    private void hideRegistrar() {
        ventana_registrar.setVisibility(View.GONE);
        Cuenta.setVisibility(View.VISIBLE);
        toggleMusic.setVisibility(View.VISIBLE);
        Notificacion.setVisibility(View.VISIBLE);
        Dificultad.setVisibility(View.VISIBLE);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profileImageUri = data.getData();
            Perfil.setImageURI(profileImageUri); // Muestra la imagen seleccionada
        }
    }

    private void RegistrarUser() {
        AppCompatEditText usernameEditText = ventana_registrar.findViewById(R.id.username);
        AppCompatEditText passwordEditText = ventana_registrar.findViewById(R.id.password);

        String username = usernameEditText.getText().toString().trim();
        String password = encriptar.encryptPassword(passwordEditText.getText().toString().trim());

        // Validación de entrada
        if (username.isEmpty()) {
            Toast.makeText(this, "El nombre de usuario no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
            return;
        }

        // Manejo de la imagen de perfil
        byte[] profileImage = null;
        if (profileImageUri != null) {
            try {
                Bitmap profileImageBitmap = getBitmapFromUri(profileImageUri);
                profileImage = imageToByteArray(profileImageBitmap);
            } catch (IOException e) {
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                return; // Termina el registro si no se puede cargar la imagen
            }
        }

        // Registro del usuario
        try {
            boolean isUserAdded = dbHelper.addUser(username, password, profileImage);
            if (isUserAdded) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
            }
        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
        }
    }
    private void IniciarSesion(String username, String password){
        // Encripta la contraseña antes de compararla
        String encryptedPassword = encriptar.encryptPassword(password);
        Log.d("Iniciar Sesión", "Username: " + username + ", Encrypted Password: " + encryptedPassword);
        // Verifica si el usuario existe en la base de datos
        if (dbHelper.checkUser(username, encryptedPassword)) {
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
            User user = dbHelper.getUser(username);
            preferencesManager.saveUserDetails(user.getUser(), user.getProfileImage(), user.getHighScore(), user.getMoney());
        } else {
            Toast.makeText(this, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        return MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
    }
    private byte[] imageToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // O usa CompressFormat.JPEG si prefieres
        return stream.toByteArray();
    }
    private void mostrarDetallesUsuario() {
        String username = preferencesManager.getUsername();
        byte[] profileImage = preferencesManager.getProfileImage();
        int highScore = preferencesManager.getHighScore();

        if (username != null) {
            usernameText.setText(username);
            HighScoreTextView.setText(""+highScore);
            HighScoreViewBorder.setText(""+highScore);

            if (profileImage != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(profileImage, 0, profileImage.length);
                profileImageView.setImageBitmap(bitmap);
            }

            logoutButton.setVisibility(View.VISIBLE); // Muestra el botón de cerrar sesión
        } else {
            // Si no hay usuario, oculta los elementos
            usernameText.setText("0001");
            HighScoreTextView.setText("000000");
            HighScoreViewBorder.setText("000000");
            profileImageView.setImageResource(R.drawable.user);
            logoutButton.setVisibility(View.GONE); // Oculta el botón de cerrar sesión
        }
    }
}