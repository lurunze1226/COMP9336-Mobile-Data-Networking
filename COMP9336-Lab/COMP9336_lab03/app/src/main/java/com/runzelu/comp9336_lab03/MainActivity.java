package com.runzelu.comp9336_lab03;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    LocationListener locationListener = null;
    LocationManager locationManager = null;
    String mProbiderName = null;
    Button button, status;
    TextView textView, textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.location);
        textView = (TextView) findViewById(R.id.myLocationText);
        status = (Button) findViewById(R.id.status);
        textView2 = (TextView) findViewById(R.id.status_view);
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isGPSEnabled) {
                    textView2.setText("GPS is active\n");
                } else {
                    textView2.setText("GPS is not active\n");
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GPStracker g = new GPStracker(getApplicationContext());
                Location location = g.getLcation();
                if (location != null) {
//                    Calendar c = Calendar.getInstance();
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd,MMMM,YYYY hh,mm,a");
//                    String strDate = sdf.format(c.getTime());
                    Date currentTime = Calendar.getInstance().getTime();
                    String data_time = currentTime.toString();
                    textView.setText("Date/Time: " + data_time + "\n" +
                            "Provider: " + location.getProvider() + "\n" +
                            "Accuracy: " + location.getAccuracy() + "\n" +
                            "Latitude: " + location.getLatitude() + "\n" +
                            "Altitude: " + location.getLongitude() + "\n" +
                            "Speed: " + location.getSpeed() + "\n");
                } else {
                    Toast.makeText(getApplicationContext(), "No GPS", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
