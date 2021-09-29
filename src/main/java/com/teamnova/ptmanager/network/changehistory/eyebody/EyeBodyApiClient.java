package com.teamnova.ptmanager.network.changehistory.eyebody;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.friend.FriendService;
import com.teamnova.ptmanager.network.register.RegisterService;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 눈바디 관련 통신을 하는 네트워크 객체
 * */
public class EyeBodyApiClient {
    // api 서버와 통신을 하기위한 통신객체
    private Retrofit retrofit;

    public EyeBodyApiClient() {
        retrofit = RetrofitInstance.getRetroClient();
    }

    // 눈바디 정보 저장
    public void registerEyeBodyInfo(Handler retrofitResultHandler, RequestBody userId, MultipartBody.Part eyeBodyFile){
        // 웹서비스 구현체 생성
        EyeBodyService service = retrofit.create(EyeBodyService.class);

        // 서버로 전송
        Call<EyeBody> call = service.registerEyeBodyInfo(userId, eyeBodyFile);

        call.enqueue(new Callback<EyeBody>() {
            @Override
            public void onResponse(Call<EyeBody> call, Response<EyeBody> response) {
                if (response.isSuccessful()){
                    EyeBody eyeBody = response.body();

                    Log.d("눈바디 이미지 파일 전송결과:", eyeBody.getSavePath());

                    Message msg = retrofitResultHandler.obtainMessage(0, eyeBody);
                    retrofitResultHandler.sendMessage(msg);
                } else{
                    Log.d("눈바디 이미지파일 전송결과 실패:", "실패");

                    Message msg = retrofitResultHandler.obtainMessage(0, null);
                    retrofitResultHandler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(Call<EyeBody> call, Throwable t) {
                Log.d("4444",t.getMessage());
                Message msg = retrofitResultHandler.obtainMessage(0, null);
                retrofitResultHandler.sendMessage(msg);
            }
        });
    }
}
