package com.teamnova.ptmanager.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.teamnova.ptmanager.MainActivity;
import com.teamnova.ptmanager.R;

public class WeekViewActivity4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view4);

        findViewById(R.id.buttonBasic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeekViewActivity4.this, WeekViewActivity3.class);
                startActivity(intent);
            }
        });
    }
}
