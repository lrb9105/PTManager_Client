package com.teamnova.ptmanager.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.teamnova.ptmanager.MainActivity;
import com.teamnova.ptmanager.R;

public class BigImageExActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image_ex);

        findViewById(R.id.mGlide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BigImageExActivity.this, GlideLoaderActivity.class));
            }
        });

        findViewById(R.id.mRecycler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BigImageExActivity.this, RecyclerViewActivity.class));
            }
        });
    }
}