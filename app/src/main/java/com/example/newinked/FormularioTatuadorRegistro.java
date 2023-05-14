package com.example.newinked;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FormularioTatuadorRegistro extends AppCompatActivity {

    private EditText UsuarioNombre;

    private EditText UsuarioEmail;

    Button botonRegistro;

    private DatabaseReference mdatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_tatuador_registro);

        // Obtener referencias a los EditTexts y al botón de registro
        UsuarioNombre = findViewById(R.id.nombreCompletoEditText);
        UsuarioEmail = findViewById(R.id.correoElectronicoEditText);
        botonRegistro = findViewById(R.id.registrarButtonTatuador);

        // Obtener una instancia de la base de datos de Firebase
        mdatabase = FirebaseDatabase.getInstance().getReference();

        // Agregar un listener al botón de registro para guardar el objeto Usuario en la base de datos al pulsar
        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores ingresados por el usuario
                String nombre = UsuarioNombre.getText().toString();
                String email = UsuarioEmail.getText().toString();

                // Validar que los campos no estén vacíos
                if (nombre.isEmpty() || email.isEmpty()) {
                    Toast.makeText(FormularioTatuadorRegistro.this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    // Crear un objeto Usuario con los valores ingresados
                    Tatuador tatuador = new Tatuador(nombre, email);

                    // Guardar el objeto Usuario en la base de datos
                    mdatabase.child("tatuadores").push().setValue(tatuador);

                    // Mostrar un mensaje de éxito al usuario
                    Toast.makeText(FormularioTatuadorRegistro.this, "Tatuador registrado correctamente", Toast.LENGTH_SHORT).show();

                    // Limpiar los EditTexts
                    UsuarioNombre.setText("");
                    UsuarioEmail.setText("");

                    // Llevar al usuario a la actividad LoginTatuador después de un registro exitoso
                    Intent intent = new Intent(FormularioTatuadorRegistro.this, LoginTatuador.class);
                    startActivity(intent);
                    finish(); // Cerrar la actividad actual para evitar que el usuario vuelva al formulario de registro
                }
            }
        });
    }
}