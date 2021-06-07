package com.teamnova.ptmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamnova.ptmanager.databinding.ActivityMainBinding;
import com.teamnova.ptmanager.network.login.LoginService;
import com.teamnova.ptmanager.test.TestDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private Button btn1;
    private TextView textView;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("testtest","000000");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.btn1);
        textView = findViewById(R.id.text);

        /*btn1.setOnClickListener(new View.OnClickListener() {*//*
            @Override
            public void onClick(View v) {

                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://15.165.144.216/")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                LoginService service = retrofit.create(LoginService.class);
                Call<TestDTO> call = service.test();

                call.enqueue(new Callback<TestDTO>() {
                    @Override
                    public void onResponse(Call<TestDTO> call, Response<TestDTO> response) {
                        TestDTO dto = response.body();

                        textView.setText(dto.getId());

                    }

                    @Override
                    public void onFailure(Call<TestDTO> call, Throwable t) {
                        Log.d("testtest",t.getMessage());
                        Toast.makeText(MainActivity.this, "실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }*//*
        });*/
    }
}