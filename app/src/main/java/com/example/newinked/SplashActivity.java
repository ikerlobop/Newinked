package com.example.newinked;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import android.animation.ValueAnimator;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //oculta la barra de notificaciones
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);

        //animación de la pantalla de bienvenida
        LottieAnimationView animationView2 = findViewById(R.id.animation_view2);
        animationView2.setAnimation(R.raw.phrases);
        animationView2.playAnimation();
        animationView2.setRepeatCount(ValueAnimator.INFINITE);

        //Carga de la animación de loading
        LottieAnimationView animationView = findViewById(R.id.load);
        animationView.setAnimation(R.raw.load);
        animationView.setRepeatCount(ValueAnimator.INFINITE); // repite indefinidamente el loading
        animationView.playAnimation();

        //Se inicia la actividad principal
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN_DELAY);
    }
}