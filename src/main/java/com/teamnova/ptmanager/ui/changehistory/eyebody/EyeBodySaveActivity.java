package com.teamnova.ptmanager.ui.changehistory.eyebody;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityEyeBodySaveBinding;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class EyeBodySaveActivity extends AppCompatActivity {
    private ActivityEyeBodySaveBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityEyeBodySaveBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        super.onCreate(savedInstanceState);


        ImageSelectActivity.startImageSelectionForResult(this, true, true, true, true, 1213);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1213 && resultCode == Activity.RESULT_OK) {
            // 받아온 파일
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);

            //Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
            //imageView.setImageBitmap(selectedImage);

            Intent intent = new Intent();
            intent.putExtra("eyeBodyPath", filePath);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}