package com.example.newinked;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

public class FormularioUsuarioRegistro extends AppCompatActivity {

    private EditText UsuarioNombre;

    private EditText UsuarioEmail;

    private EditText UsuarioContrasena;

    private EditText ConfirmaUsuarioContrasena;

    private EditText UsuarioUbicacion;

    private EditText UsuarioFechaNacimiento;

    Button botonRegistroUsuario;

    private DatabaseReference mdatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_usuario_registro);

        // Obtener referencias a los EditTexts y al botón de registro
        UsuarioNombre = findViewById(R.id.nombreCompletoEditText);
        UsuarioEmail = findViewById(R.id.correoElectronicoEditText);
        UsuarioContrasena = findViewById(R.id.contrasenaEditText);
        ConfirmaUsuarioContrasena = findViewById(R.id.confirmarContrasenaEditText);
        UsuarioFechaNacimiento = findViewById(R.id.fechaNacimientoEditText);
        UsuarioUbicacion = findViewById(R.id.ubicacionEditText);
        botonRegistroUsuario = findViewById(R.id.registrarButtonUsuario);


        // Obtener una instancia de la base de datos de Firebase
       mdatabase = FirebaseDatabase.getInstance().getReference();

        // Agregar un listener al botón de registro para guardar el objeto Usuario en la base de datos al pulsar
        botonRegistroUsuario.setOnClickListener(v -> {
            // Obtener los valores ingresados por el usuario
            String nombre = UsuarioNombre.getText().toString();
            String email = UsuarioEmail.getText().toString();
            String contrasena = UsuarioContrasena.getText().toString();
            String confirmaContrasena = ConfirmaUsuarioContrasena.getText().toString();
            String ubicacion = UsuarioUbicacion.getText().toString();
            String fechaNacimiento = UsuarioFechaNacimiento.getText().toString();


            // Validar que los campos no estén vacíos
            if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty() || fechaNacimiento.isEmpty()|| confirmaContrasena.isEmpty() || ubicacion.isEmpty()) {
                Toast.makeText(FormularioUsuarioRegistro.this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            //que coincidan las contraseñas
            else if  (!contrasena.equals(confirmaContrasena)) {
                Toast.makeText(FormularioUsuarioRegistro.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            } else {
                // Verificar si ya existe un usuario con el mismo email
                mdatabase.child("usuarios").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Si se encuentra un usuario con este email, mostrar un mensaje de error
                        if (snapshot.exists()) {
                            Toast.makeText(FormularioUsuarioRegistro.this, "Ya existe un usuario con este correo electrónico", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            // Crear un objeto Usuario con los valores ingresados
                            Usuario usuario = new Usuario(nombre, email, contrasena, ubicacion, fechaNacimiento);

                            // Guardar el objeto Usuario en la base de datos
                            mdatabase.child("usuarios").push().setValue(usuario);

                            // Escribimos en firebase auth el usuario
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, contrasena);

                            // Mostrar un mensaje de éxito al usuario
                            Toast.makeText(FormularioUsuarioRegistro.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();

                            // Limpiar los EditTexts
                            UsuarioNombre.setText("");
                            UsuarioEmail.setText("");
                            UsuarioContrasena.setText("");
                            UsuarioFechaNacimiento.setText("");
                            ConfirmaUsuarioContrasena.setText("");
                            UsuarioUbicacion.setText("");

                            // Llevar al usuario a la actividad LoginTatuador después de un registro exitoso
                            Intent intent = new Intent(FormularioUsuarioRegistro.this, LoginUsuario.class);
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
        });
    }
}