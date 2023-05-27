package com.example.newinked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class PerfilTatuador extends AppCompatActivity {

    private static final int REQUEST_SELECT_IMAGE = 1;

    private EditText nombreEditText;
    private EditText bioEditText;
    private Button saveButton;
    private GridView gridView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_tatuador);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Obtener el usuario actual
        FirebaseUser user = mAuth.getCurrentUser();

        // Crear la consulta para buscar el tatuador por email
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

                        // Obtener los valores actuales de nombre y bio del tatuador
                        String nombre = dataSnapshot.child("nombre").getValue(String.class);
                        String bio = dataSnapshot.child("bio").getValue(String.class);

                        // Establecer los valores actuales en los EditText
                        nombreEditText.setText(nombre);
                        bioEditText.setText(bio);

                        // Manejar el clic del botón "Guardar"
                        saveButton = findViewById(R.id.edit_profile_button);
                        saveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String nuevoNombre = nombreEditText.getText().toString().trim();
                                String nuevaBio = bioEditText.getText().toString().trim();

                                // Validar que se haya ingresado un nombre
                                if (TextUtils.isEmpty(nuevoNombre)) {
                                    Toast.makeText(PerfilTatuador.this, "Ingrese un nombre", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Guardar los cambios en la base de datos
                                dataSnapshot.getRef().child("nombre").setValue(nuevoNombre);
                                dataSnapshot.getRef().child("bio").setValue(nuevaBio);

                                Toast.makeText(PerfilTatuador.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                            }
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
        gridPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToGallery();
            }
        });

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

                    // Obtener la referencia a las imágenes del tatuador
                    DatabaseReference imagenesRef = tatuadorRef.child("imagenes");

                    // Escuchar los cambios en las imágenes del tatuador
                    imagenesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<String> imageUrls = new ArrayList<>();

                            // Obtener todas las URLs de las imágenes del tatuador
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String imageUrl = snapshot.getValue(String.class);
                                imageUrls.add(imageUrl);
                            }

                            // Configurar el adaptador del GridView con las imágenes del tatuador
                            GalleryAdapter adapter = new GalleryAdapter(PerfilTatuador.this, imageUrls);
                            gridView.setAdapter(adapter);
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
            String email = user.getEmail();

            // Consultar la referencia del tatuador basado en el email
            DatabaseReference usuarioRef = mDatabase.child("tatuadores");
            Query query = usuarioRef.orderByChild("email").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
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
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // La imagen se subió exitosamente
                                        Toast.makeText(PerfilTatuador.this, "Imagen subida", Toast.LENGTH_SHORT).show();

                                        // Obtener la URL de descarga de la imagen
                                        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    Uri downloadUri = task.getResult();

                                                    // Guardar la URL de descarga de la imagen en la base de datos
                                                    String imageUrl = downloadUri.toString();
                                                    DatabaseReference imagenesRef = tatuadorRef.child("imagenes");
                                                    imagenesRef.push().setValue(imageUrl);

                                                    // Actualizar la interfaz de usuario con la nueva imagen en el GridView
                                                    setupGalleryGridView();
                                                } else {
                                                    // Error al obtener la URL de descarga de la imagen
                                                    Toast.makeText(PerfilTatuador.this, "Error al obtener la URL de descarga de la imagen", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error al subir la imagen
                                        Toast.makeText(PerfilTatuador.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                                    }
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
