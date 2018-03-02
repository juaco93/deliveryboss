package com.jadevelopment.deliveryboss1.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jadevelopment.deliveryboss1.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback{

    private GoogleMap mMap;
    private Intent intent;

    Button btnGuardarUbicacion;
    String latitud;
    String longitud;
    String[] latLngForm;

    int MY_LOCATION_REQUEST_CODE = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnGuardarUbicacion = (Button) findViewById(R.id.btnGuardarUbicacion);
        btnGuardarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent(MapsActivity.this, ModificarDireccionFragment.class);
                if(latitud!=null &&longitud!=null){
                returnIntent.putExtra("CoordLat", latitud);
                returnIntent.putExtra("CoordLon", longitud);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Mantené presionado en el mapa para marcar tu ubicación",Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        Log.d("latlong", "Mapa listo");

        intent = getIntent();
        Log.d("latlong", "Valor del intent--->" + intent.getStringExtra("latitudLongitud"));

        //SI NO EXISTE EN LA BD PERO VIENE DEL FORMULARIO ANTERIOR
        if (intent.getStringExtra("latitudLongitud") != null && !intent.getStringExtra("latitudLongitud").trim().isEmpty() && !intent.getStringExtra("latitudLongitud").equals("¡Agregá tu ubicación!") ) {
            String latitudLongitudRecibidas = intent.getStringExtra("latitudLongitud");

            latLngForm = latitudLongitudRecibidas.split(",");

            Double latGuar = Double.valueOf(latLngForm[0].trim());
            Double lonGuar = Double.valueOf(latLngForm[1].trim());
            latitud = latLngForm[0];
            longitud = latLngForm[1];

            Log.d("latlong", latitud + ',' + longitud);

            LatLng guardadaPreviamente = new LatLng(latGuar, lonGuar);
            mMap.addMarker(new MarkerOptions()
                    .position(guardadaPreviamente)
                    .title("Mi ubicación marcada")
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guardadaPreviamente, 18));

        } else {
            LatLng larioja = new LatLng(-29.412296, -66.856449);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(larioja, 15));
        }


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Mi ubicación marcada")
                );
                latitud = String.valueOf(latLng.latitude);
                longitud = String.valueOf(latLng.longitude);
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this,"No ha otorgado los permisos para la Ubicacion",Toast.LENGTH_LONG).show();
            }
        }
    }



}
