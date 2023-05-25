package com.example.newinked;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

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

    private TextView titleTextView;
    private Spinner categorySpinner;
    private GridView photoGridView;

    private List<String> allPhotoUrls; // Lista de todas las URLs de las fotos
    private List<String> filteredPhotoUrls; // Lista de URLs de fotos filtradas por categoría

    private DatabaseReference photosRef; // Referencia a la ubicación de las fotos en la base de datos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buscador_activity);

        // Obtener referencias a los elementos de la interfaz
        titleTextView = findViewById(R.id.titleTextView);
        categorySpinner = findViewById(R.id.categorySpinner);
        photoGridView = findViewById(R.id.photoGridView);

        // Configurar el Spinner con las opciones
        List<String> categories = new ArrayList<>();
        categories.add("Floral");
        categories.add("Oriental");
        categories.add("Lineal");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        // Obtener referencia a la ubicación de las fotos en la base de datos (ajusta la ruta según tu estructura)
        photosRef = FirebaseDatabase.getInstance().getReference().child("photos");

        // Inicializar las listas de URLs de las fotos
        allPhotoUrls = new ArrayList<>();
        filteredPhotoUrls = new ArrayList<>();

        // Configurar el adaptador para el GridView
        ArrayAdapter<String> gridAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, filteredPhotoUrls);
        photoGridView.setAdapter(gridAdapter);

        // Establecer el listener para el evento de selección del Spinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories.get(position);

                // Filtrar las URLs de las fotos según la categoría seleccionada
                filterPhotosByCategory(selectedCategory);

                // Notificar al adaptador que los datos han cambiado
                gridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se requiere acción específica cuando no se selecciona nada en el Spinner
            }
        });

        // Escuchar los cambios en la ubicación de las fotos en la base de datos
        photosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Limpiar las listas de URLs de las fotos
                allPhotoUrls.clear();
                filteredPhotoUrls.clear();

                // Recorrer los datos de las fotos y obtener las URLs
                for (DataSnapshot photoSnapshot : dataSnapshot.getChildren()) {
                    // Obtener la URL y la categoría de la foto
                    String url = photoSnapshot.child("url").getValue(String.class);
                    String category = photoSnapshot.child("category").getValue(String.class);

                    // Agregar la URL a la lista de todas las URLs de las fotos
                    allPhotoUrls.add(url);

                    // Verificar si la foto pertenece a la categoría seleccionada actualmente
                    if (category.equals(categorySpinner.getSelectedItem())) {
                        // Agregar la URL a la lista de URLs de las fotos filtradas
                        filteredPhotoUrls.add(url);
                    }
                }

                // Notificar al adaptador que los datos han cambiado
                gridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error de lectura de la base de datos, si es necesario
            }
        });
    }

    private void filterPhotosByCategory(String category) {
        // Limpiar la lista de URLs de fotos filtradas
        filteredPhotoUrls.clear();

        // Recorrer todas las URLs de las fotos y filtrar por categoría
        for (String photoUrl : allPhotoUrls) {
            // Comparar la categoría de la foto con la categoría seleccionada
            if (isPhotoInCategory(photoUrl, category)) {
                // Agregar la URL de la foto a la lista filtrada
                filteredPhotoUrls.add(photoUrl);
            }
        }
    }

    private boolean isPhotoInCategory(String photoUrl, String category) {
        // Implementa tu lógica para determinar si una foto pertenece a una categoría
        // Puedes utilizar la URL de la foto o cualquier otra propiedad de la foto para hacer la verificación
        // Devuelve true si la foto pertenece a la categoría, de lo contrario, devuelve false

        // Ejemplo de lógica de verificación simple: verificar si la URL de la foto contiene la categoría
        return photoUrl.contains(category);
    }
}


