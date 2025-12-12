package com.example.bancotrack;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Pag_Mapa extends AppCompatActivity {

    private MapView mapView;
    // Variables de Localización
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastProcessedLocation = null;
    private Marker miMarcador;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    // Variables de UI
    private Button btnCalcular;
    private TextView tvDirecciones;
    private Button btnVolver;

    // Variables de Ruta
    private GeoPoint destinoSeleccionado = null;
    private String nombreDestino = "";

    // Constante para el manejo de permisos
    private static final int PERMISSION_REQUEST_CODE = 99;

    // Variables de Cajeros
    private List<Cajero> listaCajeros;
    private String API_KEY = "8242b99f48d8cd573f6393829e6ff5c8"; // Tu clave de OpenWeatherMap
    private String tempClima = ""; // Variable para guardar la temperatura

    // Clase interna para Cajeros
    private static class Cajero {
        String nombre;
        GeoPoint punto;

        public Cajero(String nombre, double lat, double lon) {
            this.nombre = nombre;
            this.punto = new GeoPoint(lat, lon);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Configuración de osmdroid
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_pag_mapa);

        // 2. Configuración del Mapa
        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);

        btnCalcular = findViewById(R.id.btnCalcular);
        tvDirecciones = findViewById(R.id.tvDirecciones);
        btnVolver = findViewById(R.id.btnVolver);

        // Listener del botón 'Calcula la Ruta'
        btnCalcular.setOnClickListener(v -> {
            calcularYRuta();
        });

        // Listener del botón 'Volver' (NUEVA FUNCIÓN)
        btnVolver.setOnClickListener(v -> {
            // finish() cierra la actividad actual y regresa a la actividad que la llamó (MainActivity)
            finish();
        });

        // 3. Inicialización de Servicios de Localización
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Configuración de la frecuencia de actualización de ubicación
        locationRequest = new LocationRequest.Builder(10000)
                .setMinUpdateIntervalMillis(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .build();

        configurarLocationCallback();

        // 4. Configuración del Marcador de Usuario ("Tú")
        miMarcador = new Marker(mapView);
        miMarcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        miMarcador.setTitle("Tú");
        mapView.getOverlays().add(miMarcador);

        // 5. Carga de Cajeros y Marcadores
        cargarCajeros();
        añadirMarcadoresCajeros();

        // 6. Pedir permisos
        pedirPermisos();
    }

    // **********************************************
    // MÉTODOS DE LOCALIZACIÓN
    // **********************************************

    private void configurarLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    lastProcessedLocation = location;
                    actualizarMarcadorUbicacion(location);
                }
            }
        };
    }

    private void iniciarActualizacionesDeUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void pedirPermisos() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            iniciarActualizacionesDeUbicacion();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarActualizacionesDeUbicacion();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado. No se mostrará tu posición en el mapa.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void actualizarMarcadorUbicacion(Location location) {
        GeoPoint puntoActual = new GeoPoint(location.getLatitude(), location.getLongitude());
        miMarcador.setPosition(puntoActual);

        // Zoom solo si es necesario (cuando la aplicación se centra por primera vez)
        if (mapView.getZoomLevel() < 12.8) {
            mapView.getController().setZoom(15.0);
        }

        mapView.getController().setCenter(puntoActual);
        mapView.invalidate();
    }

    // **********************************************
    // MÉTODOS DE CAJEROS
    // **********************************************

    private void cargarCajeros() {
        listaCajeros = new ArrayList<>();
        // Coordenadas cercanas a La Dorada, Caldas (las que definiste)
        listaCajeros.add(new Cajero("Cajero Centro Comercial", 5.454092917136365, -74.66470066546033));
        listaCajeros.add(new Cajero("Cajero Sede Principal", 5.453113724567923, -74.66310965052007));
        listaCajeros.add(new Cajero("Cajero Puerto Salgar", 5.46486969048301, -74.65566976443176));
        listaCajeros.add(new Cajero("Cajero Doradal", 5.898389953803509, -74.73692626933399));
        listaCajeros.add(new Cajero("Cajero Medellin", 6.2420314508424655, -75.58677371065433));
        listaCajeros.add(new Cajero("Cajero Bogota", 4.727618048452373, -74.04680638761843));
        listaCajeros.add(new Cajero("Cajero Guaduas", 5.070373245439438, -74.59960995002675));
        listaCajeros.add(new Cajero("CCorresponsal Bancario Bancolombia 3", 5.447884936915848, -74.67063841779787));
    }

}