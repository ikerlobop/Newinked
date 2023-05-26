package com.example.newinked;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FormularioTatuadorRegistro extends AppCompatActivity {

    private EditText TatuadorNombre;
    private EditText TatuadorEmail;
    private EditText TatuadorContrasena;
    private EditText ConfirmaTatuadorContrasena;
    private Spinner TatuadorUbicacion;
    private Button botonRegistro;
    private DatabaseReference mDatabase;
    private String idTatuador;

    private String[] provincias = {
            "Álava",
            "Albacete",
            "Alicante",
            "Almería",
            "Asturias",
            "Ávila",
            "Badajoz",
            "Barcelona",
            "Burgos",
            "Cáceres",
            "Cádiz",
            "Cantabria",
            "Castellón",
            "Ciudad Real",
            "Córdoba",
            "La Coruña",
            "Cuenca",
            "Gerona",
            "Granada",
            "Guadalajara",
            "Guipúzcoa",
            "Huelva",
            "Huesca",
            "Islas Baleares",
            "Jaén",
            "León",
            "Lérida",
            "Lugo",
            "Madrid",
            "Málaga",
            "Murcia",
            "Navarra",
            "Orense",
            "Palencia",
            "Las Palmas",
            "Pontevedra",
            "La Rioja",
            "Salamanca",
            "Segovia",
            "Sevilla",
            "Soria",
            "Tarragona",
            "Santa Cruz de Tenerife",
            "Teruel",
            "Toledo",
            "Valencia",
            "Valladolid",
            "Vizcaya",
            "Zamora",
            "Zaragoza"
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_tatuador_registro);

        TatuadorNombre = findViewById(R.id.nombreCompletoEditText);
        TatuadorEmail = findViewById(R.id.correoElectronicoEditText);
        botonRegistro = findViewById(R.id.registrarButtonTatuador);
        ConfirmaTatuadorContrasena = findViewById(R.id.confirmarContrasenaEditText);
        TatuadorContrasena = findViewById(R.id.contrasenaEditText);
        TatuadorUbicacion = findViewById(R.id.sexoSpinner);
        // Crear un adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provincias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Establecer el adaptador en el Spinner
        TatuadorUbicacion.setAdapter(adapter);

        // Obtener una instancia de la base de datos de Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Agregar un listener al botón de registro para guardar el objeto Tatuador en la base de datos al pulsar
        botonRegistro.setOnClickListener(v -> {
            // Obtener los valores ingresados por el usuario
            String nombre = TatuadorNombre.getText().toString();
            String email = TatuadorEmail.getText().toString();
            String contrasena = TatuadorContrasena.getText().toString();
            String confirmaContrasena = ConfirmaTatuadorContrasena.getText().toString();
            String ubicacion = TatuadorUbicacion.getSelectedItem().toString();

            // Validar que los campos no estén vacíos
            if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty() || confirmaContrasena.isEmpty() || ubicacion.isEmpty()) {
                Toast.makeText(FormularioTatuadorRegistro.this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            // Que coincidan las contraseñas
            else if (!contrasena.equals(confirmaContrasena)) {
                Toast.makeText(FormularioTatuadorRegistro.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            } else {
                // Verificar si ya existe un usuario con el mismo email
                mDatabase.child("tatuadores").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Si se encuentra un usuario con este email, mostrar un mensaje de error
                        if (snapshot.exists()) {
                            Toast.makeText(FormularioTatuadorRegistro.this, "Ya existe un usuario con este correo electrónico", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            // Crear un objeto Tatuador con los valores ingresados
                            Tatuador tatuador = new Tatuador(nombre, email, contrasena, ubicacion);

                            // Obtener una referencia para el nuevo tatuador en la base de datos
                            DatabaseReference tatuadorRef = mDatabase.child("tatuadores").push();
                            idTatuador = tatuadorRef.getKey();
                            tatuador.setIdtatuador(idTatuador);

                            // Guardar el objeto Tatuador en la base de datos
                            tatuadorRef.setValue(tatuador);


                            // Escribir en Firebase Auth el usuario
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, contrasena);

                            // Mostrar un mensaje de éxito al usuario
                            Toast.makeText(FormularioTatuadorRegistro.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();

                            // Limpiar los EditTexts
                            TatuadorNombre.setText("");
                            TatuadorEmail.setText("");
                            TatuadorContrasena.setText("");
                            ConfirmaTatuadorContrasena.setText("");
                            TatuadorUbicacion.setSelection(0);

                            // Llevar al usuario a la actividad LoginTatuador después de un registro exitoso
                            Intent intent = new Intent(FormularioTatuadorRegistro.this, LoginTatuador.class);
                            startActivity(intent);
                            finish(); // Cerrar la actividad actual para evitar que el usuario vuelva al formulario de registro
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Manejar el error
                        Toast.makeText(FormularioTatuadorRegistro.this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}


