package com.runzelu.comp9336_lab10;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView1, textView2, textView3;
    private WifiManager wifiManager;
    int linkSpeed, frequency;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView) findViewById(R.id.tx_1);
        textView2 = (TextView) findViewById(R.id.tx_2);
        textView3 = (TextView) findViewById(R.id.tx_3);
        button = (Button) findViewById(R.id.button);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        // check wifi is enabled or not
        if (!wifiManager.isWifiEnabled()) {
            if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
                wifiManager.setWifiEnabled(true);
            }
        }
        // check if 5GHz Band id support or not
        if (wifiManager.is5GHzBandSupported()) {
            textView1.setText("5G WiFi is available on this device.\n");
        } else {
            textView1.setText("5G WiFi is not available on this device.\n");
        }

        // Task2
        WifiConfiguration config= new WifiConfiguration();
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            // LINK_SPEED_UNITS Mbps
            linkSpeed = wifiInfo.getLinkSpeed();
        }
        // FREQUENCY_UNITS is MHz
        frequency = wifiInfo.getFrequency();
        textView2.setText("Frequency: " + frequency + " " + wifiInfo.FREQUENCY_UNITS + "\n" +
        "Bit Rate: " + linkSpeed + " " + wifiInfo.LINK_SPEED_UNITS + "\n");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double f = new Double((double)frequency);
                double frequency_GHz =  f / 1000;
                Log.d("test", String.valueOf(frequency_GHz) + " " + String.valueOf(linkSpeed));
                if (frequency_GHz >= 4.9 && frequency_GHz <= 5.1 && linkSpeed < 54) {
                    textView3.setText("Current WiFi protocol is 802.11a\n" +
                            "The modulation is OFDM waveform.\n");
                } else if (frequency_GHz >= 2.3 && frequency_GHz <= 2.5 && linkSpeed < 11) {
                    textView3.setText("Current WiFi protocol is 802.11b\n" +
                            "The modulation is DSSS waveform.\n");
                } else if (frequency_GHz >= 2.3 && frequency_GHz <= 2.5 && linkSpeed < 54) {
                    textView3.setText("Current WiFi protocol is 802.11g\n" +
                            "The modulation is OFDM waveform.\n");
                } else if ((frequency_GHz >= 2.3 && frequency_GHz <= 2.5 && linkSpeed < 600) ||
                        (frequency_GHz >= 4.9 && frequency_GHz <= 5.1 && linkSpeed < 600)) {
                    textView3.setText("Current WiFi protocol is 802.11n\n" +
                            "The modulation is MIMO-OFDM waveform.\n");
                } else if (frequency_GHz >= 4.9 && frequency_GHz <= 5.1 && linkSpeed < 3466.8 ||
                        (frequency_GHz >= 0.053 && frequency_GHz <= 0.8 && linkSpeed < 568.9)) {
                    textView3.setText("Current WiFi protocol is 802.11ac\n" +
                            "The modulation is MIMO-OFDM waveform.\n");
                } else if (frequency_GHz >= 59 && frequency_GHz <= 61 && linkSpeed < 6757) {
                    textView3.setText("Current WiFi protocol is 802.11ad\n" +
                            "The modulation is OFDM(single carrier, low-power single carrier) waveform.\n");
                }
            }
        });

    }
}
