package com.example.newinked;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Buscador.this, R.array.categoriaopciones, R.layout.spinner_item_preview);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        categorySpinner.setAdapter(adapter);

        // Agregar el listener al Spinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateGridView(); // Llamar al método para actualizar la vista cuando se seleccione una opción en el Spinner
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateGridView() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tatuadores");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Spinner categorySpinner = findViewById(R.id.categorySpinner);
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

                    // Buscamos por estilo de tatuaje dentro de imagenes en el estilo con hashmap key
                    for (DataSnapshot imageSnapshot : tatuadorSnapshot.child("imagenes").getChildren()) {
                        String imageUrl = imageSnapshot.child("imageUrl").getValue(String.class);
                        // Si el estilo es igual al seleccionado en el Spinner, se añaden las imágenes
                        if (categorySpinner.getSelectedItem().toString().equals("Todos")) {
                            // Agregar todas las imágenes sin filtrar
                            imageUrls.add(imageUrl);
                            tatuadorNombres.add(tatuadorNombre);
                            tatuadorUbicacions.add(tatuadorUbicacion);
                            tatuadorEmails.add(tatuadorEmail);
                            tatuadorTelefonos.add(tatuadorTelefono);
                        } else {
                            // Filtrar por estilo seleccionado en el Spinner
                            String estiloSeleccionado = categorySpinner.getSelectedItem().toString();
                            String estilo = imageSnapshot.child("estilo").getValue(String.class);
                            if (estiloSeleccionado.equals(estilo)) {
                                  imageUrls.add(imageUrl);
                                  tatuadorNombres.add(tatuadorNombre);
                                  tatuadorUbicacions.add(tatuadorUbicacion);
                                  tatuadorEmails.add(tatuadorEmail);
                                  tatuadorTelefonos.add(tatuadorTelefono);

                            }
                        }
                    }
                }

                // Configuramos el adaptador del GridView
                imageAdapter = new ImageAdapter(Buscador.this, imageUrls);
                photoGridView.setAdapter(imageAdapter);

                // Configuramos el listener para cuando se haga clic en una imagen
                photoGridView.setOnItemClickListener((parent, view, position, id) -> {
                    String ubicacion = tatuadorUbicacions.get(position);
                    String tatuadorNombre = tatuadorNombres.get(position);
                    String email = tatuadorEmails.get(position);
                    String telefono = tatuadorTelefonos.get(position);

                    // Lanzamos intent con los datos para pasar a la siguiente activity
                    Intent intent = new Intent(Buscador.this, PerfilTatuadorDesdeCliente.class);
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









