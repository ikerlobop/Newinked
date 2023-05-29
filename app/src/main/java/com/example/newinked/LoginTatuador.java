package com.example.newinked;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class LoginTatuador extends AppCompatActivity {

    EditText tatuadorEditText, contrasenaEditText;
    Button loginButton;
    TextView forgotPasswordTextView;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tatuador);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        tatuadorEditText = findViewById(R.id.emailEditText);
        contrasenaEditText = findViewById(R.id.contrasenaEditText);
        loginButton = findViewById(R.id.loginButtonTatuador);
        forgotPasswordTextView = findViewById(R.id.tvRegistro);

        loginButton.setOnClickListener(view -> {
            String email = tatuadorEditText.getText().toString();
            String contrasena = contrasenaEditText.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(contrasena)) {
                Toast.makeText(LoginTatuador.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, contrasena)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DatabaseReference tatuadorRef = mDatabase.child("tatuadores");
                            Query query = tatuadorRef.orderByChild("email").equalTo(email);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ArrayList<String> tatuadorIds = new ArrayList<>();
                                    for (DataSnapshot tatuadorSnapshot : snapshot.getChildren()) {
                                        String tatuadorId = tatuadorSnapshot.getKey();
                                        tatuadorIds.add(tatuadorId);
                                    }

                                    if (!tatuadorIds.isEmpty()) {
                                        Intent intent = new Intent(LoginTatuador.this, PerfilTatuador.class);
                                        intent.putStringArrayListExtra("tatuadorIds", tatuadorIds);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginTatuador.this, "No se encontraron tatuadores con este correo electrónico", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginTatuador.this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(LoginTatuador.this, "Error al iniciar sesión. Verifique sus credenciales e intente nuevamente", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        forgotPasswordTextView.setOnClickListener(view -> {
            Intent recuperarContrasenaIntent = new Intent(LoginTatuador.this, RecuperarContrasenaActivity.class);
            startActivity(recuperarContrasenaIntent);
        });
    }
}
