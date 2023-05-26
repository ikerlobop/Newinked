package com.example.newinked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

    private ImageView userPhoto;
    private EditText userNameLabel;
    private EditText userBioLabel;
    private TextView galleryLabel;
    private GridView galleryGridView;

    private Button saveButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    DataSnapshot snapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_tatuador);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        userPhoto = findViewById(R.id.user_profile_image);
        userNameLabel = findViewById(R.id.user_name_label);
        userBioLabel = findViewById(R.id.user_bio_label);
        galleryLabel = findViewById(R.id.gallery_label);
        saveButton = findViewById(R.id.edit_profile_button);

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
                        // Obtener los datos del tatuador
                        String nombre = dataSnapshot.child("nombre").getValue(String.class);
                        String bio = dataSnapshot.child("bio").getValue(String.class);
                        String foto = dataSnapshot.child("foto").getValue(String.class);

                        // Actualizar los campos correspondientes en la interfaz de usuario
                        userNameLabel.setText(nombre);
                        userBioLabel.setText(bio);

                        // Verificar si hay imágenes en la base de datos y cargar la primera imagen en el ImageView
                        if (dataSnapshot.hasChild("imagenes")) {
                            DataSnapshot imagenesSnapshot = dataSnapshot.child("imagenes").getChildren().iterator().next();
                            String imageUrl = imagenesSnapshot.getValue(String.class);
                            Picasso.get().load(imageUrl).into(userPhoto);
                        } else {
                            // No hay imágenes en la base de datos, mostrar una imagen predeterminada
                           // userPhoto.setImageResource(R.drawable.default_user_photo);
                        }

                        // Manejar el clic del botón "Guardar"
                        saveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String newNombre = userNameLabel.getText().toString();
                                String newBio = userBioLabel.getText().toString();

                                // Actualizar el nombre y la bio en la base de datos
                                dataSnapshot.getRef().child("nombre").setValue(newNombre);
                                dataSnapshot.getRef().child("bio").setValue(newBio).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(PerfilTatuador.this, "Biografía actualizada correctamente", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(PerfilTatuador.this, "Error al actualizar la biografía", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
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
                Toast.makeText(PerfilTatuador.this, "Error al obtener los datos del tatuador", Toast.LENGTH_SHORT).show();
            }
        });


// Obtener referencia al GridView de la galería
        galleryGridView = findViewById(R.id.gallery_gridview);

        // Obtener referencia al Spinner de estilos
        Spinner estiloSpinner = findViewById(R.id.tattoo_style_spinner);

        // Crear un ArrayAdapter con los estilos y establecerlo en el Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.estilos_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estiloSpinner.setAdapter(adapter);


        Button gridPhotoButton = findViewById(R.id.gridbutton);
        gridPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_SELECT_IMAGE);
            }
        });

        // Configurar el Listener para el Spinner de estilos
        estiloSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String estiloSeleccionado = parent.getItemAtPosition(position).toString();
                int[] imageResources;

                // Cambiar las imágenes del GridView según el estilo seleccionado
                if (estiloSeleccionado.equals("Lineal")) {
                    imageResources = new int[]{R.drawable.lineal1, R.drawable.lineal2};
                } else if (estiloSeleccionado.equals("Floral")) {
                    imageResources = new int[]{R.drawable.floral1, R.drawable.floral2};
                } else if (estiloSeleccionado.equals("Oriental")) {
                    imageResources = new int[]{R.drawable.oriental1, R.drawable.oriental2, R.drawable.oriental3};
                } else {
                    // Estilo no reconocido no muestra nada
                    imageResources = new int[]{};
                }

                // Crear el adaptador con las nuevas imágenes y establecerlo en el GridView
                ImageAdapter imageAdapter = new ImageAdapter(PerfilTatuador.this, imageResources);
                galleryGridView.setAdapter(imageAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Acciones adicionales cuando no se selecciona ningún estilo
            }
        });

        Button addPhotoButton = findViewById(R.id.add_photo_button);
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_SELECT_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            // Obtener una referencia única para la imagen en Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("imagenes/" + UUID.randomUUID().toString());

            // Subir la imagen a Firebase Storage
            UploadTask uploadTask = imageRef.putFile(selectedImageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Obtener la URL de descarga de la imagen subida
                    Task<Uri> downloadUrlTask = imageRef.getDownloadUrl();
                    downloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            // Obtener la URL de descarga de la imagen
                            String imageUrl = downloadUri.toString();

                            // Obtener el usuario actual
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Realizar la consulta para obtener el tatuador por email
                            Query query = mDatabase.child("tatuadores").orderByChild("email").equalTo(user.getEmail());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        // El tatuador con el email especificado existe en la base de datos
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            // Guardar la URL de la imagen en la base de datos
                                            DatabaseReference ref = dataSnapshot.getRef();
                                            ref.child("imagenes").push().setValue(imageUrl);

                                            // Cargar la imagen utilizando Picasso
                                            ImageView imageView = findViewById(R.id.user_profile_image);
                                            Picasso.get().load(imageUrl).into(imageView);
                                        }
                                    } else {
                                        // El tatuador con el email especificado no existe en la base de datos
                                        Toast.makeText(PerfilTatuador.this, "No se encontró el tatuador", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(PerfilTatuador.this, "Error al obtener los datos del tatuador", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
        }
    }
}






