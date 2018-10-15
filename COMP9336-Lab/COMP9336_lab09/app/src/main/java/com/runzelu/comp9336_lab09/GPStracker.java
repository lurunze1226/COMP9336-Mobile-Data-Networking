package com.runzelu.comp9336_lab09;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class GPStracker extends Service implements LocationListener {
    Context context;
    public GPStracker(Context c){
        context = c;
    }


    public Location getLcation(){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(context,"Perminsson not greanter",Toast.LENGTH_SHORT).show();
            return null;
        }
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetWorkEnable = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isNetWorkEnable){
            Toast.makeText(context,"Please enable NetWork",Toast.LENGTH_LONG).show();
            return null;
        }else{
            Toast.makeText(context,"NetWork has been enabled",Toast.LENGTH_LONG).show();
            Toast.makeText(context,"GPS has been enabled",Toast.LENGTH_LONG).show();
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,6000,10,this);
            Location l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            Location l = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (l != null) {
                return l;
            }
        }
        if (isGPSEnabled){
            Toast.makeText(context,"GPS has been enabled",Toast.LENGTH_LONG).show();
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,6000,10,this);
            Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            Location l = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            return l;
        }else{
            Toast.makeText(context,"Please enable GPS",Toast.LENGTH_LONG).show();
        }
        return null;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Nullable

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
