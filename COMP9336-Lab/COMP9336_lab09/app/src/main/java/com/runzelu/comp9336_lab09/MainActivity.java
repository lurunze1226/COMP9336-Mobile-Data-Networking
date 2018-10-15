package com.runzelu.comp9336_lab09;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button save, load, storage, task_3;
    EditText Name, email;
    TextView st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        save = (Button) findViewById(R.id.store);
        load = (Button) findViewById(R.id.retrieve);
        storage = (Button) findViewById(R.id.storage);
        st = (TextView) findViewById(R.id.storage_view);
        task_3 = (Button) findViewById(R.id.task3);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("name", Name.getText().toString());
                editor.putString("mailid", email.getText().toString());

                editor.commit();
            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
                String strname=sharedPreferences.getString("name","");
                String strmail = sharedPreferences.getString("mailid","");

                if (strname.equals("") || strmail.equals("")){
                    Toast.makeText(MainActivity.this,"Data was not found", Toast.LENGTH_SHORT).show();
                }else{
                    Name.setText(strname);
                    email.setText(strmail);
                }
            }
        });

        storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStorage();
            }
        });

        task_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent to_task3 = new Intent(MainActivity.this, Task3Activity.class);
                startActivity(to_task3);
            }
        });
    }

    private void checkStorage() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable;
        //obtain the capacity of the external storage
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        }
        else {
            bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        }
        long GigsAvailable = bytesAvailable / (1024 * 1024 * 1024);
        st.setText("The capacity of storage: " + GigsAvailable + " Gigs");
    }
}
