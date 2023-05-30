package com.example.newinked;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import java.io.IOException;
import java.util.List;

public class EnviarMensaje extends AppCompatActivity {

    private DatabaseReference mensajesRef;
    private EditText mensajeEditText;
    private MapView mapView;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enviar_mensaje);

        //Configuramos openstreetmap mediante la clase Configuration
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        // Obtenemos los datos pasados por el intent
        String tatuadorNombre = getIntent().getStringExtra("nombre");
        String tatuadoUbicacion = getIntent().getStringExtra("ubicacion");
        String tatuadorEmail = getIntent().getStringExtra("email");
        String tatuadorTelefono = getIntent().getStringExtra("telefono");

        // Enlazamos vistas
        TextView recipientNameTextView = findViewById(R.id.textViewRecipientName);
        TextView recipientNameTextView2 = findViewById(R.id.textViewRecipientName2);

        // Establecemos datos en las vistas
        recipientNameTextView.setText(tatuadorNombre);
        recipientNameTextView2.setText(tatuadoUbicacion);

        // Inicializamos las rreferencias a la base de datos para guardar mensajes
        mensajesRef = FirebaseDatabase.getInstance().getReference().child("mensajes");

        // Inicializamos el EditText y el botón de enviar mensaje
        mensajeEditText = findViewById(R.id.editTextMessage);
        Button enviarMensajeButton = findViewById(R.id.buttonSend);
        Button llamarPorTelefono = findViewById(R.id.buttonSend2);

        // Establecemos el listener del botón de enviar mensaje
        enviarMensajeButton.setOnClickListener(v -> enviarMensaje());

        // Establecemos el listener del botón de llamar por teléfono
        llamarPorTelefono.setOnClickListener(v -> llamarPorTelefono(tatuadorTelefono));

        // Inicializamos geocoder para obtener la ubicación
        geocoder = new Geocoder(this);

        // Enlazamos lista mapa
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Mostrar la ubicación de los pueblos y ciudades
        GeoPoint provinceLocation = obtenerUbicacionProvincia(tatuadoUbicacion);
        mapView.getController().setCenter(provinceLocation);
        mapView.getController().setZoom(15.0);
    }

    private GeoPoint obtenerUbicacionProvincia(String provincia) {
        try {
            // Obtenemos las coordenadas de los pueblos y ciudades
            List<Address> addresses = geocoder.getFromLocationName(provincia + ", Spain", 1);

            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                double latitud = address.getLatitude();
                double longitud = address.getLongitude();
                return new GeoPoint(latitud, longitud);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Si no se encontró la ubicación, devolvemos oceano atlántico */

        return new GeoPoint(0, 0);
    }

    private void llamarPorTelefono(String telefono) {
        //Intent que llama por telefono
        //añade prefijo 34
        String uri = "tel:" + "34" + telefono.trim();
        startActivity(new android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse(uri)));


    }
    private void enviarMensaje() {
        // Obtenemos  el mensaje del EditText, trim para eliminar espacios en blanco
        String mensaje = mensajeEditText.getText().toString().trim();

        // Verificamos si el mensaje no está vacío
        if (!mensaje.isEmpty()) {
            // Generamos un nuevo ID para el mensaje
            String mensajeId = mensajesRef.push().getKey();

            // Guardamos el mensaje en la base de datos
            assert mensajeId != null;
            mensajesRef.child(mensajeId).setValue(mensaje);

            // abre correo de movil gmail
            String[] TO = {getIntent().getStringExtra("email")};
            String[] CC = {""};
            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setData(android.net.Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_CC, CC);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Mensaje de NewInked");

            Toast.makeText(this, "Mensaje enviado", Toast.LENGTH_SHORT).show();

            mensajeEditText.setText("");
        } else {

            Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
        }

    }
}

