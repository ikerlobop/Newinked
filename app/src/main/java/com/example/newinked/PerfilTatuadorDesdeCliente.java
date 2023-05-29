package com.example.newinked;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilTatuadorDesdeCliente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_tatuador_desde_cliente);

        // Obtener los datos pasados por el intent
        String tatuadorNombre = getIntent().getStringExtra("nombre");
        String tatuadoUbicacion = getIntent().getStringExtra("ubicacion");
        String tatuadorEmail = getIntent().getStringExtra("email");
        ImageView imageViewPhoto = findViewById(R.id.imageView7);
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewEmail = findViewById(R.id.textViewEmail);
        TextView textViewLocation = findViewById(R.id.textViewLocation);
        Button buttonSendMessage = findViewById(R.id.buttonSendMessage);

        // Establecer el nombre del tatuador en el TextView
        textViewName.setText(tatuadorNombre);
        textViewLocation.setText(tatuadoUbicacion);
        textViewEmail.setText(tatuadorEmail);

        //enseñar imagen del tatuador en res
        imageViewPhoto.setImageResource(R.drawable.image);



        buttonSendMessage.setOnClickListener(v -> {

            Intent intent = new Intent(PerfilTatuadorDesdeCliente.this, EnviarMensaje.class);
            intent.putExtra("nombre", tatuadorNombre);
            intent.putExtra("email", tatuadorEmail);
            intent.putExtra("ubicacion", tatuadoUbicacion);
            startActivity(intent);
            startActivity(intent);
        });
    }
}


