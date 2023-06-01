package com.example.newinked;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class InicioSesionActivity extends AppCompatActivity {

    Button btnUsuario2, btnTatuador2;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciosesion_activity);

        lottieAnimationView = findViewById(R.id.load);
        lottieAnimationView.setAnimation(R.raw.personlogin);
        lottieAnimationView.playAnimation();
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);


        btnUsuario2 = findViewById(R.id.btnUsuario2);
        btnTatuador2 = findViewById(R.id.btnTatuador2);

        //Se define aqui el listener para los botones
        btnUsuario2.setOnClickListener(view -> {
            Intent intent = new Intent(InicioSesionActivity.this, LoginUsuario.class);
            startActivity(intent);
        });

        btnTatuador2.setOnClickListener(view -> {
            Intent intent = new Intent(InicioSesionActivity.this, LoginTatuador.class);
            startActivity(intent);
        });
    }
}