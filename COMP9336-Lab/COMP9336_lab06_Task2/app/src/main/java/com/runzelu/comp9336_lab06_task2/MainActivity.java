package com.runzelu.comp9336_lab06_task2;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorEvent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView initial, end, consume;
    private TextView usage_item;
    private float batteryPct_init=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initial = (TextView)findViewById(R.id.initial_level);
        end = (TextView) findViewById(R.id.final_level);
        consume = (TextView) findViewById(R.id.consumed_battery);
        usage_item = (TextView) findViewById(R.id.usage_time);

        this.registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask dotask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable(){
                    public void run(){
                        checkConnection();
                        chech_again();
                    }
                });
            }
        };
        timer.schedule(dotask,300000,10000);

    }

    private void chech_again() {
        this.registerReceiver(this.batteryInfoReceiver2, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }
    private BroadcastReceiver batteryInfoReceiver2 = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            int level2 = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct2 = (level2 / (float)scale) * 100;
            end.setText("Final Level: " + batteryPct2 + "%\n");
            float difference = batteryPct2 - batteryPct_init*100;
            consume.setText("Consumed battery: " + difference+"%\n");
        }
    };


    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver(){
        //     @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            final float initial_value = level / (float)scale;
            if (batteryPct_init==0){
                batteryPct_init = initial_value;
                initial.setText("Initial level of battery: "+ batteryPct_init*100+"%\n");
            }


            //            if ((event.timestamp-last_time)*NS2S>60){
            //
            //                checkConnection();
            //
            //                int level2 = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            //                float batteryPct2 = level2 / (float)scale;
            //                end.setText("Final Level: " + batteryPct2*100 + "%\n");
            //                float difference = batteryPct2 - batteryPct;
            //                consume.setText("Consumed battery: " + difference*100+"%\n");
            //            }
        }
    };


    private void checkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //  BluetoothAdapter mBluetoothAdaptor = BluetoothAdapter.getDefaultAdapter();


        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(networkInfo.isConnected() && !isGPSEnabled){
            usage_item.setText("Using Wi-Fi for 10 minutes:");
        }else if (isGPSEnabled && !networkInfo.isConnected()){
            usage_item.setText("Using GPS for 10 minutes:");
        }else{
            usage_item.setText("Normal usage of mobile phone for 10 minutes:");
        }

        //        wifiConnected = (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);


        //        if(networkInfo!=null&&networkInfo.isConnected() && mBluetoothAdaptor==null){
        //            if (wifiConnected){
        //                usage_item.setText("Using WiFi for 10 minutes:");
        //            }
        //        }else if (mBluetoothAdaptor!=null){
        //            if (mBluetoothAdaptor.isEnabled())
        //                usage_item.setText("Using Bluetooth for 10 minutes:");
        //        }else{
        //            usage_item.setText("Normal usage of mobile phone for 10 minutes:");
        //        }
    }


}

