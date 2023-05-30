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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FormularioUsuarioRegistro extends AppCompatActivity {

    private EditText UsuarioNombre;

    private EditText UsuarioEmail;

    private EditText UsuarioContrasena;

    private EditText ConfirmaUsuarioContrasena;

    private EditText UsuarioFechaNacimiento;

    Button botonRegistroUsuario;

    private DatabaseReference mdatabase;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_usuario_registro);

        // Obtener referencias a los EditTexts y al botón de registro
        UsuarioNombre = findViewById(R.id.nombreCompletoEditText);
        UsuarioEmail = findViewById(R.id.correoElectronicoEditText);
        UsuarioContrasena = findViewById(R.id.contrasenaEditText);
        ConfirmaUsuarioContrasena = findViewById(R.id.confirmarContrasenaEditText);
        UsuarioFechaNacimiento = findViewById(R.id.fechaNacimientoEditText);
        AutoCompleteTextView UsuarioUbicacion = findViewById(R.id.autoTexviewPoblacionusuario);
        botonRegistroUsuario = findViewById(R.id.registrarButtonUsuario);


        // Obtener una instancia de la base de datos de Firebase
        mdatabase = FirebaseDatabase.getInstance().getReference();

        // Obtener la lista de poblaciones desde tu archivo JSON o cualquier otra fuente de datos
        ArrayList<String> poblaciones = obtenerPoblacionesDesdeJSON();

        // Configurar el adaptador con la lista de poblaciones
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, poblaciones);
        UsuarioUbicacion.setAdapter(adapter);

        // Configurar el autocompletado al escribir en el AutoCompleteTextView
        UsuarioUbicacion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita hacer nada antes de cambiar el texto
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrar las sugerencias según el texto ingresado
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se necesita hacer nada después de cambiar el texto
            }
        });

        // Agregar un listener al botón de registro para guardar el objeto Usuario en la base de datos al pulsar
        botonRegistroUsuario.setOnClickListener(v -> {
            // Obtener los valores ingresados por el usuario
            String nombre = UsuarioNombre.getText().toString();
            String email = UsuarioEmail.getText().toString();
            String contrasena = UsuarioContrasena.getText().toString();
            String confirmaContrasena = ConfirmaUsuarioContrasena.getText().toString();
            String ubicacion = UsuarioUbicacion.getText().toString();
            String fechaNacimiento = UsuarioFechaNacimiento.getText().toString();

            // Validar que los campos no estén vacíos
            if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty() || fechaNacimiento.isEmpty()|| confirmaContrasena.isEmpty() || ubicacion.isEmpty()) {
                Toast.makeText(FormularioUsuarioRegistro.this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            //que coincidan las contraseñas
            else if  (!contrasena.equals(confirmaContrasena)) {
                Toast.makeText(FormularioUsuarioRegistro.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            } else if (!validarFechaNacimiento(fechaNacimiento)) {
                Toast.makeText(FormularioUsuarioRegistro.this, "La fecha de nacimiento no es válida", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (esMenorDeEdad(fechaNacimiento)){
                Toast.makeText(FormularioUsuarioRegistro.this, "Debes ser mayor de edad para registrarte", Toast.LENGTH_SHORT).show();
                return;
            }
             else {

                // Verificar si ya existe un usuario con el mismo email
                mdatabase.child("usuarios").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Si se encuentra un usuario con este email, mostrar un mensaje de error
                        if (snapshot.exists()) {
                            Toast.makeText(FormularioUsuarioRegistro.this, "Ya existe un usuario con este correo electrónico", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            // Crear un objeto Usuario con los valores ingresados
                            Usuario usuario = new Usuario(nombre, email, contrasena, ubicacion, fechaNacimiento);

                            // Guardar el objeto Usuario en la base de datos
                            mdatabase.child("usuarios").push().setValue(usuario);

                            // Escribimos en firebase auth el usuario
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, contrasena);

                            // Mostrar un mensaje de éxito al usuario
                            Toast.makeText(FormularioUsuarioRegistro.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();

                            // Limpiar los EditTexts
                            UsuarioNombre.setText("");
                            UsuarioEmail.setText("");
                            UsuarioContrasena.setText("");
                            UsuarioFechaNacimiento.setText("");
                            ConfirmaUsuarioContrasena.setText("");
                            UsuarioUbicacion.setSelection(0);

                            // Llevar al usuario a la actividad LoginTatuador después de un registro exitoso
                            Intent intent = new Intent(FormularioUsuarioRegistro.this, LoginUsuario.class);
                            startActivity(intent);
                            finish(); // Cerrar la actividad actual para evitar que el usuario vuelva al formulario de registro
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Manejar el error
                    }
                });
            }
        });
    }
    private boolean validarFechaNacimiento(String fechaNacimiento) {
        // Validar que la fecha de nacimiento tenga el formato correcto
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        simpleDateFormat.setLenient(false);
        try {
            Date fechaNacimientoDate = simpleDateFormat.parse(fechaNacimiento);

            // Obtener el año actual
            Calendar calendar = Calendar.getInstance();
            int yearActual = calendar.get(Calendar.YEAR);

            // Obtener el año de la fecha de nacimiento
            calendar.setTime(fechaNacimientoDate);
            int yearNacimiento = calendar.get(Calendar.YEAR);

            // Validar el rango de años (establecer un rango adecuado)
            int anioMinimo = 1930; // Año mínimo aceptado
            int anioMaximo = yearActual - 18; // Año máximo (se resta 18 años del año actual)

            if (yearNacimiento < anioMinimo || yearNacimiento > anioMaximo) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
    private boolean esMenorDeEdad(String fechaNacimiento) {
        // Validar que el usuario sea mayor de edad
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        simpleDateFormat.setLenient(false);
        try {
            Date fechaNacimientoDate = simpleDateFormat.parse(fechaNacimiento);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fechaNacimientoDate);
            calendar.add(Calendar.YEAR, 18);
            Date fechaMayorEdad = calendar.getTime();
            return fechaMayorEdad.after(new Date());
        } catch (ParseException e) {
            return false;
        }
    }
    private ArrayList<String> obtenerPoblacionesDesdeJSON() {
        ArrayList<String> poblaciones = new ArrayList<>();

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.rows);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();

            // Procesar el contenido JSON
            JSONArray jsonArray = new JSONArray(jsonContent.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String poblacion = jsonObject.getString("label"); // Obtener el valor del campo "label"
                poblaciones.add(poblacion);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return poblaciones;
    }
}