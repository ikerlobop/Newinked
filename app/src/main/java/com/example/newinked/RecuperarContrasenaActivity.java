package com.example.newinked;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContrasenaActivity extends AppCompatActivity {

    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        //Lottie
        LottieAnimationView animationView = findViewById(R.id.passwordLottie);
        animationView.setAnimation(R.raw.password);
        animationView.setRepeatCount(ValueAnimator.INFINITE); // repite indefinidamente el loading
        animationView.playAnimation();

        emailEditText = findViewById(R.id.emailEditText);
        Button enviarSolicitudButton = findViewById(R.id.enviarSolicitudButton);
        enviarSolicitudButton.setOnClickListener(view -> {
            String correo = emailEditText.getText().toString();

            if (TextUtils.isEmpty(correo)) {
                Toast.makeText(RecuperarContrasenaActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(correo)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RecuperarContrasenaActivity.this, "Solicitud enviada. Por favor revise su correo electrónico", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RecuperarContrasenaActivity.this, "No se pudo enviar la solicitud. Inténtelo de nuevo más tarde", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}