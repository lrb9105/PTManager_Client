package com.teamnova.ptmanager.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.GlideImageViewFactory;
import com.github.piasy.biv.view.ImageSaveCallback;
import com.google.firebase.installations.Utils;
import com.teamnova.ptmanager.R;

public class GlideLoaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));

        setContentView(R.layout.activity_glide_loader);

        String url = getIntent().getStringExtra("url");

        System.out.println("url2: " + url);

        BigImageView bigImageView = findViewById(R.id.mBigImage);
        bigImageView.setProgressIndicator(new ProgressPieIndicator());
        bigImageView.setImageViewFactory(new GlideImageViewFactory());
        bigImageView.showImage(
                Uri.parse(url)
        );

        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigImageView bigImageView = findViewById(R.id.mBigImage);

                bigImageView.setImageSaveCallback(new ImageSaveCallback() {
                    @Override
                    public void onSuccess(String uri) {
                        Toast.makeText(GlideLoaderActivity.this,
                                "Success",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(GlideLoaderActivity.this,
                                "Fail",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                // should be called on worker/IO thread
                if (ActivityCompat.checkSelfPermission(GlideLoaderActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                bigImageView.saveImageIntoGallery();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        long start = System.nanoTime();
        //Utils.fixLeakCanary696(getApplicationContext());
        long end = System.nanoTime();
        //Log.w(Utils.TAG, "fixLeakCanary696: " + (end - start));

        BigImageViewer.imageLoader().cancelAll();
    }
}