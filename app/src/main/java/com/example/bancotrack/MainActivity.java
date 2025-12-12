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


            // Crear un Intent para abrir MainActivityBD
            // El Intent es como una solicitud para el sistema de que quieres hacer algo (en este caso, abrir una Activity)
            Intent intent = new Intent(MainActivity.this, MainActivityBD.class);
            startActivity(intent);

            // Opcional: cerrar esta Activity para que el usuario no pueda volver atrás con el botón 'back'
            //finish();
        });
    }
}