package com.example.codist;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
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

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HomePage extends MainActivity {
    private static final String TAG = "bluetooth";
    BluetoothAdapter mBluetoothAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



        // kullanıcı giriş yapmamışsa login sayfasına gönder
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            changeActivity(MainActivity.getInstance().openLoginPage());
            finish();
        }


    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    //Bluetooth Broadcoast receiver für den Button
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,BluetoothAdapter.ERROR);

                switch(mode)
                {
                    //Wenn das Device in Discoverable Mode ist
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG,"mBroadcastReceiver2: Discoverability Enabled.");
                        break;

                    //Wenn das Device nicht im discoverable mode ist:
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG,"mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG,"mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG,"mBroadcastReceiver2: Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG,"mBroadcastReceiver2:Connected.");
                        break;

                }


            }
        }
    };

    //Broadcast Receiver 3 für discover devices list

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG,"onReceive: Action Found");
            if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                Toast.makeText(getApplicationContext(),device.getName(),Toast.LENGTH_SHORT).show();
            }
        }
    };


    public void btnDiscover(View view) {
        //Checken ob Bluetooth an ist
        if (mBluetoothAdapter == null) {
            Log.d(TAG,"enableDisableBT: Does not have Bluetooth capabilities");
        }
        if(!(mBluetoothAdapter.isEnabled())) {
            Log.d(TAG,"enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        Log.d(TAG,"btnDiscover: Looking for unpaired Devices");
        if(mBluetoothAdapter.isDiscovering())
        {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG,"btnDiscover:Cancelling discovery.");
            //Checkt ob die Berechtigungen im Manifest für BT vorliegen
            checkBTPermissions();
            //start discovery again
            mBluetoothAdapter.startDiscovery();
            //scann(mBluetoothAdapter,30);
            //System.out.println("scann1 was aufgerufen");
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3,discoverDevicesIntent);
        }

        if(!(mBluetoothAdapter.isDiscovering())) {
            //another check
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            //scann(mBluetoothAdapter,30);
            //System.out.println("scann2 wurde aufgerufen");
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3,discoverDevicesIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
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
}
