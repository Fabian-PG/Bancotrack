package com.example.bancotrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnMapa;
    private Button btnInformacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnMapa = findViewById(R.id.btnMapa); // Asume que el ID es btnMapa

        btnMapa.setOnClickListener(v -> {
            // 1. Crear el Intent: Define que quieres ir de MainActivity a Pag_Mapa
            Intent intent = new Intent(MainActivity.this, Pag_Mapa.class);

            // 2. Iniciar la actividad
            startActivity(intent);
        });

        Button btnInformacion = findViewById(R.id.btnInformacion);

        btnInformacion.setOnClickListener(v -> {
            // Crear un Intent para abrir MainActivityBD
            // El Intent es como una solicitud para el sistema de que quieres hacer algo (en este caso, abrir una Activity)
            Intent intent = new Intent(MainActivity.this, MainActivityBD.class);
            startActivity(intent);

            // Opcional: cerrar esta Activity para que el usuario no pueda volver atrás con el botón 'back'
            //finish();
        });
    }
}