package com.example.newinked;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InicioSesionActivity extends AppCompatActivity {

    Button btnUsuario2, btnTatuador2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciosesion_activity);

        // Buscamos nuestras vistas usando sus IDs
        btnUsuario2 = findViewById(R.id.btnUsuario2);
        btnTatuador2 = findViewById(R.id.btnTatuador2);

        // Definimos los listeners para nuestros botones
        btnUsuario2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Acciones a realizar cuando se presiona el botón "USUARIO"
                // por ejemplo, abrir una nueva actividad de registro para usuarios
            }
        });

        btnTatuador2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Acciones a realizar cuando se presiona el botón "TATUADOR"
                // por ejemplo, abrir una nueva actividad de registro para tatuadores
            }
        });
    }
}