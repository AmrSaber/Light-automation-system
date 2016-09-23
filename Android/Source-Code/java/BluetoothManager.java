package amrsaber.smarthome;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.UUID;

public class BluetoothManager {

	private BluetoothAdapter bluetoothAdapter;
	private BluetoothSocket bluetoothSocket;
	private BluetoothDevice module;

	
	private OutputStream out;
	private InputStream in;

    //uuid for arduino
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// constructor, gets the adapter and fills the devices list
	public BluetoothManager() {
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.bluetoothAdapter.cancelDiscovery();
	}

	public void getDevice(String deviceName) throws IOException {
		if (!bluetoothAdapter.isEnabled()) {throw new IOException("Bluetooth is off !!");}
		Set<BluetoothDevice> set = this.bluetoothAdapter.getBondedDevices();
		for (BluetoothDevice bd : set) {
			if (bd.getName().equals(deviceName)) {
				this.module = bd;
				break;
			}
		} // end for
		if (module == null) {
			throw new IOException("Make sure you are Paired with the HC-05 Module !");
		}
	}

	public void connect() throws IOException {
		if(isConnected()) return;
        if(module == null) throw new IOException("Device not set");
		this.bluetoothSocket = module.createRfcommSocketToServiceRecord(MY_UUID);
		try {
			this.bluetoothSocket.connect();
		} catch (IOException ioe) {
			throw new IOException("Target device is not available");
		}
        //get the streams and wrap them in reader and writer to enable sending/recieving strings
		this.out = bluetoothSocket.getOutputStream();
		this.in = bluetoothSocket.getInputStream();
	}// end method connect

	public void close() throws IOException {
		if(!gotDevice() || !isConnected()) return;
		this.out.flush();
		this.out.close();
		this.in.close();
		this.bluetoothSocket.close();
	}// end method close

	public void write(int toWrite) throws IOException {
		char bridge = (char) toWrite;
		out.write(bridge);
		out.flush();
	}

    //TODO: Test this function
    public void write(String message)throws IOException{
        //The fastest way that does the job
        out.write(message.getBytes(Charset.forName("UTF-8")));
        out.flush();
    }
	
    //TODO: Test this function
    //this function blocks until reading is done
    public String readString()throws IOException{
        //this is the fastest way that does the job
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = in.read(buffer);
        while (length != -1) {
            result.write(buffer, 0, length);
            length = in.read(buffer);
        }
        return result.toString("UTF-8");
    }

	//this function blocks until reading is done
    public int readInt() throws IOException {
        return in.read();
    }

	public boolean gotDevice() {
		return this.module != null;
	}

	public boolean isConnected() {
		return bluetoothSocket != null && this.bluetoothSocket.isConnected();
	}


}//end class
