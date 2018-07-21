package android.kaviles.bletutorial;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_ENABLE_BT = 1;
    public static final int BTLE_SERVICES = 2;
    public static final int UPLOAD_DELAY = 1000;

    private HashMap<String, BTLE_Device> mBTDevicesHashMap;
    private ArrayList<BTLE_Device> mBTDevicesArrayList;
    private ArrayList<String> addressList;
    private ListAdapter_BTLE_Devices adapter;
    private ListView listView;
    private Button btn_Scan;
    private ImageButton btn_Reload;
    private ImageButton btn_Settings;
    private Button btn_Test;

    private BroadcastReceiver_BTState mBTStateUpdateReceiver;
    private Scanner_BTLE mBTLeScanner;
    private boolean isLooping;
    private int loop_count;
    private FetchRequest request;
    private boolean upLoading;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loop_count = 0;
        isLooping = false;
        upLoading = false;
        addressList = new ArrayList<String>();
        addressList.add("01:17:C5:97:2C:F8");
        addressList.add("01:17:C5:5B:80:8D");
        addressList.add("01:17:C5:5C:4E:AD");
        addressList.add("01:17:C5:5B:B8:7E");
        addressList.add("01:17:C5:5C:DE:49");
        addressList.add("01:17:C5:52:A1:DB");

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utils.toast(getApplicationContext(), "BLE not supported");
            finish();
        }

        mBTStateUpdateReceiver = new BroadcastReceiver_BTState(getApplicationContext());
        mBTLeScanner = new Scanner_BTLE(this, 1800000, -100);

        mBTDevicesHashMap = new HashMap<>();
        mBTDevicesArrayList = new ArrayList<>();

        adapter = new ListAdapter_BTLE_Devices(this, R.layout.btle_device_list_item, mBTDevicesArrayList);

        listView = new ListView(this);
        listView.setAdapter(adapter);
        //listView.setOnItemClickListener(this);
        request = new FetchRequest(MainActivity.this);
        btn_Scan = (Button) findViewById(R.id.btn_scan);
        btn_Reload = (ImageButton) findViewById(R.id.btn_reload);
        btn_Test = (Button) findViewById(R.id.btn_test_upload);
        btn_Settings = (ImageButton) findViewById(R.id.btn_setting);
        ((ScrollView) findViewById(R.id.scrollView)).addView(listView);
        btn_Scan.setOnClickListener(this);
        btn_Reload.setOnClickListener(this);
        btn_Settings.setOnClickListener(this);
        btn_Test.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(mBTStateUpdateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onResume() {
        super.onResume();

//        registerReceiver(mBTStateUpdateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();

//        unregisterReceiver(mBTStateUpdateReceiver);
        stopScan();
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mBTStateUpdateReceiver);
        stopScan();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
//                Utils.toast(getApplicationContext(), "Thank you for turning on Bluetooth");
            }
            else if (resultCode == RESULT_CANCELED) {
                Utils.toast(getApplicationContext(), "Please turn on Bluetooth");
            }
        }
        else if (requestCode == BTLE_SERVICES) {
            // Do something
        }
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Context context = view.getContext();
//
////        Utils.toast(context, "List Item clicked");
//
//        // do something with the text views and start the next activity.
//
//        stopScan();
//
//        String name = mBTDevicesArrayList.get(position).getName();
//        String address = mBTDevicesArrayList.get(position).getAddress();
//
//        Intent intent = new Intent(this, Activity_BTLE_Services.class);
//        intent.putExtra(Activity_BTLE_Services.EXTRA_NAME, name);
//        intent.putExtra(Activity_BTLE_Services.EXTRA_ADDRESS, address);
//        startActivityForResult(intent, BTLE_SERVICES);
//    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_scan:
                Utils.toast(getApplicationContext(), "Scan Button Pressed");

                if (!mBTLeScanner.isScanning()) {
                    startScan();
                }
                else {
                    stopScan();
                }

                break;
            case R.id.btn_reload:
                if(isLooping){
                    upLoading = false;
                    stopLoop();
                    btn_Reload.setImageResource(R.drawable.ic_upload_multiple_black_36dp);
                }else{
                    startLoop();
                    btn_Reload.setImageResource(R.drawable.ic_stop_black_36dp);
                }
                break;
            case R.id.btn_setting:
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.btn_test_upload:
                //request.test_String("Hello");
                loop_count = 0;
                break;
            default:
                break;
        }

    }

    public void addDevice(BluetoothDevice device, int rssi) {

        String address = device.getAddress();
        switch (address) {
            case "01:17:C5:97:2C:F8":
            case "01:17:C5:5B:80:8D":
            case "01:17:C5:5C:4E:AD":
            case "01:17:C5:5B:B8:7E":
            case "01:17:C5:5C:DE:49":
            case "01:17:C5:52:A1:DB":

                if (!mBTDevicesHashMap.containsKey(address)) {
                    BTLE_Device btleDevice = new BTLE_Device(device);
                    btleDevice.setRSSI(rssi);

                    mBTDevicesHashMap.put(address, btleDevice);
                    mBTDevicesArrayList.add(btleDevice);
                    //Log.d("MAC", device.getAddress());
                    //Log.d("RSSI", Integer.toString(rssi));
                } else {
                    mBTDevicesHashMap.get(address).setRSSI(rssi);
                    //Log.d("MAC", device.getAddress());
                    //Log.d("RSSI", Integer.toString(rssi));
                }

                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    public void startLoop(){
        isLooping = true;
        startScan();
    }

    public void stopLoop(){
        isLooping = false;
        stopScan();
    }

    public void startScan(){
        upLoading = true;
        btn_Scan.setText("Scanning...");

        mBTDevicesArrayList.clear();
        mBTDevicesHashMap.clear();

        mBTLeScanner.start();

        upload_after_delay();
    }

    public void stopScan() {
        mBTLeScanner.stop();
        if(upLoading) {
            stopUpload();
        }
        if(isLooping) {
            startScan();
        }else{
            //loop_count = 0;
            btn_Scan.setText("Scan Again");
        }
    }

    public void upload_after_delay(){
        upLoading = true;
        mHandler.postDelayed(runnable, 1000);
        loop_count++;
    }

    public void stopUpload(){
        upLoading = false;
        mHandler.removeCallbacks(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            for(String address:addressList){
                if(!mBTDevicesHashMap.containsKey(address)) {
                    BTLE_Device btleDevice = new BTLE_Device(address, -100);
                    mBTDevicesHashMap.put(address, btleDevice);
                    mBTDevicesArrayList.add(btleDevice);
                }
            }
            request.upload_deviceList(mBTDevicesArrayList, loop_count);
            if(upLoading) {
                upload_after_delay();
            }
        }
    };
}
