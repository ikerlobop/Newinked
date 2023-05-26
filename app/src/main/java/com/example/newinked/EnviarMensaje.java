package com.example.newinked;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EnviarMensaje extends AppCompatActivity {
    private EditText mensajeEditText;

    private DatabaseReference mensajesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enviar_mensaje);

        // Inicializar la referencia a la base de datos
        mensajesRef = FirebaseDatabase.getInstance().getReference().child("mensajes");

        // Inicializar las vistas
        mensajeEditText = findViewById(R.id.editTextMessage);
        Button enviarMensajeButton = findViewById(R.id.buttonSend);

        // Establecer el listener del botón de enviar mensaje
        enviarMensajeButton.setOnClickListener(v -> enviarMensaje());
    }

    private void enviarMensaje() {
        // Obtener el mensaje del EditText
        String mensaje = mensajeEditText.getText().toString().trim();

        // Verificar si el mensaje no está vacío
        if (!mensaje.isEmpty()) {
            // Generar un nuevo ID para el mensaje
            String mensajeId = mensajesRef.push().getKey();

            // Guardar el mensaje en la base de datos
            assert mensajeId != null;
            mensajesRef.child(mensajeId).setValue(mensaje);

            // Mostrar mensaje de éxito
            Toast.makeText(this, "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show();

            // Limpiar el EditText
            mensajeEditText.setText("");
        } else {
            // Mostrar mensaje de error si el campo está vacío
            Toast.makeText(this, "Por favor, ingresa un mensaje", Toast.LENGTH_SHORT).show();
        }
    }
}

