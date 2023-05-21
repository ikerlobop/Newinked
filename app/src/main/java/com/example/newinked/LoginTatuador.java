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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginTatuador extends AppCompatActivity {

    EditText usuarioEditText, contrasenaEditText;
    Button loginButton;

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
        loginButton = findViewById(R.id.loginButton);

        // Listener del botón de inicio de sesión
        loginButton.setOnClickListener(new View.OnClickListener() {
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
                                    // Verificar si el correo y la contraseña coinciden en la base de datos
                                    mdatabase.child("tatuadores").child(mAuth.getCurrentUser().getUid()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        // Guardamos la ID del usuario generada en la base de datos
                                                        String id = mAuth.getCurrentUser().getUid();

                                                        // Obtén la ID generada por Firebase
                                                        DatabaseReference tatuadorRef = mdatabase.child("tatuadores").child(id);
                                                        String firebaseId = tatuadorRef.getKey();

                                                        // Utilizar la nueva ID generada en lugar de la obtenida del intent
                                                        firebaseId = Objects.requireNonNull(firebaseId);


                                                        // Si las credenciales son correctas, iniciar la actividad de PerfilTatuador
                                                        Intent intent = new Intent(LoginTatuador.this, PerfilTatuador.class);
                                                        // Pasar la ID generada por Firebase a la actividad de PerfilTatuador
                                                        intent.putExtra("id", firebaseId);

                                                        startActivity(intent);
                                                        finish();


                                                } else {
                                                        // Si las credenciales no coinciden, mostrar un mensaje de error
                                                        Toast.makeText(LoginTatuador.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // Si la autenticación con Firebase Auth falla, mostrar un mensaje de error
                                    Toast.makeText(LoginTatuador.this, "Error al autenticar usuario", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });
            }
        });
    }
}


