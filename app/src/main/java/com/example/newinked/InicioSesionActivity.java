package com.example.newinked;

import android.content.Intent;
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
                Intent intent = new Intent(InicioSesionActivity.this, LoginUsuario.class);
                startActivity(intent);
            }
        });

        btnTatuador2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioSesionActivity.this, LoginTatuador.class);
                startActivity(intent);
            }
        });
    }
}