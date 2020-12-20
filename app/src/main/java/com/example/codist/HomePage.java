package com.example.codist;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HomePage extends MainActivity {
    private Button signoutButton;
    private static final int REQUEST_ENABLE_BT = 1;
    //    private static final int REQUEST_DISCOVERABILITY = 1 ;
    BluetoothAdapter btAdapter;
    private int counterDevices = 0;
    private int counterDevices2 = 0;

//    private static final String TAG = "HomePage";
//
//    BluetoothAdapter mBluetoothAdapter;
//    Button btnEnableDisable_Discoverable;
//    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
//    public DeviceListAdapter mDeviceListAdapter;
//    ListView lvNewDevices;
//
//    // Create a BroadcastReceiver for ACTION_FOUND
//    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            // When discovery finds a device
//            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
//                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);
//
//                switch(state){
//                    case BluetoothAdapter.STATE_OFF:
//                        Log.d(TAG, "onReceive: STATE OFF");
//                        break;
//                    case BluetoothAdapter.STATE_TURNING_OFF:
//                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
//                        break;
//                    case BluetoothAdapter.STATE_ON:
//                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
//                        break;
//                    case BluetoothAdapter.STATE_TURNING_ON:
//                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
//                        break;
//                }
//            }
//        }
//    };
//
//    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//
//            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
//
//                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
//
//                switch (mode) {
//                    //Device is in Discoverable Mode
//                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
//                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
//                        break;
//                    //Device not in discoverable mode
//                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
//                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
//                        break;
//                    case BluetoothAdapter.SCAN_MODE_NONE:
//                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
//                        break;
//                    case BluetoothAdapter.STATE_CONNECTING:
//                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
//                        break;
//                    case BluetoothAdapter.STATE_CONNECTED:
//                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
//                        break;
//                }
//
//            }
//        }
//    };
//
//
//
//
//    /**
//     * Broadcast Receiver for listing devices that are not yet paired
//     * -Executed by btnDiscover() method.
//     */
//    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//            Log.d(TAG, "onReceive: ACTION FOUND.");
//
//            if (action.equals(BluetoothDevice.ACTION_FOUND)){
//                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
//                mBTDevices.add(device);
//                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
//                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
//                lvNewDevices.setAdapter(mDeviceListAdapter);
//            }
//        }
//    };


    protected void onCreate(
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        signoutButton = (Button) findViewById(R.id.signout);
        boolean isChecked = false;

        // kullanıcı giriş yapmamışsa login sayfasına gönder
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            changeActivity(MainActivity.getInstance().openLoginPage());
            finish();
        }


        // Getting the Bluetooth adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        CheckBluetoothState();

//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(receiver, filter);

//        Toast.makeText(HomePage.this, "Bulunan Cihaz Sayısı : " + counterDevices2, Toast.LENGTH_SHORT).show();
//

        signoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logout(v);
            }
        });

    }

    /* It is called when an activity completes.*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBluetoothState();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void CheckBluetoothState() {
        // Checks for the Bluetooth support and then makes sure it is turned on
        // If it isn't turned on, request to turn it on
        // List paired devices
        if (btAdapter == null) {
            Toast.makeText(HomePage.this, "Cihazınızda Bluetooth desteklenmiyor!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (btAdapter.isEnabled()) {
                Toast.makeText(HomePage.this, "Bluetooth aktifleştirildi.", Toast.LENGTH_SHORT).show();

                // Listing paired devices
                Toast.makeText(HomePage.this, "Eşleştirilmiş Cihazlar : ", Toast.LENGTH_SHORT).show();
                Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
                for (BluetoothDevice device : devices) {
                    counterDevices++;
                    Toast.makeText(HomePage.this, device.getName() + ", " + device, Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(HomePage.this, "Eşleştirilmiş Cihaz Sayısı : " + counterDevices, Toast.LENGTH_SHORT).show();
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        changeActivity(MainActivity.getInstance().openLoginPage());
        finish();
    }

    public void changeActivity(Class className) {
        startActivity(new Intent(this, className));
    }
}
