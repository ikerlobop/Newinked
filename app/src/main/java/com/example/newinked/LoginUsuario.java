package com.example.newinked;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginUsuario extends AppCompatActivity {

    EditText usuarioEditText, contrasenaEditText;
    Button loginButtonUsuario;

    // Declarar una instancia de FirebaseAuth del ámbito de la actividad
    FirebaseAuth mAuth;
    private DatabaseReference mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_usuario);

        // Obtener una instancia de la base de datos de Firebase
        mdatabase = FirebaseDatabase.getInstance().getReference();

        // Inicializar la instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Referencias a las vistas
        usuarioEditText = findViewById(R.id.usuarioEditText);
        contrasenaEditText = findViewById(R.id.contrasenaEditText);
        loginButtonUsuario = findViewById(R.id.loginButtonUsuario);

        // Listener del botón de inicio de sesión
        loginButtonUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = usuarioEditText.getText().toString();
                final String contrasena = contrasenaEditText.getText().toString();

                // Autenticar al usuario con Firebase Auth
                mAuth.signInWithEmailAndPassword(email, contrasena)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Verificar si el usuario existe en la base de datos
                                    mdatabase.child("usuarios").child(mAuth.getCurrentUser().getUid()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        // Si el usuario existe, iniciar la actividad de Buscador
                                                        Intent intent = new Intent(LoginUsuario.this, Buscador.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        // Si el usuario no existe, mostrar un mensaje de error
                                                        Toast.makeText(LoginUsuario.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // Si la autenticación con Firebase Auth falla, mostrar un mensaje de error
                                    Toast.makeText(LoginUsuario.this, "Error al autenticar usuario", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}









