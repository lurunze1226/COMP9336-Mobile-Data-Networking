package com.runzelu.comp9336_lab04;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Task2Activity extends AppCompatActivity implements SensorEventListener {

    private TextView text2, text2_1;
    private SensorManager sensorManager;
    private Sensor acc_sensor;
    private float x;
    private float y;
    private float z;
    private float[] gravity = new float[3];
    private float [] filterG = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task2);
        text2 = (TextView) findViewById(R.id.text2);
        text2_1 = (TextView) findViewById(R.id.text_2_1);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,acc_sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // obtain the x, y and z
        x = sensorEvent.values[0];
        y = sensorEvent.values[1];
        z = sensorEvent.values[2];
        // 9.81 is the gravity acceleration
        float x_g = (float) (x/9.81);
        float y_g = (float) (y/9.81);
        float z_g = (float) (z/9.81);

        float alpha = (float) 0.8;

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1-alpha) * sensorEvent.values[0];
        gravity[1] = alpha * gravity[1] + (1-alpha) * sensorEvent.values[1];
        gravity[2] = alpha * gravity[2] + (1-alpha) * sensorEvent.values[2];

        //Remove the gravity contribution with the high-pass filter.
        filterG[0] = (sensorEvent.values[0] - gravity[0]);
        filterG[1] = (sensorEvent.values[1] - gravity[1]);
        filterG[2] = (sensorEvent.values[2] - gravity[2]);

        text2.setText("Acceleration with Gravity:\n" +
                "x: " + x_g+"\n" +
                "y: " + y_g+ "\n" +
                "z: " + z_g + "\n");
        text2_1.setText("Acceleration without Gravity:\n" +
                "x: " + filterG[0] + "\n" +
                "y: " + filterG[1] + "\n" +
                "z: " + filterG[2] + "\n");
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
