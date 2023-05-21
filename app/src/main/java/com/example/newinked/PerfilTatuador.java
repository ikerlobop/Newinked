package com.example.newinked;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class PerfilTatuador extends AppCompatActivity {

    private ImageView profileImageView;
    private Button editProfileButton;
    private TextView userNameLabel;
    private TextView userBioLabel;
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
                // Aquí puedes agregar el código para permitir al usuario añadir fotos a su galería
            }
        });

        // Aquí puedes agregar el código para recuperar y mostrar la información de perfil del usuario
    }
}