package com.example.newinked;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//firestore
import com.google.firebase.firestore.FirebaseFirestore;



public class FormularioTatuadorRegistro extends AppCompatActivity {


    private EditText TatuadorNombre;

    private EditText TatuadorEmail;

    private EditText TatuadorContrasena;

    private EditText ConfirmaTatuadorContrasena;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private EditText TatuadorUbicacion;


    Button botonRegistro;

    private DatabaseReference mdatabase;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_tatuador_registro);

        TatuadorNombre = findViewById(R.id.nombreCompletoEditText);
        TatuadorEmail = findViewById(R.id.correoElectronicoEditText);
        botonRegistro = findViewById(R.id.registrarButtonTatuador);
        ConfirmaTatuadorContrasena = findViewById(R.id.confirmarContrasenaEditText);
        TatuadorContrasena = findViewById(R.id.contrasenaEditText);
        TatuadorUbicacion = findViewById(R.id.ubicacionEditText);

        // Obtener una instancia de la base de datos de Firebase
        mdatabase = FirebaseDatabase.getInstance().getReference();

        // Agregar un listener al botón de registro para guardar el objeto Usuario en la base de datos al pulsar
        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores ingresados por el usuario
        String nombre = TatuadorNombre.getText().toString();
        String email = TatuadorEmail.getText().toString();
        String contrasena = TatuadorContrasena.getText().toString();
        String confirmaContrasena = ConfirmaTatuadorContrasena.getText().toString();
        String ubicacion = TatuadorUbicacion.getText().toString();



                // Validar que los campos no estén vacíos
                if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty() || confirmaContrasena.isEmpty() || ubicacion.isEmpty()) {
                    Toast.makeText(FormularioTatuadorRegistro.this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                //que coincidan las contraseñas
                else if  (!contrasena.equals(confirmaContrasena)) {
                    Toast.makeText(FormularioTatuadorRegistro.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    // Verificar si ya existe un usuario con el mismo email
                    mdatabase.child("tatuadores").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Si se encuentra un usuario con este email, mostrar un mensaje de error
                            if (snapshot.exists()) {
                                Toast.makeText(FormularioTatuadorRegistro.this, "Ya existe un usuario con este correo electrónico", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                // Crear un objeto Usuario con los valores ingresados
                                Usuario usuario = new Usuario(nombre, email, contrasena, ubicacion);

                                // Guardar el objeto Usuario en la base de datos
                                mdatabase.child("tatuadores").push().setValue(usuario);

                                // Escribimos en firebase auth el usuario
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, contrasena);

                                // Mostrar un mensaje de éxito al usuario
                                Toast.makeText(FormularioTatuadorRegistro.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();

                                // Limpiar los EditTexts
                                   TatuadorNombre.setText("");
                                      TatuadorEmail.setText("");
                                        TatuadorContrasena.setText("");
                                            ConfirmaTatuadorContrasena.setText("");
                                                TatuadorUbicacion.setText("");



                                // Llevar al usuario a la actividad LoginTatuador después de un registro exitoso
                                Intent intent = new Intent(FormularioTatuadorRegistro.this, LoginUsuario.class);
                                startActivity(intent);
                                finish(); // Cerrar la actividad actual para evitar que el usuario vuelva al formulario de registro
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Manejar el error
                        }
                    });
                }
            }
        });
    }
}