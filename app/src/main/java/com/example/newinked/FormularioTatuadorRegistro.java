package com.example.newinked;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FormularioTatuadorRegistro extends AppCompatActivity {

    private EditText TatuadorNombre;
    private EditText TatuadorEmail;
    private EditText TatuadorContrasena;
    private EditText ConfirmaTatuadorContrasena;


    private DatabaseReference mDatabase;
    private String idTatuador;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_tatuador_registro);

        TatuadorNombre = findViewById(R.id.nombreCompletoEditText);
        TatuadorEmail = findViewById(R.id.correoElectronicoEditText);
        Button botonRegistro = findViewById(R.id.registrarButtonTatuador);
        ConfirmaTatuadorContrasena = findViewById(R.id.confirmarContrasenaEditText);
        TatuadorContrasena = findViewById(R.id.contrasenaEditText);
        AutoCompleteTextView autoCompleteTextViewPoblacion = findViewById(R.id.autoTexviewPoblacion);

        // Obtenemos la instancia de la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Obtener la lista de poblaciones desde el archivo Json
        ArrayList<String> poblaciones = obtenerPoblacionesDesdeJSON();

        // Se configur el array adapter para el autocompletado de las poblaciones
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, poblaciones);
        autoCompleteTextViewPoblacion.setAdapter(adapter);

        // Configuramos el listener para el autocompletado de las poblaciones
        autoCompleteTextViewPoblacion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita hacer nada antes de cambiar el texto
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Sugerencias de autocompletado mediante el filtro
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se necesita hacer nada después de cambiar el texto
            }
        });

        // Agregar un listener al botón de registro para guardar el objeto Tatuador en la base de datos al pulsar
        botonRegistro.setOnClickListener(v -> {
            // Obtener los valores ingresados por el usuario
            String nombre = TatuadorNombre.getText().toString();
            String email = TatuadorEmail.getText().toString();
            String contrasena = TatuadorContrasena.getText().toString();
            String confirmaContrasena = ConfirmaTatuadorContrasena.getText().toString();
            String ubicacion = autoCompleteTextViewPoblacion.getText().toString();

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
                            autoCompleteTextViewPoblacion.setText("");

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

    private ArrayList<String> obtenerPoblacionesDesdeJSON() {
        ArrayList<String> poblaciones = new ArrayList<>();

        try {
            //Leemos el archivo JSON desde su ubicación
            InputStream inputStream = getResources().openRawResource(R.raw.rows);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            //Mientras haya lineas en el archivo, las leemos y las añadimos al StringBuilder
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            //Cerramos el reader
            reader.close();

            // Procesamos el contenido del JSON
            JSONArray jsonArray = new JSONArray(jsonContent.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                //Para obtener el nombre de la poblacion buscamos por el nombre de la columna "label"
                String poblacion = jsonObject.getString("label");
                poblaciones.add(poblacion);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return poblaciones;
    }

}
