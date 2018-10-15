package com.runzelu.comp9336_lab04;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button button, button2, button3;
    private SensorManager sm;
    private ListView listView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button_2);
        button3 = (Button) findViewById(R.id.button_3);
        listView = (ListView) findViewById(R.id.sensorList);
        textView = (TextView) findViewById(R.id.text);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSensors();
            }
        });

        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent to_task2 = new Intent();
                to_task2.setClass(MainActivity.this, Task2Activity.class);
                startActivity(to_task2);
            }
        });
        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent to_task3 = new Intent();
                to_task3.setClass(MainActivity.this, Task3Activity.class);
                startActivity(to_task3);
            }
        });
    }

    public void showSensors() {
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensor_list = sm.getSensorList(Sensor.TYPE_ALL);
        // obtain list of all sensor
        ArrayList<String> show_list = new ArrayList<>();
        for (int i = 0; i < sensor_list.size(); ++i){
            show_list.add((i+1) + ". Name: " + sensor_list.get(i).getName() + "\n" +
                    "  Vendor: " + sensor_list.get(i).getVendor() + "\n" +
                    "  Version: " + sensor_list.get(i).getVersion() + "\n" +
                    "  MaximumRange: "+sensor_list.get(i).getMaximumRange() + "\n" +
                    "  Mindelay: " + sensor_list.get(i).getMinDelay());
        }
        textView.setText("Avaliable Sensors in This Phone:");
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.list_view, show_list));
    }
}
