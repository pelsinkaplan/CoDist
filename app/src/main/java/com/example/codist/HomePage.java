package com.example.codist;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomePage extends MainActivity implements OnMapReadyCallback {

    private static final String TAG = "bluetooth";
    FirebaseAuth auth;
    FirebaseFirestore store;
    BluetoothAdapter mBluetoothAdapter;
    String body;
    Switch condition;
    String uid;
    Location userLocation;
    Location loc;
    ArrayList<Double> locOfRedDost = new ArrayList<>();
    GoogleMap mGoogleMap;
    SupportMapFragment mapFragment;
    private List<MarkerOptions> marker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        condition = findViewById(R.id.switch1);
        auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();
        uid = auth.getUid();
        marker = new ArrayList<>();
        userLocation = new Location("");
        loc = new Location("");

        if (mBluetoothAdapter == null) {
            Log.d(TAG, "enableDisableBT: Does not have Bluetooth capabilities");
        }
        if (!(mBluetoothAdapter.isEnabled())) {
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);
        }

        checkBTPermissions();

        btnDiscover();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 30 seconds
                btnDiscover();
                handler.postDelayed(this, 15000);
            }
        }, 20000);  //the time is in miliseconds


        store.collection("users").document(uid).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    condition.setChecked(value.getBoolean("korona"));
                    if (value.get("lat") != null && value.get("long") != null) {
                        userLocation.setLatitude(value.getDouble("lat"));
                        userLocation.setLongitude(value.getDouble("long"));
                    }
                }
            }
        });

        // Create a reference to the cities collection
        CollectionReference citiesRef = store.collection("users");

        // Create a query against the collection.
        Query query = citiesRef.whereEqualTo("korona", true);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        loc.setLatitude((Double) document.getDouble("lat"));
                        loc.setLongitude((Double) document.getDouble("long"));
                        Log.d("TAG", "coronalı" + loc.getLatitude() + " " + loc.getLongitude());
                        locOfRedDost.add(loc.getLongitude());
                        locOfRedDost.add(loc.getLatitude());
                    }
                    // Get the SupportMapFragment and request notification when the map is ready to be used.
                    mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(HomePage.this::onMapReady);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


        // kullanıcı giriş yapmamışsa login sayfasına gönder
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            changeActivity(MainActivity.getInstance().openLoginPage());
            finish();
        }


        condition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                store.collection("users").document(uid).update("korona", isChecked);
            }
        });
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: Action Found");
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                double expr = (-69 - (double) rssi) / 20;
                double meters = 2 * Math.pow(10, expr);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                Log.d(TAG, "meters: " + meters + ": " + device.getName());
                if (meters < 0.9) {
                    body = "Sosyal Mesafe İhlali!";
                    notification();
                }
            }
        }
    };

    public void notification() {
        NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify = new Notification.Builder
                (getApplicationContext()).setContentText(body).
                setContentTitle("CoDist").setSmallIcon(R.drawable.logo).build();

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.notify(0, notify);

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            Vibrator vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vi.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vi.vibrate(500);
            }
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnDiscover() {
        //Checken ob Bluetooth an ist

        Log.d(TAG, "btnDiscover: Looking for unpaired Devices");
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover:Cancelling discovery.");

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver, discoverDevicesIntent);
        }

        if (!(mBluetoothAdapter.isDiscovering())) {
            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver, discoverDevicesIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        changeActivity(MainActivity.getInstance().openLoginPage());
        finish();
    }

    public void changeActivity(Class className) {
        startActivity(new Intent(this, className));
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng currentUser = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        marker.add(new MarkerOptions());
        mGoogleMap.addMarker(marker.get(0).position(currentUser)
                .title("currentUser --> " + currentUser.latitude + ":" + currentUser.longitude)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("green_dot", 50, 50)))
                .alpha(0.8f)
                .flat(true));
        for (int i = 0; i < locOfRedDost.size(); i++) {
            if (i % 2 == 1) {
                LatLng user = new LatLng(locOfRedDost.get(i - 1), locOfRedDost.get(i));
                marker.add(new MarkerOptions());
                mGoogleMap.addMarker(marker.get(i / 2 + 1).position(user)
                        .title("user --> " + user.latitude + ":" + user.longitude)
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("red_dot", 50, 50)))
                        .alpha(0.8f)
                        .flat(true));
            }
        }

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                currentUser, 17
        ));
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
}
