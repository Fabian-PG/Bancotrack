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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnIrALista = findViewById(R.id.btn_ir_a_lista);

        btnIrALista.setOnClickListener(v -> {
            // Crear un Intent para abrir MainActivityBD
            // El Intent es como una solicitud para el sistema de que quieres hacer algo (en este caso, abrir una Activity)
            Intent intent = new Intent(MainActivity.this, MainActivityBD.class);
            startActivity(intent);

            // Opcional: cerrar esta Activity para que el usuario no pueda volver atrás con el botón 'back'
            //finish();
        });
    }
}