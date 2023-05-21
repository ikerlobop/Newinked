package com.example.newinked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerfilTatuador extends AppCompatActivity {

    private ImageView profileImageView;
    private Button editProfileButton;
    private EditText userNameLabel;
    private EditText userBioLabel;
    private TextView galleryLabel;
    private GridView galleryGridView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_tatuador);

        // Inicializar vistas
        profileImageView = findViewById(R.id.user_profile_image);
        editProfileButton = findViewById(R.id.edit_profile_button);
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
                // Aquí puedes agregar el código para añadir una foto a la galería
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

// Si pulsamos el botón "Editar perfil"
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = userNameLabel.getText().toString();
                String biografia = userBioLabel.getText().toString();

                if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(biografia)) {
                    Toast.makeText(PerfilTatuador.this, "Por favor, ingresa nombre y biografía", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Actualizar solo el nombre y la biografía del tatuador
                DatabaseReference tatuadorRef = FirebaseDatabase.getInstance().getReference("tatuadores").child(firebaseId);
                tatuadorRef.child("nombre").setValue(nombre);
                tatuadorRef.child("biografia").setValue(biografia)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Datos actualizados correctamente
                                Toast.makeText(PerfilTatuador.this, "Perfil actualizado exitosamente", Toast.LENGTH_SHORT).show();

                                // Realizar las actualizaciones necesarias en la interfaz de usuario si es necesario

                                // Mostrar la biografía en el TextView "bio_nuevo"
                                TextView bio_nuevo = findViewById(R.id.bio_nuevo);
                                bio_nuevo.setText(biografia);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error al actualizar los datos
                                Toast.makeText(PerfilTatuador.this, "Error al actualizar el perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }
}
