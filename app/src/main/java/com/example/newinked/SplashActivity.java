package com.example.newinked;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import android.animation.ValueAnimator;
import android.animation.Animator;
import com.airbnb.lottie.LottieDrawable;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Establecer la pantalla de la bienvenida a pantalla completa
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);

        // Establecer la animación de carga WELCOME
        LottieAnimationView animationView2 = findViewById(R.id.animation_view2);
        animationView2.setAnimation(R.raw.welcome2);
        animationView2.playAnimation();
        animationView2.setRepeatMode(LottieDrawable.REVERSE);
        animationView2.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animationView2.setProgress(1f);
                animationView2.cancelAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });


        // Establecer la animación de carga en la pantalla de bienvenida
        LottieAnimationView animationView = findViewById(R.id.animation_view);
        animationView.setAnimation(R.raw.tattoo);
        animationView.setRepeatCount(ValueAnimator.INFINITE); // repite indefinidamente el loading
        animationView.playAnimation();

        // Iniciar la actividad principal después de un retraso de SPLASH_SCREEN_DELAY
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_DELAY);
    }
}