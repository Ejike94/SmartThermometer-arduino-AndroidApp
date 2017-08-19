package com.example.ejike.smarttermometer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import java.util.logging.Handler;

/**
 * Created by ejike on 17/11/2016.
 */


public class connectBluetooth extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter blueAdapter;
    private boolean mScanning;
    private Handler mHandler;


    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;


    public void connectToDevice(){

        blueAdapter = BluetoothAdapter.getDefaultAdapter();
         //Create a new BluetoothAdapter
        blueAdapter.getDefaultAdapter();
        if (blueAdapter == null || !blueAdapter.isEnabled()){
            Intent enableBt = new Intent((BluetoothAdapter.ACTION_REQUEST_ENABLE));
            //Connect to Bluetooth
            startActivityForResult(enableBt, REQUEST_ENABLE_BT);
        }
    }
}
