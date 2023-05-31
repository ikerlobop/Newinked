package com.example.newinked;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BajaActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference tatuadoresRef;
    private DatabaseReference usuariosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baja_activity);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        Button bajaButton = findViewById(R.id.bajaButton);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tatuadoresRef = database.getReference("tatuadores");
        usuariosRef = database.getReference("usuarios");

        bajaButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(BajaActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(BajaActivity.this);
            builder.setMessage("¿Estás seguro de que deseas eliminar al usuario? Los cambios no se podrán revertir.")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Usuario ha confirmado la eliminación
                            mAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(BajaActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = mAuth.getCurrentUser();

                                                if (user != null) {
                                                    user.delete()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> deleteTask) {
                                                                    if (deleteTask.isSuccessful()) {
                                                                        // Eliminar al usuario de Realtime Database
                                                                        eliminarUsuarioDeRealtimeDatabase(email);
                                                                        Toast.makeText(BajaActivity.this, "Usuario eliminado exitosamente", Toast.LENGTH_SHORT).show();
                                                                        finish();
                                                                    } else {
                                                                        Toast.makeText(BajaActivity.this, "No se pudo eliminar al usuario. Inténtelo de nuevo más tarde", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            } else {
                                                Toast.makeText(BajaActivity.this, "La autenticación falló. Verifique el correo electrónico y la contraseña", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Usuario ha cancelado la eliminación
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void eliminarUsuarioDeRealtimeDatabase(String email) {

        tatuadoresRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BajaActivity.this, "Error al eliminar al usuario de tatuadores: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        usuariosRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BajaActivity.this, "Error al eliminar al usuario de usuarios: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
