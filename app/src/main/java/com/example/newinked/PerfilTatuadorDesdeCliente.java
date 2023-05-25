package com.example.newinked;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerfilTatuadorDesdeCliente extends AppCompatActivity {
    private DatabaseReference tatuadorRef;

    private Button enviarMensajeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_tatuador_desde_cliente);

        // Inicializar la referencia a la base de datos
        tatuadorRef = FirebaseDatabase.getInstance().getReference().child("tatuadores").child("-NWDR-VxVAMK2W-h7yiA");

        // Inicializar las vistas
        enviarMensajeButton = findViewById(R.id.buttonSendMessage);

        // Obtener datos del tatuador
        obtenerDatosTatuador();

        // Establecer el listener del botón de enviar mensaje
        enviarMensajeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la actividad EnviarMensajeActivity
                Intent intent = new Intent(PerfilTatuadorDesdeCliente.this, EnviarMensaje.class);
                startActivity(intent);
            }
        });
    }

    private void obtenerDatosTatuador() {
        tatuadorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Tatuador tatuador = dataSnapshot.getValue(Tatuador.class);
                    if (tatuador != null) {
                        // Aquí puedes utilizar los datos del tatuador
                        String nombre = tatuador.getNombre();
                        String email = tatuador.getEmail();
                        String ubicacion = tatuador.getUbicacion();

                        // Ejemplo de uso: Imprimir los datos
                        System.out.println("Nombre: " + nombre);
                        System.out.println("Email: " + email);
                        System.out.println("Ubicación: " + ubicacion);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error en caso de que ocurra
                System.out.println("Error al leer los datos del tatuador: " + databaseError.getMessage());
            }
        });
    }
}

