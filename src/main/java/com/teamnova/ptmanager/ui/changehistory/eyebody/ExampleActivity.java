package com.teamnova.ptmanager.ui.changehistory.eyebody;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityExampleBinding;
import com.teamnova.ptmanager.databinding.ActivityEyeBodyChangeHistoryBinding;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class ExampleActivity extends AppCompatActivity {
    private ActivityExampleBinding binding;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = ActivityExampleBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        super.onCreate(savedInstanceState);

        imageView = findViewById(R.id.imageView);
        ImageSelectActivity.startImageSelectionForResult(this, true, true, true, true, 1213);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1213 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
            imageView.setImageBitmap(selectedImage);
        }
    }
}