package com.runzelu.comp9336_lab06;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView USB, current_level, consumption;
    private Button button;
    private int intLevel;
    private int intScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        current_level = (TextView) findViewById(R.id.textView);
        USB = (TextView) findViewById(R.id.textView2);
        button = (Button) findViewById(R.id.button);
        consumption = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task1();
            }
        });

        consumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent task2 = new Intent();
                task2.setClass(MainActivity.this, Task2Activity.class);
                startActivity(task2);
            }
        });
    }

    private void Task1() {
        this.registerReceiver(this.batteryInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    // registering the intent
    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // getIntExtra() Retrieve extended data from the intent
            // EXTRA_STATUS indicating integer containing the current status constant.
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

            // containing the current battery level
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            // integer containing the maximum battery level.
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            // convert level to percentage
            float batteryPct = level / (float)scale;
            current_level.setText("Current level of battery is: " + level + "%\n");
            if (usbCharge && isCharging){
                USB.setText("Mobile is charging via USB\n");
            }
        }
    };
}
