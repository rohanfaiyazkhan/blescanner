package android.kaviles.bletutorial;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Kelvin on 5/8/16.
 */
public class BTLE_Device {

    private BluetoothDevice bluetoothDevice;
    private String name;
    private String address;
    private int rssi;

    public BTLE_Device(String address, int rssi){
        this.address = address;
        this.rssi = rssi;

        if(address.equals("01:17:C5:97:2C:F8"))
            name = "Soho";
        else if(address.equals("01:17:C5:5B:80:8D"))
            name = "Living Room P1";
        else if(address.equals("01:17:C5:5C:4E:AD"))
            name = "Foyer";
        else if(address.equals("01:17:C5:5B:B8:7E"))
            name = "Living Room P2";
        else if(address.equals("01:17:C5:5C:DE:49"))
            name = "Kitchen";
        else if(address.equals("01:17:C5:52:A1:DB"))
            name = "Bedroom";
    }


    public BTLE_Device(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.address = bluetoothDevice.getAddress();
        this.name = bluetoothDevice.getName();

        if(address.equals("01:17:C5:97:2C:F8"))
            name = "Soho";
        else if(address.equals("01:17:C5:5B:80:8D"))
            name = "Living Room P1";
        else if(address.equals("01:17:C5:5C:4E:AD"))
            name = "Foyer";
        else if(address.equals("01:17:C5:5B:B8:7E"))
            name = "Living Room P2";
        else if(address.equals("01:17:C5:5C:DE:49"))
            name = "Kitchen";
        else if(address.equals("01:17:C5:52:A1:DB"))
            name = "Bedroom";
    }

    public String getAddress() {
        return this.address;
    }

    public String getName() {
        return this.name;
    }

    public void setRSSI(int rssi) {
        this.rssi = rssi;
    }

    public int getRSSI() {
        return rssi;
    }

}
