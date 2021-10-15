package com.teamnova.ptmanager.test;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.teamnova.ptmanager.R;

public class ProfileExActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_ex);

        ImageView img = findViewById(R.id.user_profile_ex);

        Glide.with(getApplicationContext()).asBitmap().load("http://15.165.144.216/img/profile/20210611043455_JPEG_20210611_133453_.jpg")
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        img.setImageBitmap(resource);
                        //할일

                    }
                });
    }
}