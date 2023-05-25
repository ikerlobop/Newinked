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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginUsuario extends AppCompatActivity {

    EditText usuarioEditText, contrasenaEditText;
    Button loginButtonUsuario;

    // Declarar una instancia de FirebaseAuth del ámbito de la actividad
    FirebaseAuth mAuth;
    DatabaseReference mdatabase;

    DataSnapshot snapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_usuario);


        // Inicializar la instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference();

        // Referencias a las vistas
        usuarioEditText = findViewById(R.id.usuarioEditText);
        contrasenaEditText = findViewById(R.id.contrasenaEditText);
        loginButtonUsuario = findViewById(R.id.loginButtonUsuario);

        // Listener del botón de inicio de sesión
        loginButtonUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String email = usuarioEditText.getText().toString();
                 String contrasena = contrasenaEditText.getText().toString();

                // Verificar que los campos no estén vacíos
                if (email.isEmpty() || contrasena.isEmpty()) {
                    Toast.makeText(LoginUsuario.this, "Por favor, ingrese su usuario y contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Autenticar al usuario con Firebase Auth
                mAuth.signInWithEmailAndPassword(email, contrasena)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    DatabaseReference tatuadorRef = mdatabase.child("usuarios");
                                    Query query = tatuadorRef.orderByChild("email").equalTo(email);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            List<String> tatuadorIds = new ArrayList<>();
                                            for (DataSnapshot tatuadorSnapshot : snapshot.getChildren()) {
                                                String tatuadorId = tatuadorSnapshot.getKey();
                                                tatuadorIds.add(tatuadorId);
                                            }

                                            if (!tatuadorIds.isEmpty()) {
                                                // Aquí tienes los IDs de los tatuadores
                                                // Puedes manejar los IDs como necesites
                                                Intent intent = new Intent(LoginUsuario.this, Buscador.class);
                                                intent.putStringArrayListExtra("tatuadorIds", (ArrayList<String>) tatuadorIds);
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
                            }
                        });

            }
        });
    }
}









