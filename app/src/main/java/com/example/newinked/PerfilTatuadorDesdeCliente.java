package com.example.newinked;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.List;




public class PerfilTatuadorDesdeCliente extends AppCompatActivity {

    private MapView mapView;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_tatuador_desde_cliente);

        String tatuadorNombre = getIntent().getStringExtra("nombre");
        String tatuadoUbicacion = getIntent().getStringExtra("ubicacion");
        String tatuadorEmail = getIntent().getStringExtra("email");
        String tatuadorTelefono = getIntent().getStringExtra("telefono");

        ImageView imageViewPhoto = findViewById(R.id.imageView7);
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewEmail = findViewById(R.id.textViewEmail);
        TextView textViewLocation = findViewById(R.id.textViewLocation);
        TextView textViewStreet = findViewById(R.id.textViewLocation4);
        Button buttonSendMessage = findViewById(R.id.buttonSendMessage);

        // Establecer el nombre del tatuador en el TextView
        textViewName.setText(tatuadorNombre);
        textViewLocation.setText(tatuadoUbicacion);
        textViewEmail.setText(tatuadorEmail);
        textViewStreet.setText(tatuadorTelefono);

        // Enseñar imagen del tatuador en res
        imageViewPhoto.setImageResource(R.drawable.image);

        // Configurar la configuración de OpenStreetMap
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        // Inicializar el geocoder
        geocoder = new Geocoder(this);
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        //Se muestra la ubicación del tatuador en el mapa
        GeoPoint provinceLocation = obtenerUbicacionProvincia(tatuadoUbicacion);
        mapView.getController().setCenter(provinceLocation);
        mapView.getController().setZoom(15.0);

        buttonSendMessage.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilTatuadorDesdeCliente.this, EnviarMensaje.class);
            intent.putExtra("nombre", tatuadorNombre);
            intent.putExtra("email", tatuadorEmail);
            intent.putExtra("ubicacion", tatuadoUbicacion);
            intent.putExtra("telefono", tatuadorTelefono);
            startActivity(intent);
        });
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
}



