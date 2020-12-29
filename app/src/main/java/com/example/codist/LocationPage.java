package com.example.codist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LocationPage extends FragmentActivity implements OnMapReadyCallback {

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private Button backbutton;
    private Button current_loc;
    private Button map_loc;
    private double current_lat;
    private double current_long;
    private double marker_lat;
    private double marker_long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_page);
        backbutton = (Button) findViewById(R.id.backbuttonlocation);
        current_loc = (Button) findViewById(R.id.current_location);
        map_loc = (Button) findViewById(R.id.map_location);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(MainActivity.getInstance().openRegisterPage());
            }
        });

        current_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchLastLocation();
                Toast.makeText(getApplicationContext(), current_lat + "" + current_long, Toast.LENGTH_LONG).show();
                changeActivity(MainActivity.getInstance().openRegisterPage());
                finish();
            }
        });

        map_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Toast.makeText(getApplicationContext(), marker_lat + " : " + marker_long, Toast.LENGTH_SHORT).show();
              // System.out.println("marker latitude : " + marker_lat);
               changeActivity(MainActivity.getInstance().openRegisterPage());
               finish();
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                   // Toast.makeText(getApplicationContext(), currentLocation.getLatitude()
                    //        + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.google_map);

                    supportMapFragment.getMapAsync(LocationPage.this);
                    current_lat = currentLocation.getLatitude();
                    current_long = currentLocation.getLongitude();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // when map is loaded
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // when clicked on map
                // initialize marker options
                MarkerOptions markerOptions = new MarkerOptions();
                // set position of marker
                markerOptions.position(latLng);
                // set title of marker
                markerOptions.title(latLng.latitude + ":" + latLng.longitude);
                //remove all marker
                googleMap.clear();
                // animating to zoom the marker
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        latLng,10
                ));
                // add marker on map
                googleMap.addMarker(markerOptions);

                marker_lat = latLng.latitude;
                marker_long = latLng.longitude;
                System.out.println("marker longitude " + marker_long);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    public void changeActivity(Class className) {
        Intent intent  = new Intent(this, className);
        intent.putExtra("lat", current_lat);
        intent.putExtra("long", current_long);
        startActivity(intent);
    }
}