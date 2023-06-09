package com.example.newinked;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginUsuario extends AppCompatActivity {

    EditText usuarioEditText, contrasenaEditText;
    Button loginButtonUsuario;
    TextView forgotPasswordTextView;

    // Declarar una instancia de FirebaseAuth del ámbito de la actividad
    FirebaseAuth mAuth;
    DatabaseReference mdatabase;

    DataSnapshot snapshot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_usuario);


        // Inicializamos el objeto FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference();

        // Referencias de los views
        usuarioEditText = findViewById(R.id.usuarioEditText);
        contrasenaEditText = findViewById(R.id.contrasenaEditText);
        loginButtonUsuario = findViewById(R.id.loginButtonUsuario);
        forgotPasswordTextView = findViewById(R.id.tvRegistro);

        // Listener del botón de inicio de sesión
        loginButtonUsuario.setOnClickListener(view -> {
            String email = usuarioEditText.getText().toString();
            String contrasena = contrasenaEditText.getText().toString();

            // Campos vacíos -> mensaje de error
            if (email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(LoginUsuario.this, "Por favor, ingrese su usuario y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            // Autenticamos usuario y contraseña
            mAuth.signInWithEmailAndPassword(email, contrasena)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DatabaseReference usuarioRef = mdatabase.child("usuarios");
                            Query query = usuarioRef.orderByChild("email").equalTo(email);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ArrayList<String> usuariosIds = new ArrayList<>();
                                    for (DataSnapshot usuarioSnapshot : snapshot.getChildren()) {
                                        String usuarioId = usuarioSnapshot.getKey();
                                        usuariosIds.add(usuarioId);
                                    }

                                    if (!usuariosIds.isEmpty()) {
                                        // Aquí tienes los IDs de los tatuadores
                                        // Puedes manejar los IDs como necesites
                                        Intent intent = new Intent(LoginUsuario.this, Buscador.class);
                                        intent.putStringArrayListExtra("usuariosIds", usuariosIds);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginUsuario.this, "No se encontraron usuarios con este correo electrónico", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Maneja el error
                                }
                            });
                        } else {
                            Toast.makeText(LoginUsuario.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        // Listener del texto "Olvidé mi contraseña"
        forgotPasswordTextView.setOnClickListener(view -> {
            Intent recuperarContrasenaIntent = new Intent(LoginUsuario.this, RecuperarContrasenaActivity.class);
            startActivity(recuperarContrasenaIntent);
        });
    }
}










