package com.example.newinked;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class RegistroActivity extends AppCompatActivity {

    Button btnUsuario, btnTatuador;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_activity);

        lottieAnimationView = findViewById(R.id.registrolottie);
        lottieAnimationView.setAnimation(R.raw.personlogin);
        lottieAnimationView.playAnimation();
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);

        // Buscamos nuestras vistas usando sus IDs
        btnUsuario = findViewById(R.id.btnUsuario);
        btnTatuador = findViewById(R.id.btnTatuador);

        // Definimos los listeners para nuestros botones
        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistroActivity.this, FormularioUsuarioRegistro.class);
                startActivity(intent);
            }
        });

        btnTatuador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistroActivity.this, FormularioTatuadorRegistro.class);
                startActivity(intent);
            }
        });
    }
}