package com.example.newinked;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Buscador extends AppCompatActivity {

    private GridView photoGridView;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buscador_activity);

        TextView titleTextView = findViewById(R.id.titleTextView);
        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        photoGridView = findViewById(R.id.photoGridView);

        // Configurar el Spinner con las opciones
        String[] categories = new String[]{"Floral", "Oriental", "Lineal"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("tatuadores");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> imageUrls = new ArrayList<>();
                List<String> tatuadorNombres = new ArrayList<>();
                List<String> tatuadorUbicacions = new ArrayList<>();
                List<String> tatuadorEmails = new ArrayList<>();
                List<String> tatuadorTelefonos = new ArrayList<>();

                for (DataSnapshot tatuadorSnapshot : dataSnapshot.getChildren()) {
                    String tatuadorNombre = tatuadorSnapshot.child("nombre").getValue(String.class);
                    String tatuadorEmail = tatuadorSnapshot.child("email").getValue(String.class);
                    String tatuadorUbicacion = tatuadorSnapshot.child("ubicacion").getValue(String.class);
                    String tatuadorTelefono = tatuadorSnapshot.child("telefono").getValue(String.class);
                    for (DataSnapshot imageSnapshot : tatuadorSnapshot.child("imagenes").getChildren()) {
                        String imageUrl = imageSnapshot.getValue(String.class);
                        imageUrls.add(imageUrl);
                        tatuadorNombres.add(tatuadorNombre);
                        tatuadorUbicacions.add(tatuadorUbicacion);
                        tatuadorEmails.add(tatuadorEmail);
                        tatuadorTelefonos.add(tatuadorTelefono);
                    }
                }

                // Configurar el adaptador del GridView
                imageAdapter = new ImageAdapter(Buscador.this, imageUrls);
                photoGridView.setAdapter(imageAdapter);

                // Configurar el listener para cuando se haga click en una imagen
                photoGridView.setOnItemClickListener((parent, view, position, id) -> {
                    String ubicacion = tatuadorUbicacions.get(position);
                    String tatuadorNombre = tatuadorNombres.get(position);
                    String email = tatuadorEmails.get(position);
                    String telefono = tatuadorTelefonos.get(position);

                    Intent intent = new Intent(Buscador.this,PerfilTatuadorDesdeCliente.class);
                    intent.putExtra("nombre", tatuadorNombre);
                    intent.putExtra("email", email);
                    intent.putExtra("ubicacion", ubicacion);
                    intent.putExtra("telefono", telefono);
                    startActivity(intent);
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Buscador.this, "Error al cargar las imágenes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}



