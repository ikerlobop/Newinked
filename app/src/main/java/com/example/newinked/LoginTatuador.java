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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginTatuador extends AppCompatActivity {

    EditText tatuadorEditText, contrasenaEditText;
    Button loginButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    DataSnapshot snapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tatuador);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        tatuadorEditText = findViewById(R.id.emailEditText);
        contrasenaEditText = findViewById(R.id.contrasenaEditText);
        loginButton = findViewById(R.id.loginButtonTatuador);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = tatuadorEditText.getText().toString();
                String contrasena = contrasenaEditText.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(contrasena)) {
                    Toast.makeText(LoginTatuador.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, contrasena)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    DatabaseReference tatuadorRef = mDatabase.child("tatuadores");
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
                                                Intent intent = new Intent(LoginTatuador.this, PerfilTatuador.class);
                                                intent.putStringArrayListExtra("tatuadorIds", (ArrayList<String>) tatuadorIds);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(LoginTatuador.this, "No se encontraron tatuadores con este correo electrónico", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Maneja el error
                                        }
                                    });
                                } else {
                                    Toast.makeText(LoginTatuador.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }
}
