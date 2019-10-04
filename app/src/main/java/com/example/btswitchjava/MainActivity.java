package com.example.btswitchjava;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    View view;

    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private boolean mScanning;
    private Handler mHandler;

    private BluetoothGatt mGatt; //To provide bluetooth communication

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private int permissionCheck;

    private RecyclerView mRecyclerView;

    MyRecyclerViewAdapter adapter;

    ArrayList<String> devicesNames = new ArrayList<>();

    private String ledState = "OFF";

    private Switch mSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("SHD","onCeate");
        mHandler = new Handler();

        mSwitch = findViewById(R.id.ledSwitch);

        mSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(mSwitch.isChecked()) ledState = "ON"; else ledState = "OFF";

                //                if(ledState == "ON") ledState = "OFF";
//                else if(ledState == "OFF") ledState = "ON";

                final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("30:AE:A4:CC:3E:16");

                //devicesNames.add("Name: " + device.getName());
                //adapter.notifyDataSetChanged();

                final BluetoothGatt mGatt = device.connectGatt(getApplication(), false, gattCallback);
            }
        });

        //recycler View

        devicesNames.add("Test");
//        devicesNames.add("Cow");
//        devicesNames.add("Camel");
//        devicesNames.add("Sheep");
//        devicesNames.add("Goat");

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, devicesNames);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);






        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }



//        mBluetoothAdapter.startLeScan(mLeScanCallback);
      //  scanLeDevice(true);
       // scanLeDevice(false);

//        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("30:AE:A4:CC:3E:16");
//
//        devicesNames.add("Name: " + device.getName());
//        adapter.notifyDataSetChanged();
//
//        final BluetoothGatt mGatt = device.connectGatt(getApplication(), false, gattCallback);

//        final BluetoothGattCharacteristic mCharacterisitc = new BluetoothGattCharacteristic(UUID.fromString("a40d0c2e-73ba-4d8b-8eef-9a0666992e56") , BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE );
//
//       // mCharacterisitc.setValue(123, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//        mCharacterisitc.setValue("ON");
//        mGatt.writeCharacteristic(mCharacterisitc);

    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);



            System.out.println("BLE// onScanResult");
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            Log.i("Device Name: ", "Name - " + result.getDevice().getName());
            Log.i("Device Address: ", "Address - " + result.getDevice().getAddress());
            BluetoothDevice btDevice = result.getDevice();




          // connectToDevice(btDevice);

            //Log.d("SHD","onScanResult");
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d("SHD","onBatchScanResult");


        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    private void scanLeDevice(final boolean enable) {

        final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        if (enable) {

            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(mLeScanCallback);
                    Log.d("SHD","StopScan");
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothLeScanner.startScan(mLeScanCallback);
            Log.d("SHD","StartScan");
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(mLeScanCallback);
            Log.d("SHD","StopScan");
        }
    }

    public void connectToDevice(BluetoothDevice device) {
        System.out.println("BLE// connectToDevice()");
        if (mGatt == null) {

            mGatt = device.connectGatt(MainActivity.this, false, gattCallback); //Connect to a GATT Server
            //scanLeDevice(false);// will stop after first device detection
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            System.out.println("BLE// BluetoothGattCallback");
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();

                    break;
                case BluetoothProfile.STATE_CONNECTING:
                    Log.i("gattCallback", "STATE_CONNECTING");
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }
        }

        @Override
        //New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());


            BluetoothGattCharacteristic characteristic;

            characteristic = services.get(2).getCharacteristics().get(0); //(UUID.fromString("a40d0c2e-73ba-4d8b-8eef-9a0666992e56"));
            characteristic.setValue(ledState);
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

            Log.i("write characteristic: ", characteristic.getStringValue(0));

            gatt.writeCharacteristic(characteristic);

            gatt.disconnect();

//            gatt.readCharacteristic(services.get(2).getCharacteristics().get(0));



        }

        @Override
        //Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.getStringValue(0));
            gatt.disconnect();
        }

    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }
}
