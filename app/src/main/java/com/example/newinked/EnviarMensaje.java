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

import com.google.common.net.InternetDomainName;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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

        // Configurar la configuración de OpenStreetMap
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        // Obtener los datos pasados por el intent
        String tatuadorNombre = getIntent().getStringExtra("nombre");
        String tatuadoUbicacion = getIntent().getStringExtra("ubicacion");
        String tatuadorEmail = getIntent().getStringExtra("email");
        String tatuadorTelefono = getIntent().getStringExtra("telefono");

        // Enlazar el TextView del destinatario
        TextView recipientNameTextView = findViewById(R.id.textViewRecipientName);
        TextView recipientNameTextView2 = findViewById(R.id.textViewRecipientName2);

        // Establecer el nombre del tatuador en el TextView
        recipientNameTextView.setText(tatuadorNombre);
        recipientNameTextView2.setText(tatuadoUbicacion);

        // Inicializar la referencia a la base de datos
        mensajesRef = FirebaseDatabase.getInstance().getReference().child("mensajes");

        // Inicializar las vistas
        mensajeEditText = findViewById(R.id.editTextMessage);
        Button enviarMensajeButton = findViewById(R.id.buttonSend);
        Button llamarPorTelefono = findViewById(R.id.buttonSend2);

        // Establecer el listener del botón de enviar mensaje
        enviarMensajeButton.setOnClickListener(v -> enviarMensaje());

        // Establecer el listener del botón de llamar por teléfono
        llamarPorTelefono.setOnClickListener(v -> llamarPorTelefono(tatuadorTelefono));

        // Inicializar el geocoder
        geocoder = new Geocoder(this);

        // Enlazar el mapa
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Mostrar la ubicación de la provincia en el mapa
        GeoPoint provinceLocation = obtenerUbicacionProvincia(tatuadoUbicacion);
        mapView.getController().setCenter(provinceLocation);
        mapView.getController().setZoom(15.0);
    }

    private GeoPoint obtenerUbicacionProvincia(String provincia) {
        try {
            // Obtener las direcciones que coinciden con el nombre de la provincia
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

        // Si no se encontró la ubicación, puedes devolver un valor predeterminado
        // o mostrar un mensaje de error al usuario.
        return new GeoPoint(0, 0);
    }

    private void llamarPorTelefono(String telefono) {
        // Crear un intent implícito para llamar por teléfono
        //añade prefijo 34
        String uri = "tel:" + "34" + telefono.trim();
        startActivity(new android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse(uri)));


    }
    private void enviarMensaje() {
        // Obtener el mensaje del EditText
        String mensaje = mensajeEditText.getText().toString().trim();

        // Verificar si el mensaje no está vacío
        if (!mensaje.isEmpty()) {
            // Generar un nuevo ID para el mensaje
            String mensajeId = mensajesRef.push().getKey();

            // Guardar el mensaje en la base de datos
            assert mensajeId != null;
            mensajesRef.child(mensajeId).setValue(mensaje);

            //abre correo de movil gmail
            String[] TO = {getIntent().getStringExtra("email")};
            String[] CC = {""};
            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setData(android.net.Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_CC, CC);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Mensaje de NewInked");



            // Mostrar un mensaje de éxito
            Toast.makeText(this, "Mensaje enviado", Toast.LENGTH_SHORT).show();

            // Limpiar el EditText
            mensajeEditText.setText("");
        } else {
            // Mostrar un mensaje de error
            Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
        }

    }
}

