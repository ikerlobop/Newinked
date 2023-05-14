package com.example.newinked;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class LoginTatuador extends AppCompatActivity {

    EditText emailEditText, contrasenaEditText;
    Button loginButton;

    // FirebaseAuth instance declaration
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tatuador);

        // Initialize our FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Find our views by their IDs
        emailEditText = findViewById(R.id.emailEditText);
        contrasenaEditText = findViewById(R.id.contrasenaEditText);
        loginButton = findViewById(R.id.loginButtonTatuador);

        // Listener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String contrasena = contrasenaEditText.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(contrasena)) {
                    Toast.makeText(LoginTatuador.this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Authenticate our user with Firebase Auth
                    mAuth.signInWithEmailAndPassword(email, contrasena)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // If login is successful, redirect our user to the main activity
                                        Intent intent = new Intent(LoginTatuador.this, Buscador.class);
                                        startActivity(intent);
                                        finish(); // Close the current activity to prevent the user from going back to the login screen
                                    } else {
                                        // If login fails, show an error message
                                        Toast.makeText(LoginTatuador.this, "Inicio de sesión fallido, verifique su correo electrónico y contraseña e intente de nuevo", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}