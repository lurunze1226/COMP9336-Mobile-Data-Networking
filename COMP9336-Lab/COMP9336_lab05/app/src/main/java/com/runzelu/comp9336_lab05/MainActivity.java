package com.runzelu.comp9336_lab05;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private GPS gps;
    private TextView txtLocation;
    private TextView txtRotation;
    private TextView txtAngle;
    private TextView txtMagnet;
    private TextView txtHeading;
    private TextView txtTrueHeading;
    private Button btnStart;
    private Button btnEnd;

    private SensorManager sensorManager;
    private Sensor magneticSensor;
    private Sensor gyroscopeSensor;
    private GeomagneticField geoField;
    private SensorEventListener gyroscopeListener;
    private SensorEventListener magenticListener;

    private float timestamp;
    private float rotation[] = new float[3];
    private float angleRadian[] = new float[3];
    private float angleDegree[] = new float[3];
    private float magnet[] = new float[3];
    private float heading;
    public static final String TAG = "MobileSensor2";
    // nano sec to sec
    private static final float NS2S = 1.0f / 1000000000.0f;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gps = new GPS(this);
        StringBuilder sb = new StringBuilder();
        if (gps.canGetLocation()) {
            sb.append(String.valueOf(gps.getLatitude()));
            sb.append(String.valueOf(gps.getLongitude()));
        }
        geoField = new GeomagneticField(
                Double.valueOf(gps.getLatitude()).floatValue(),
                Double.valueOf(gps.getLongitude()).floatValue(),
                Double.valueOf(gps.getAltitude()).floatValue(),
                System.currentTimeMillis()
        );

        txtLocation = findViewById(R.id.textView2);
        txtLocation.setText(sb);
        txtRotation = findViewById(R.id.txtRotation);
        txtAngle = findViewById(R.id.txtAngle);
        txtMagnet = findViewById(R.id.txtMagnet);
        txtHeading = findViewById(R.id.txtHeading);
        txtTrueHeading = findViewById(R.id.txtTrueHeading);
        btnStart = findViewById(R.id.btnStart);
        btnEnd = findViewById(R.id.btnEnd);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                angleRadian[0] = angleRadian[1] = angleRadian[2] = 0;
            }
        });
        // rotation record the z
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = "\nAngle: \n" + angleDegree[2];
                txtAngle.setText(s);
            }
        });

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 1500);
                String s = "Location: \n" + gps.getLongitude() + "\n" + gps.getLatitude() + "\n";
                txtLocation.setText(s);

                s = "Rotation: \nX: " + rotation[0] + "\nY: " + rotation[1] + "\nZ: " + rotation[2];
                txtRotation.setText(s);

                s = "\nMagnet field intensity: \nX: " + magnet[0] + "\nY: " + magnet[1] + "\nZ: " + magnet[2];
                txtMagnet.setText(s);

                s = "\nHeading: \n" + heading;
                txtHeading.setText(s);
                // Task 5
                // Obtain the true north heading by getting rid of declination angle based on your position.
                float trueHeading = heading - geoField.getDeclination();
                s = "\nTrue Heading: \n" + trueHeading;
                txtTrueHeading.setText(s);
            }
        };
        handler.postDelayed(r, 1500);

        gyroscopeListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (timestamp != 0) {
                    // difference timestamp between two rotation(nano sec to sec)
                    final float dT = (event.timestamp - timestamp) * NS2S;
                    rotation[0] = event.values[0];
                    rotation[1] = event.values[1];
                    rotation[2] = event.values[2];
                    // add the rotation degree at each axis, and obtain the radian rotated
                    // to the original position
                    angleRadian[0] += event.values[0] * dT;
                    angleRadian[1] += event.values[1] * dT;
                    angleRadian[2] += event.values[2] * dT;
                    // converting radian to degree
                    angleDegree[0] = (float) Math.toDegrees(angleRadian[0]);
                    angleDegree[1] = (float) Math.toDegrees(angleRadian[1]);
                    angleDegree[2] = (float) Math.toDegrees(angleRadian[2]);
                }
                timestamp = event.timestamp;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        // Task 4
        magenticListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                magnet[0] = event.values[0];
                magnet[1] = event.values[1];
                magnet[2] = event.values[2];
                if(magnet[0] > 0)
                    heading = (float) (270 + Math.toDegrees(Math.atan(magnet[2]/magnet[1])));
                else if(magnet[0] < 0)
                    heading = (float) (90 + Math.toDegrees(Math.atan(magnet[2]/magnet[1])));
                else {
                    if(magnet[1] > 0)
                        heading = 0;
                    else
                        heading = 180;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroscopeListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(magenticListener, magneticSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroscopeListener);
        sensorManager.unregisterListener(magenticListener);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { return super.onCreateOptionsMenu(menu); }
}
