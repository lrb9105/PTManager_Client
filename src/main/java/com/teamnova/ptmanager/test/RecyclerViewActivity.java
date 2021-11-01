package com.teamnova.ptmanager.test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideCustomImageLoader;
import com.teamnova.ptmanager.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BigImageViewer.initialize(GlideCustomImageLoader.with(getApplicationContext(), SampleCustomImageSizeModel.class));

        setContentView(R.layout.activity_recycler_view);

        configRecycler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BigImageViewer.imageLoader().cancelAll();
    }

    private void configRecycler() {
        final List<String> imageUrlsList = new ArrayList<>();

        imageUrlsList.add(
                "http://15.165.144.216//img/profile/20210611043455_JPEG_20210611_133453_.jpg");
        imageUrlsList.add(
                "http://15.165.144.216//img/profile/20210611043455_JPEG_20210611_133453_.jpg");
        imageUrlsList.add(
                "http://15.165.144.216//img/profile/20210611043455_JPEG_20210611_133453_.jpg");
        imageUrlsList.add(
                "http://15.165.144.216//img/profile/20210611043455_JPEG_20210611_133453_.jpg");
        imageUrlsList.add(
                "http://15.165.144.216//img/profile/20210611043455_JPEG_20210611_133453_.jpg");
        imageUrlsList.add(
                "http://15.165.144.216//img/profile/20210611043455_JPEG_20210611_133453_.jpg");
        imageUrlsList.add(
                "http://15.165.144.216//img/profile/20210611043455_JPEG_20210611_133453_.jpg");
        imageUrlsList.add(
                "http://15.165.144.216//img/profile/20210611043455_JPEG_20210611_133453_.jpg");
        imageUrlsList.add(
                "http://15.165.144.216//img/profile/20210611043455_JPEG_20210611_133453_.jpg");
        imageUrlsList.add(
                "http://15.165.144.216//img/profile/20210611043455_JPEG_20210611_133453_.jpg");
        imageUrlsList.add(
                "http://15.165.144.216//img/profile/20210611043455_JPEG_20210611_133453_.jpg");
        imageUrlsList.add(
                "http://15.165.144.216//img/profile/20210611043455_JPEG_20210611_133453_.jpg");
        imageUrlsList.add(
                "http://15.165.144.216//img/profile/20210611043455_JPEG_20210611_133453_.jpg");
        imageUrlsList.add(
                "http://15.165.144.216//img/profile/20210611043455_JPEG_20210611_133453_.jpg");
        imageUrlsList.add(
                "http://15.165.144.216//img/profile/20210611043455_JPEG_20210611_133453_.jpg");

        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler.setAdapter(new RecyclerAdapter(imageUrlsList));

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recycler);
    }
}