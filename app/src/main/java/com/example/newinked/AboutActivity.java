package com.example.newinked;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import com.airbnb.lottie.LottieAnimationView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        LottieAnimationView animation_view5 = findViewById(R.id.animation_view5);

        // Establecer la animación de carga en la pantalla de bienvenida
        animation_view5.setAnimation(R.raw.tattoo);
        animation_view5.setRepeatCount(ValueAnimator.INFINITE); // Repetir indefinidamente la animación
        animation_view5.setSpeed(0.2f); // Establecer velocidad lenta (0.5f = mitad de la velocidad normal)
        animation_view5.playAnimation();

        LottieAnimationView avFlecha = findViewById(R.id.personlogin2);

        // Establecer la animación de carga en la pantalla de bienvenida
        avFlecha.setAnimation(R.raw.directional);
        avFlecha.setRepeatCount(ValueAnimator.INFINITE); // Repetir indefinidamente la animación
        avFlecha.setSpeed(0.50f); // Establecer velocidad lenta (0.75f = 3/4 de la velocidad normal)
        avFlecha.playAnimation();

        // Agregar un Listener al avFlecha para hacerlo interactivo como un botón
        avFlecha.setOnClickListener(v -> {
            // Acción a realizar cuando se hace clic en avFlecha (simulando un botón)
            // Redirigir a la actividad MainActivity
            Intent intent = new Intent(AboutActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
