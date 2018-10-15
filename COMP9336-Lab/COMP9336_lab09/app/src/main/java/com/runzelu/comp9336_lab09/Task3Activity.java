package com.runzelu.comp9336_lab09;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task3Activity extends AppCompatActivity {

    ListView listview;
    Button save, read;
    private WifiManager myWifiManager;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter arrayAdapter;
    private List<ScanResult> results;
    String result;
    List<String> myList;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task3);

        listview = (ListView) findViewById(R.id.listview);
        save = (Button) findViewById(R.id.save);
        read = (Button) findViewById(R.id.read);
        myWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!myWifiManager.isWifiEnabled()){
            Toast.makeText(this, "Wifi is disabled. you need to enable it", Toast.LENGTH_LONG).show();
            myWifiManager.setWifiEnabled(true);
        }

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String entire = arrayList.get(position);
                File root = Environment.getExternalStorageDirectory();

                File dir = new File(root.getAbsolutePath()+"/MyAppFile");

                File newfile = new File(dir,"MyMessage.txt");

                String message;
                try {
                    FileInputStream fileInputStream = new FileInputStream(newfile);
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuffer stringBuffer = new StringBuffer();
                    int i = 0;

                    // read file
                    while((message=bufferedReader.readLine())!=null){
                        if(i==position){
                            i+=1;
                            continue;
                        }
                        stringBuffer.append(message + ",");
                        i+=1;
                    }
                    if(myList.isEmpty() || arrayList.isEmpty()){
                        String newresult = stringBuffer.toString();
                        ArrayList<String> newList = new ArrayList<String>(Arrays.asList(newresult.split(",")));

                    }else{
                        myList.remove(position);
                        arrayList.remove(position);

                    }
                    arrayAdapter.notifyDataSetChanged();

                    Toast.makeText(Task3Activity.this,"Delete Done",Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    // Checks if external storage is available for read and write
    public void writeExternalStorage(View view)
    {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){

            File file = Environment.getExternalStorageDirectory();
            File dir = new File(file.getAbsolutePath()+"/MyAppFile");
            if(!dir.exists()){
                dir.mkdir();
            }
            File newfile = new File(dir,"MyMessage.txt");

            scanWifi();
            String listString = "";


            Toast.makeText(getApplicationContext(),"length="+arrayList.size(),Toast.LENGTH_LONG).show();

            for (String s : arrayList)
            {
                listString += s + ",";
            }

            String Message = listString; // transfer the APs info into message

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(newfile);
                fileOutputStream.write(Message.getBytes());
                fileOutputStream.close();

                Toast.makeText(getApplicationContext(),"APs info saved",Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(),"Nothing",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Nothing",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(),"SD card not found",Toast.LENGTH_LONG).show();
        }
    }

    public void readExternalStorage(View view){
        File root = Environment.getExternalStorageDirectory();

        File dir = new File(root.getAbsolutePath()+"/MyAppFile");

        File newfile = new File(dir,"MyMessage.txt");

        String message;
        try {
            FileInputStream fileInputStream = new FileInputStream(newfile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            while((message=bufferedReader.readLine())!=null){
                stringBuffer.append(message + "\n");
            }

            result = stringBuffer.toString();//sth wrong here

            myList = new ArrayList<String>(Arrays.asList(result.split(",")));

            //     arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);
            arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,myList);

            listview.setAdapter(arrayAdapter);

            // set the preview record as listview
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scanWifi(){
        arrayList.clear();
        results = myWifiManager.getScanResults();

        for (ScanResult scanResult : results){
            Date currentTime = Calendar.getInstance().getTime();
            String data_time = currentTime.toString();
            GPStracker g =  new GPStracker(getApplicationContext());
            Location location = g.getLcation();

            String latitude="";
            String longitude="";

            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                latitude = Double.toString(lat);
                longitude = Double.toString(lng);
            }

            String ssid = scanResult.SSID;
            int level = scanResult.level;
            String strength = Integer.toString(level);
            arrayList.add("Time: " + data_time + "\n" +
                    "Location: " + "(" + latitude + longitude + ")" +"\n" +
                    "SSID: " + ssid + "\n" +
                    "Signal strength: " + "[" + strength + "]" + "\n");
        }

    }
}
