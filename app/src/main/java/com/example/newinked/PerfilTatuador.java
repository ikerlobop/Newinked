package com.example.newinked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

public class PerfilTatuador extends AppCompatActivity {

    private static final int REQUEST_SELECT_IMAGE = 1;
    private ImageView profileImageView;
    private EditText userNameLabel;
    private EditText userBioLabel;
    private TextView galleryLabel;
    private GridView galleryGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_tatuador);

        String tatuadorId = getIntent().getStringExtra("tatuadorId");
        List<String> imageUrls = new ArrayList<>();
        // Inicializar vistas
        profileImageView = findViewById(R.id.user_profile_image);
        userNameLabel = findViewById(R.id.user_name_label);
        userBioLabel = findViewById(R.id.user_bio_label);
        galleryLabel = findViewById(R.id.gallery_label);
        galleryGridView = findViewById(R.id.gallery_gridview);
        Button addPhotoButton = findViewById(R.id.add_photo_button);
        Spinner tattooStyleSpinner = findViewById(R.id.tattoo_style_spinner);

        // Configurar adaptador para Spinner de estilos de tatuaje
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.tattoo_styles, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tattooStyleSpinner.setAdapter(spinnerAdapter);

        // Obtén la referencia del usuario actual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // El usuario no está autenticado, muestra un mensaje de error o inicia sesión.
            Toast.makeText(PerfilTatuador.this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String firebaseId = currentUser.getUid(); // Obtén la ID del usuario autenticado

        // Obtén la referencia de la carpeta de imágenes de Firebase Storage
        StorageReference imagesRef = FirebaseStorage.getInstance().getReference("imagenes").child(firebaseId);


        imagesRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference imageRef : listResult.getItems()) {
                    // Obtén la URL de descarga de cada imagen y agrégala a la lista de URL de imágenes
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            imageUrls.add(downloadUri.toString()); // Agregar la URL a la lista de imageUrls

                            // Crea un adaptador y asigna la lista de URL de imágenes al GridView
                            ImageAdapter imageAdapter = new ImageAdapter(PerfilTatuador.this, imageUrls);
                            galleryGridView.setAdapter(imageAdapter);

                            // Cargar imágenes en ImageView usando Picasso
                            Picasso.get().load(downloadUri.toString()).into(profileImageView);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Maneja cualquier error al obtener la URL de descarga de la imagen
                            Toast.makeText(PerfilTatuador.this, "Error al obtener la URL de descarga de la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Maneja cualquier error al listar los archivos en la carpeta de imágenes
                Toast.makeText(PerfilTatuador.this, "Error al listar los archivos de imágenes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Escuchar evento de clic en botón para añadir foto
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un intent para seleccionar múltiples imágenes de la galería
                // Crear un intent para seleccionar múltiples imágenes de la galería
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_SELECT_IMAGE);
            }
        });

        // Aquí puedes agregar el código para recuperar y mostrar la información de perfil de tatuadores
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            String tatuadorId = getIntent().getStringExtra("tatuadorId");
            if (data.getClipData() != null) {
                // Se seleccionaron múltiples imágenes
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    // Aquí puedes guardar cada imagen en la base de datos
                    guardarImagenEnBaseDeDatos(imageUri, tatuadorId);
                }
            } else if (data.getData() != null) {
                // Se seleccionó una sola imagen
                Uri imageUri = data.getData();
                // Aquí puedes guardar la imagen en la base de datos
                guardarImagenEnBaseDeDatos(imageUri, tatuadorId);
            }
        }
    }

    private void guardarImagenEnBaseDeDatos(Uri imageUri, String tatuadorId) {
        DatabaseReference tatuadorRef = FirebaseDatabase.getInstance().getReference("tatuadores").child(tatuadorId);

        // Obtén la referencia de la carpeta de imágenes de Firebase Storage guardando de manera incremental
        String imageName = "imagen" + System.currentTimeMillis(); // Genera un nombre único para la imagen
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("imagenes").child(tatuadorId).child(imageName);

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        // Guarda la URL de descarga de la imagen en la base de datos en la lista de imágenes
                        tatuadorRef.child("imagenes").push().setValue(downloadUri.toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(PerfilTatuador.this, "Imagen guardada exitosamente", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PerfilTatuador.this, "Error al guardar la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PerfilTatuador.this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}



