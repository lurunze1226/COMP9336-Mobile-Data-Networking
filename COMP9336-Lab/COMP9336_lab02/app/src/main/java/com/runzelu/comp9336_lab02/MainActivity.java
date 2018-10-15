package com.runzelu.comp9336_lab02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private ListView listView;
    private Button buttonScan, button_top;
    private ConnectivityManager connectivitymanager;
    private int size = 0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private Boolean topFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.wifi_list);
        buttonScan = (Button) findViewById(R.id.button);
        button_top = (Button) findViewById(R.id.button_2);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topFlag = false;
                scanWifi();
            }
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        button_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topFlag = true;
                scanWifi();
            }
        });
    }

    // dedicated BroadcastReceiver registered with the following intent
    private void scanWifi() {
        // check if the wifi is enabled
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this,
                    "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
    }

    // When the scanning is ended, the onReceive method of our
    // BroadcastReceiver implementation if called
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // call the getScanResults method of the WifiManager service to get the results
            results = wifiManager.getScanResults();
            // unregister our BroadcastReceiver to save the Battery of the device.
            unregisterReceiver(this);

            if (!topFlag) {
                arrayList.clear();
                for (ScanResult scanResult : results) {
                    arrayList.add(scanResult.SSID + " " +
                            scanResult.BSSID + " - " +
                            scanResult.capabilities + " - " +
                            "(" + scanResult.level + ")");
                    adapter.notifyDataSetChanged();
                }
            } else {
                arrayList.clear();
                Map<Integer, ScanResult> map = new TreeMap<Integer, ScanResult>(
                        new Comparator<Integer>() {
                            @Override
                            public int compare(Integer t1, Integer t2) {
                                return t2.compareTo(t1);
                            }
                        });

                for (ScanResult scanResult : results) {
                    map.put(scanResult.level, scanResult);
                }
                int i = 0;
                ArrayList<ScanResult> tmp = new ArrayList<>();
                for(Map.Entry<Integer, ScanResult> entry: map.entrySet()) {
                    if (i <= 3) {
                        if (!tmp.contains(entry.getValue())) {
                            tmp.add(entry.getValue());
                            i += 1;
                        } else {
                            continue;
                        }
                    } else {
                        break;
                    }
                }
                for(ScanResult w: tmp) {
                    arrayList.add(w.SSID + " " +
                            w.BSSID + " - " +
                            "(" + w.level + ")");
                    // mapping data to the listview
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };
}
