package com.example.newinked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

        // Escuchar evento de clic en botón para añadir foto
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Crear un intent para seleccionar imágenes de la galería
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_SELECT_IMAGE);
            }
        });

        // Obtén la referencia del usuario actual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // El usuario no está autenticado, muestra un mensaje de error o inicia sesión.
            Toast.makeText(PerfilTatuador.this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String firebaseId = currentUser.getUid(); // Obtén la ID del usuario autenticado

        // Aquí puedes agregar el código para recuperar y mostrar la información de perfil de tatuadores
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            // Obtén el ID del tatuador
            //obtenemos id del tatuador de la variable tipo pasada por el intent
              String tatuadorId = getIntent().getStringExtra("tatuadorId");

            // Aquí puedes guardar la imagen en la base de datos
            guardarImagenEnBaseDeDatos(selectedImageUri, tatuadorId);
        }
    }

    private void guardarImagenEnBaseDeDatos(Uri imageUri, String tatuadorId) {
        DatabaseReference tatuadorRef = FirebaseDatabase.getInstance().getReference("tatuadores").child(tatuadorId);

        // Obtén la referencia de la carpeta de imágenes de Firebase Storage
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("imagenes").child(tatuadorId);

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                tatuadorRef.child("imagenes").setValue(downloadUri.toString())
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PerfilTatuador.this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}





