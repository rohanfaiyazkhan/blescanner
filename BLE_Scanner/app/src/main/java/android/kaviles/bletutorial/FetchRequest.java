package android.kaviles.bletutorial;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sony vaio on 19-Oct-17.
 */

public class FetchRequest {

    private String ipaddress = "";
    private String lhost = "";
    private String upload_script = "";
    private ArrayList<String> nnames = new ArrayList<>();
    private ArrayList<BTLE_Device> deviceList = new ArrayList<>();
    private Context c;
    private String jsonurl;
    private String name, rssi, address;
    private int rssi_array[];
    private int loop_counter;
    private boolean busy;
    final String test_url = "http://192.168.0.230/test.php";
    final String upload_url = "http://192.168.0.230/add_device.php";
    //final String upload_url = "http://192.168.0.125/add_device.php";
    private MainActivity ma;



    public FetchRequest(MainActivity mainActivity ) {
        ma = mainActivity;
        c = ma.getApplicationContext();
        // showing refresh animation before making http call
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(c);
        ipaddress = (shared.getString(c.getString(R.string.key_ip_address), ""));
        lhost = (shared.getString(c.getString(R.string.key_port), ""));
        name = new String();
        rssi = new String();
        address = new String();
        rssi_array = new int[6];
        // appending offset to url
        //String jsonurl = "http://" + ipaddress + ":" + lhost + "/json.jsp";
        //Log.d("URL", jsonurl);
    }

//    public void test_String(final String msg){
//        StringRequest stringrequest = new StringRequest(Request.Method.POST, test_url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("Server response", response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("Server error", error.toString());
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError{
//                Map<String, String> params = new HashMap<String, String>();
//                String message = msg;
//                params.put("msg",message);
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(c);
//        requestQueue.add(stringrequest);
//    }

//    public void upload_devices_json(ArrayList<BTLE_Device> devices, int loop){
//        this.deviceList = devices;
//        this.loop_counter = loop;
//        if(loop_counter<=0){
//            loop_counter = 1;
//        }
//    }

    public void upload_deviceList(ArrayList<BTLE_Device> devices, int loop){
        this.deviceList = devices;
        this.loop_counter = loop;
        if(loop_counter<=0){
            loop_counter = 1;
        }

        //upload_device(deviceList.get(0),0, looping_state);
        //TODO delete log
        Log.i("UploadStatus", "Attempting to upload " + name + " " + address + " " + rssi);

        StringRequest stringrequest = new StringRequest(Request.Method.POST, upload_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Server response", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Server error", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                for(int i = 0; i < deviceList.size() ; i++) {
                    BTLE_Device device = deviceList.get(i);
                    name = device.getName();
                    if (name == null || name.length() <= 0) {
                        name = "No name";
                    }
                    rssi_array[i] = device.getRSSI();
                    rssi = Integer.toString(device.getRSSI());
                    address = device.getAddress();
                    //TODO filter out addresses we need
                    if (address == null || address.length() <= 0) {
                        address = "No address";
                    }
                    params.put("name_"+i, name);
                    params.put("rssi_"+i, rssi);
                    params.put("address_"+i, address);
                    params.put("loop_counter_"+i, Integer.toString(loop_counter));
                }
                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringrequest);

    }

//    public void upload_deviceList(ArrayList<BTLE_Device> devices, int loop, boolean looping_state){
//        this.deviceList = devices;
//        this.loop_counter = loop;
//        if(loop_counter<=0){
//            loop_counter = 1;
//        }
//        upload_device(deviceList.get(0),0, looping_state);
//    }
//
//    public void upload_device(BTLE_Device device, final int counter, final boolean looping_state) {
//        name = device.getName();
//        if (name == null || name.length() <= 0) {
//            name = "No name";
//        }
//        rssi = Integer.toString(device.getRSSI());
//        address = device.getAddress();
//        //TODO filter out addresses we need
//        if (address == null || address.length() <= 0) {
//            address = "No address";
//        }
//
//        //TODO delete log
//        Log.i("UploadStatus", "Attempting to upload " + name + " " + address + " " + rssi);
//
//        StringRequest stringrequest = new StringRequest(Request.Method.POST, upload_url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("Server response", response);
//                int next_counter = counter + 1;
//                if (next_counter < deviceList.size()) {
//                    upload_device(deviceList.get(next_counter), next_counter, looping_state);
//                }else if(looping_state){
//                    ma.startScan();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("Server error", error.toString());
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("name", name);
//                params.put("rssi", rssi);
//                params.put("address", address);
//                params.put("loop_counter", Integer.toString(loop_counter));
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(c);
//        requestQueue.add(stringrequest);
//    }

//    public void upload_devices(ArrayList<BTLE_Device> devices, int loop){
//        this.deviceList = devices;
//        this.loop_counter = loop;
//        if(loop_counter<=0){
//            loop_counter = 1;
//        }
//
//        for(BTLE_Device device: devices) {
//
//            name = device.getName();
//            if(name == null || name.length() <= 0){
//                name = "No name";
//            }
//            rssi = Integer.toString(device.getRSSI());
//            address = device.getAddress();
//            //TODO filter out addresses we need
//            if(address == null || address.length() <= 0 ){
//                address = "No address";
//            }
//
//            StringRequest stringrequest = new StringRequest(Request.Method.POST, upload_url, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.e("Server response", response);
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e("Server error", error.toString());
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("name", name);
//                    params.put("rssi", rssi);
//                    params.put("address", address);
//                    params.put("loop_counter", Integer.toString(loop_counter));
//                    return params;
//                }
//            };
//
//            RequestQueue requestQueue = Volley.newRequestQueue(c);
//            requestQueue.add(stringrequest);
//        }
//    }

}
