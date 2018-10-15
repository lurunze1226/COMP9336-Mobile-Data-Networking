package com.runzelu.comp9336_lab01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.bt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myClick(view);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu	menu)	{
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void myClick(View v) {
        count++;
        textView = (TextView) findViewById(R.id.et);
        textView.setText("The number is: " + count);
    }
}
