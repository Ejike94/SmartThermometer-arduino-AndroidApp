package com.example.ejike.smarttermometer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.jar.Manifest;

import static com.example.ejike.smarttermometer.EjDigest.*;

public class activityTermometer extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter blueAdapter;
    private BluetoothGatt mGatt;
    private BluetoothGattService mGattService;
    private BluetoothLeScanner mLEscanner;
    private ScanSettings settings;
    private static final long SCAN_PERIOD = 4000;
    private Handler mHandler;
    boolean f = false;
    private int keyN = 3;
    private final UUID UUID_Service = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    private final UUID UUID_ServiceCharateristics = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

    List<String> newDevaName = new ArrayList<>();
    List<String> newDevaAddress = new ArrayList<>();
    List<BluetoothDevice> scannedDevices;
    List<BluetoothGattService> mGattservices; //List of Services Found
    int devicesF;
    boolean done;//this variable checks if services have already been searched

    private boolean scan;


    //  private LeDeviceListAdapter mLeDeviceListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termometer);
        //Creation of the Instance that will allow the extras message to be recieved
        Bundle extras = getIntent().getExtras();
        //Setting Class AES


        //ExtraMessage Received
        String pass_string = extras.getString(LoginScreen.EXTRA_PASS); //Password

        //The password received is hashed through this method | Encryption Class
        String pass_hash = EncryptionMessage.SHAhash(pass_string);

        if (!pass_hash.equals("3650f80b474931efcce44a84b30b368b6bd33a5ebb50f7fa5f615f8d061550b6")) {
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
        }
        //Creation of the handler that will check the time of the bluetooth device
        mHandler = new Handler();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        String username_string = extras.getString(LoginScreen.EXTRA_USER);
        TextView textView = (TextView) findViewById(R.id.welcomeId);

        //Insert the username next to Welcome
        int REQUEST_CODE_ASK_PERMISSIONS = 1;

        textView.setText("Welcome " + username_string);


            //Connect to Bluetooth

        //Initialize Bluetooth Manager
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        //Create a new BluetoothAdapter
        blueAdapter = bluetoothManager.getAdapter();

        //Connect to Bluetooth if Turned off
        if (blueAdapter == null || !blueAdapter.isEnabled()) {
            Intent enableBt = new Intent((BluetoothAdapter.ACTION_REQUEST_ENABLE));
            startActivityForResult(enableBt, REQUEST_ENABLE_BT);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activitytermometer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_connect_device) {
            connectDevice();
            return true;
        }

        if (id == R.id.action_showscan_LEdevices) {
            showDevices();
        }

        return super.onOptionsItemSelected(item);
    }

    //Bluetooth Implementation

    //Scall Callback... it receives the devices that have been scanned


    //Connect To Bluetooth Device
    public void connectDevice() {


//Connect to device when bluetooth turned on
        if (blueAdapter.isEnabled()) {
            //Find The Device
            mLEscanner = blueAdapter.getBluetoothLeScanner();
            Toast.makeText(this, "Scan Begins", Toast.LENGTH_SHORT).show();
                 mLEscanner = blueAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
            scanLeDevice(true);
        }
    }

    //Show Scan Device
    public void showDevices() {
        //Once decided to see the devices found, it stops the scan
        scanLeDevice(false);
        String[] Dname = new String[newDevaName.size()];
        int i;
        for (i = 0; i < newDevaName.size(); i++) {
            Dname[i] = newDevaName.get(i);
        }
        i=0;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Devices Found").setItems(Dname, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                BluetoothDevice device = blueAdapter.getRemoteDevice(newDevaAddress.get(which));
                establishConnection(device);
            }

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void updateValue(View view) {
         boolean flag = false;
         String password ="1045";
         password = keyDigestIn(password);//Calling method EjKeyDigest | Protection Mechanism against sniffint and Man in The Middle
        int half = password.length()/2;
        String part1="";
        String part2="";
          for(int i = 0; i<password.length(); i++)
        {
            if(i>=half)
                part2+=password.charAt(i);
            else
                part1+=password.charAt(i);
        }




        final byte[] data = part1.getBytes(); //I get the bytes of data that will be sent. ASCII
        final byte[] data2 = part2.getBytes(); //I get the bytes of data that will be sent. ASCII
        //  mGatt.getServices();
        if (mGatt == null || mGattservices == null) {//Controlling if the connection was successful and if the mGatt Service has services stored
            //Log.w(TAG, "BluetoothGatt not initialized");
            Toast.makeText(this, "Error, connection haven't been established",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Connection Success",Toast.LENGTH_LONG).show();

                mGattService= mGatt.getService(UUID_Service);


                BluetoothGattCharacteristic characteristic =
                        mGattService.getCharacteristic(UUID_ServiceCharateristics);

                if (characteristic == null) {
                    return;
                }
                else {

                  /*  final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for(byte byteChar : data)
                        stringBuilder.append(String.format("%02X ", byteChar));*/

                    characteristic.setValue(data);
                    characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                    mGatt.writeCharacteristic(characteristic);

                    try{
                        Thread.sleep(200);
                    }
                    catch (InterruptedException e) {
                        // Restore the interrupted status
                        Thread.currentThread().interrupt();
                    }

                    //Second part of the data
                    characteristic.setValue(data2);
                    characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                    mGatt.writeCharacteristic(characteristic);



                    mGatt.setCharacteristicNotification(characteristic, true); // Available to recieve data


                   // Enabled remote notifications
               /*     BluetoothGattDescriptor desc = characteristic.getDescriptor(CONFIG_DESCRIPTOR);
                    desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mgatt.writeDescriptor(desc);*/

                    return;
                }
            }
        }

    //This method handles updates the value of the temperature
    private void showUpdate(final BluetoothGattCharacteristic characteristic) {

        final byte[] data = characteristic.getValue();
        mGatt.setCharacteristicNotification(characteristic, false); // Available to recieve data
        String a="";
        /*final StringBuilder stringBuilder = new StringBuilder(data.length);*/
        //This section extracts the value recieved and converts the Ascii value to an integer number
        if (data != null && data.length > 0) {
             for (byte byteChar : data) {
                int value = Character.getNumericValue(byteChar);//Converts from ASCII to Integer
                  a = a+Integer.toString(value);

                 //stringBuilder.append(String.format("%02X ", value));
            }
        }

        a = keyDigestOut(a);//Decryption of the value recieved
        final TextView textView = (TextView) findViewById(R.id.idTermoNumber);//Gets the ID of the textview that containes the value of the temperature
        final String b = a;
        textView.post(new Runnable() {

            public void run() {
                textView.setText(b);
            }
        });//Edits the value of the temperature
    }

        //Handler for Scanning Devices with a specific timing
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Scan Ended", Toast.LENGTH_SHORT).show();
                    scan = false;
                    mLEscanner.startScan(mScanCallback);
                }
            }, SCAN_PERIOD);

     /*       scan = true;
            mLEscanner.startScan(mScanCallback);*/
        } else {
            scan = false;
            mLEscanner.stopScan(mScanCallback);
        }
    }

    //Connect to the device that has been clicked
    public void establishConnection(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
        }
    }


    //Bluetooth Override Methods
    @Override
    protected void onDestroy() {
        if (mGatt == null) {
            return;
        }
        /*Ressetting the arduino when app is shutdown */
        String password ="nope";
        final byte[] data = password.getBytes(); //I get the bytes of data that will be sent. ASCII
        mGattService= mGatt.getService(UUID_Service);

        BluetoothGattCharacteristic characteristic =
                mGattService.getCharacteristic(UUID_ServiceCharateristics);
        /*Sending the data that would reset the arduino software */
        characteristic.setValue(data);
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mGatt.writeCharacteristic(characteristic);


        /*Closing gatt communication*/
        mGatt.close();
        mGatt = null;
        super.onDestroy();
    }
    
    // CallBacks
    //This abstract class recieves the devices that have been scanned
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice btDevice = result.getDevice();
            devicesF++;
            newDevaName.add(btDevice.getName());
            newDevaAddress.add(btDevice.getAddress());
            //Implement Array Adapter
            scanLeDevice(false);

        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };


    //This Abstract class handles the connection with the recieved device
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            // Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    if(!done)
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                mGattservices = gatt.getServices();
                Log.i("onServicesDiscovered", mGattservices.toString());
                //gatt.readCharacteristic(services.get(1).getCharacteristics().get(0));
                done = true;
                   }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                showUpdate(characteristic);
            }
        }


            @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            showUpdate(characteristic);
        }
    };
}
