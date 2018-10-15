package com.runzelu.comp9336_lab07;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    Button status, send, discover;
    TextView text1, text3;
    ListView listView;
    EditText writeMsg;

    private BluetoothSocket mmSocket;
    private ArrayList<String> btList = new ArrayList<>();
    private ArrayList<BluetoothDevice> devices= new ArrayList<>();
    private static final String NAME = "BluetoothChat";
    private static final UUID MY_UUID = UUID.fromString("0001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;
    private int mState;
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming
    // connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing
    // connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote
    // device

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        status = (Button) findViewById(R.id.status);
        send = (Button) findViewById(R.id.send);
        discover = (Button) findViewById(R.id.discover);
        text1 = (TextView) findViewById(R.id.text1);
        text3 = (TextView) findViewById(R.id.text3);
        listView = (ListView) findViewById(R.id.listview);
        writeMsg = (EditText) findViewById(R.id.edit1);

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check whether your device supports Bluetooth or not:
                if (bluetoothAdapter == null) {
                    // Inform user that device does not support Bluetooth
                    text1.setText("This device has no Bluetooth Module.");
                } else {
                    // enable the bluetooth interface
                    if (!bluetoothAdapter.isEnabled()) {
                        text1.setText("Bluetooth is not enabled!");
                    } else {
                        text1.setText("Bluetooth is enabled!");
                    }
                }
            }
        });

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.enable();
                }
                // clear the caches info
                btList.clear();
                devices.clear();
                // map an array of objects
                // ArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects)
                listView.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                        R.layout.text_view, R.id.text2, btList));
                // Start the remote device discovery process.
                bluetoothAdapter.startDiscovery();
                // create a broadcast to receive the bluetooth message
                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        //Broadcast Action:
                        // The local Bluetooth adapter has started the remote device discovery process.
                        if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())){
                            text3.setText("Discovering...");
                        }

                        //Broadcast Action: Remote device discovered.
                        //Sent when a remote device is found during discovery.
                        if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                            //Retrieve extended data from the intent
                            // Get the BluetoothDevice object from the Intent
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            if (btList.indexOf(device.getName() + "\n" + device.getAddress()) == -1) {
                                // Add the name and address to an array adapter to
                                // show in a ListView
                                btList.add(device.getName() + "\n" + device.getAddress());
                                devices.add(device);
                            }
                        }


                        // finish the discovery
                        if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                            text3.setText("Discovery finished!");
                            // set the click event to list items
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    try {
                                        Method method = devices.get(i).getClass().getMethod("createBond", (Class[]) null);
                                        method.invoke(devices.get(i), (Object[]) null);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        // Broadcast Action: Indicates a change in the bond state of a remote device.
                        // For example, if a device is bonded (paired).
                        if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())){
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                            final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                            // BOND_BONDED:
                            if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                                Toast.makeText(getApplicationContext(),"Paired",Toast.LENGTH_SHORT).show();
                                ConnectThread ct  = new ConnectThread(device);
                                ct.start();

                            } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                                Toast.makeText(getApplicationContext(),"UnPaired",Toast.LENGTH_SHORT).show();

                            }

                        }

                        listView.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                                R.layout.text_view, R.id.text2, btList));
                    }
                };

                // Register the BroadcastReceiver
                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                registerReceiver(broadcastReceiver, filter);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread con = new ConnectedThread(mmSocket);
                con.start();
                String string = String.valueOf(writeMsg.getText());
            }
        });
    }



    private class ConnectThread extends Thread {

        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                //new ConnectedThread(mmSocket).start();

            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            // manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            String s = "123";
            buffer = s.getBytes();
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    mmOutStream.write(buffer);
                    mmOutStream.flush();
                    // Send the obtained bytes to the UI activity
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        // Call this from the main activity to shutdown the connection
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
