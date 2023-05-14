package com.example.newinked;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class LoginUsuario extends AppCompatActivity {

    EditText usuarioEditText, contrasenaEditText;
    Button loginButton;

    // Declarar una instancia de FirebaseAuth del ámbito de la actividad
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_usuario);

        // Inicializar la instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Referencias a las vistas
        usuarioEditText = findViewById(R.id.usuarioEditText);
        contrasenaEditText = findViewById(R.id.contrasenaEditText);
        loginButton = findViewById(R.id.loginButton);

        // Listener del botón de inicio de sesión
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = contrasenaEditText.getText().toString();
                String contrasena = contrasenaEditText.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(contrasena)) {
                    Toast.makeText(LoginUsuario.this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        usuarioEditText.setError("El correo electrónico ingresado no es válido");
                        usuarioEditText.requestFocus();
                        return;
                    } else {
                        // Autenticar al usuario con Firebase Auth
                        mAuth.signInWithEmailAndPassword(email, contrasena)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Si el inicio de sesión es exitoso, redirigir al usuario a la pantalla principal
                                            Intent intent = new Intent(LoginUsuario.this, Buscador.class);
                                            startActivity(intent);
                                            finish(); // Cerrar la actividad actual para evitar que el usuario vuelva a la pantalla de inicio de sesión
                                        } else {
                                            try {
                                                throw Objects.requireNonNull(task.getException());
                                            } catch (FirebaseAuthInvalidUserException invalidEmail) {
                                                Toast.makeText(LoginUsuario.this, "La cuenta de correo electrónico no existe", Toast.LENGTH_SHORT).show();
                                            } catch (
                                                    FirebaseAuthInvalidCredentialsException invalidPassword) {
                                                Toast.makeText(LoginUsuario.this, "La contraseña es incorrecta", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                Toast.makeText(LoginUsuario.this, "No se pudo iniciar sesión, intente de nuevo", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }
}