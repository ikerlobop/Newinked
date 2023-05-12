package com.example.newinked;

import static com.example.newinked.R.*;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    Button loginButton;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        registerButton = findViewById(id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });

        loginButton = findViewById(id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InicioSesionActivity.class);
                startActivity(intent);
            }
        });

        // Establecer la animación de carga WELCOME
        LottieAnimationView animationViewLogin = findViewById(R.id.lottie1);
        animationViewLogin.setAnimation(raw.registrate);
        animationViewLogin.playAnimation();
        animationViewLogin.setRepeatCount(ValueAnimator.INFINITE);

        // Establecer la animación de carga en la pantalla de bienvenida
        LottieAnimationView animationViewLogin2 = findViewById(R.id.lottie2);
        animationViewLogin2.setAnimation(raw.login);
        animationViewLogin2.playAnimation();
        animationViewLogin2.setRepeatCount(ValueAnimator.INFINITE);
    }
}
