package com.example.newinked;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FormularioUsuarioRegistro extends AppCompatActivity {

    private EditText UsuarioNombre;

    private EditText UsuarioEmail;

    private EditText UsuarioContrasena;

    private EditText ConfirmaUsuarioContrasena;

    private EditText UsuarioUbicacion;

    private Button botonRegistro;

    private DatabaseReference mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_usuario_registro);

        // Obtener referencias a los EditTexts y al botón de registro
        UsuarioNombre = findViewById(R.id.nombreCompletoEditText);
        UsuarioEmail = findViewById(R.id.correoElectronicoEditText);
        UsuarioContrasena = findViewById(R.id.contrasenaEditText);
        ConfirmaUsuarioContrasena = findViewById(R.id.confirmarContrasenaEditText);
        UsuarioUbicacion = findViewById(R.id.ubicacionEditText);
        botonRegistro = findViewById(R.id.registrarButton);


        // Obtener una instancia de la base de datos de Firebase
       mdatabase = FirebaseDatabase.getInstance().getReference();

        // Agregar un listener al botón de registro para guardar el objeto Usuario en la base de datos al pulsar
        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores ingresados por el usuario
                String nombre = UsuarioNombre.getText().toString();
                String email = UsuarioEmail.getText().toString();
                String contrasena = UsuarioContrasena.getText().toString();
                String confirmaContrasena = ConfirmaUsuarioContrasena.getText().toString();
                String ubicacion = UsuarioUbicacion.getText().toString();

                // Validar que los campos no estén vacíos
                if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty() || confirmaContrasena.isEmpty() || ubicacion.isEmpty()) {
                    Toast.makeText(FormularioUsuarioRegistro.this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                //que coincidan las contraseñas
                else if  (!contrasena.equals(confirmaContrasena)) {
                    Toast.makeText(FormularioUsuarioRegistro.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {

                    // Crear un objeto Usuario con los valores ingresados
                    Usuario usuario = new Usuario(nombre, email, contrasena, ubicacion);

                    // Guardar el objeto Usuario en la base de datos
                    mdatabase.child("usuarios").push().setValue(usuario);

                    // Mostrar un mensaje de éxito al usuario
                    Toast.makeText(FormularioUsuarioRegistro.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();

                    // Limpiar los EditTexts
                    UsuarioNombre.setText("");
                    UsuarioEmail.setText("");
                    UsuarioContrasena.setText("");
                    UsuarioUbicacion.setText("");
                }
            }
        });
    }
}