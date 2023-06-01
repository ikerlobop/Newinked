package com.example.newinked;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PerfilTatuador extends AppCompatActivity {

    private static final int REQUEST_SELECT_IMAGE = 1;

    private EditText nombreEditText;
    private EditText bioEditText;
    private EditText telefonoEditText;
    private Button saveButton;
    private GridView gridView;
    private Spinner spinner;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_tatuador);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Obtener el usuario actual
        FirebaseUser user = mAuth.getCurrentUser();


        // Crear la consulta para buscar el tatuador por email
        assert user != null;
        Query query = mDatabase.child("tatuadores").orderByChild("email").equalTo(user.getEmail());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // El tatuador con el email especificado existe en la base de datos
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        // Obtener referencias a los campos de nombre y bio
                        nombreEditText = findViewById(R.id.user_name_label);
                        bioEditText = findViewById(R.id.user_bio_label);
                        telefonoEditText = findViewById(R.id.userTelefono);
                        spinner = findViewById(R.id.categoria);

                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(PerfilTatuador.this, R.array.categoriaopciones, R.layout.spinner_item_preview);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter.setDropDownViewResource(R.layout.spinner_item);
                        spinner.setAdapter(adapter);

                        // Obtener los valores actuales de nombre y bio del tatuador
                        String nombre = dataSnapshot.child("nombre").getValue(String.class);
                        String bio = dataSnapshot.child("bio").getValue(String.class);
                        String telefono = dataSnapshot.child("telefono").getValue(String.class);

                        // Establecer los valores actuales en los EditText
                        nombreEditText.setText(nombre);
                        bioEditText.setText(bio);
                        telefonoEditText.setText(telefono);

                        // Manejar el clic del botón "Guardar"
                        saveButton = findViewById(R.id.edit_profile_button);
                        saveButton.setOnClickListener(v -> {
                            String nuevoNombre = nombreEditText.getText().toString().trim();
                            String nuevaBio = bioEditText.getText().toString().trim();
                            String nuevoTelefono = telefonoEditText.getText().toString().trim();

                            // Validar que se haya ingresado un nombre
                            if (TextUtils.isEmpty(nuevoNombre)) {
                                Toast.makeText(PerfilTatuador.this, "Ingrese un nombre", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Guardar los cambios en la base de datos
                            dataSnapshot.getRef().child("nombre").setValue(nuevoNombre);
                            dataSnapshot.getRef().child("bio").setValue(nuevaBio);
                            dataSnapshot.getRef().child("telefono").setValue(nuevoTelefono);

                            Toast.makeText(PerfilTatuador.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    // El tatuador con el email especificado no existe en la base de datos
                    Toast.makeText(PerfilTatuador.this, "No se encontró el tatuador", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error al leer los datos de la base de datos
                Toast.makeText(PerfilTatuador.this, "Error de base de datos", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener para el botón de subir imágenes a la galería (grid)
        Button gridPhotoButton = findViewById(R.id.gridbutton);
        gridPhotoButton.setOnClickListener(v -> uploadToGallery());

        gridView = findViewById(R.id.gallery_gridview);
        setupGalleryGridView();
    }

    private void uploadToGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    private void setupGalleryGridView() {
        // Obtener la referencia del tatuador actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String email = user.getEmail();

        DatabaseReference usuarioRef = mDatabase.child("tatuadores");
        Query query = usuarioRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Obtener la referencia al tatuador encontrado
                    DataSnapshot tatuadorSnapshot = dataSnapshot.getChildren().iterator().next();
                    DatabaseReference tatuadorRef = tatuadorSnapshot.getRef();

                    // Obtener la referencia dentro de "imagenes" a la key "imageUrl"
                    DatabaseReference imagenesRef = tatuadorRef.child("imagenes");

                    imagenesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<String> imageUrls = new ArrayList<>();

                            // Obtener todas las URLs de las imágenes del tatuador
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                String key = childSnapshot.getKey();
                                DatabaseReference imageUrlRef = imagenesRef.child(key).child("imageUrl");
                                imageUrlRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String imageUrl = snapshot.getValue(String.class);
                                        if (imageUrl != null) {
                                            imageUrls.add(imageUrl);
                                        }

                                        // Configurar el adaptador del GridView con las imágenes del tatuador
                                        GalleryAdapter adapter = new GalleryAdapter(PerfilTatuador.this, imageUrls);
                                        gridView.setAdapter(adapter);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Error en la consulta de la base de datos
                                        Toast.makeText(PerfilTatuador.this, "Error en la consulta de la base de datos", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Error en la consulta de la base de datos
                            Toast.makeText(PerfilTatuador.this, "Error en la consulta de la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error en la consulta de la base de datos
                Toast.makeText(PerfilTatuador.this, "Error en la consulta de la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            // Obtener el email del usuario actual
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            String email = user.getEmail();

            // Consultar la referencia del tatuador basado en el email
            DatabaseReference usuarioRef = mDatabase.child("tatuadores");
            Query query = usuarioRef.orderByChild("email").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildren().iterator().hasNext()) {

                        // Obtener la referencia al tatuador encontrado
                        DataSnapshot tatuadorSnapshot = dataSnapshot.getChildren().iterator().next();
                        DatabaseReference tatuadorRef = tatuadorSnapshot.getRef();

                        // Obtener la referencia al directorio de almacenamiento en Firebase Storage
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                        // Generar un nombre de archivo único para la imagen
                        String imageFileName = UUID.randomUUID().toString() + ".jpg";

                        // Crear una referencia al archivo en Firebase Storage
                        StorageReference imageRef = storageRef.child("gallery").child(imageFileName);

                        // Subir la imagen al directorio "gallery" en Firebase Storage
                        imageRef.putFile(selectedImageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    // La imagen se subió exitosamente
                                    Toast.makeText(PerfilTatuador.this, "Imagen subida", Toast.LENGTH_SHORT).show();

                                    // Obtener la URL de descarga de la imagen
                                    imageRef.getDownloadUrl().addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Uri downloadUri = task.getResult();

                                            // Guardar la URL de descarga de la imagen en la base de datos
                                            String imageUrl = downloadUri.toString();
                                            String estilo = spinner.getSelectedItem().toString();

                                            DatabaseReference imagenesRef = tatuadorRef.child("imagenes");
                                            String key = imagenesRef.push().getKey();

                                            Map<String, Object> imageData = new HashMap<>();
                                            imageData.put("imageUrl", imageUrl);
                                            imageData.put("estilo", estilo);

                                            assert key != null;
                                            imagenesRef.child(key).setValue(imageData);

                                            // Actualizar la interfaz de usuario con la nueva imagen en el GridView
                                            setupGalleryGridView();
                                        } else {
                                            // Error al obtener la URL de descarga de la imagen
                                            Toast.makeText(PerfilTatuador.this, "Error al obtener la URL de descarga de la imagen", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    // Error al subir la imagen
                                    Toast.makeText(PerfilTatuador.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // No se encontró ningún tatuador con el email dado
                        Toast.makeText(PerfilTatuador.this, "No se encontró ningún tatuador", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Error en la consulta de la base de datos
                    Toast.makeText(PerfilTatuador.this, "Error en la consulta de la base de datos", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
