package com.example.newinked;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContrasenaActivity extends AppCompatActivity {

    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        emailEditText = findViewById(R.id.emailEditText);
        Button enviarSolicitudButton = findViewById(R.id.enviarSolicitudButton);
        enviarSolicitudButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = emailEditText.getText().toString();

                if (TextUtils.isEmpty(correo)) {
                    Toast.makeText(RecuperarContrasenaActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth.getInstance().sendPasswordResetEmail(correo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RecuperarContrasenaActivity.this, "Solicitud enviada. Por favor revise su correo electrónico", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(RecuperarContrasenaActivity.this, "No se pudo enviar la solicitud. Inténtelo de nuevo más tarde", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}