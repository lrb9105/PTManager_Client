package com.teamnova.ptmanager.network.register;

import android.os.FileUtils;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.login.LoginService;
import com.teamnova.ptmanager.test.TestDTO;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterApiClient {
    // api 서버와 통신을 하기위한 통신객체
    private Retrofit retrofit;

    // retrofit 인스턴스 생성
    public RegisterApiClient() {
        retrofit = RetrofitInstance.getRetroClient();
    }

    // 서베에 회원정보를 전달
    public void registerUserInfo(MutableLiveData<String> registerResult, UserInfoDto userRegisterInfo){
        // 웹서비스 구현체 생성
        RegisterService registerService = retrofit.create(RegisterService.class);

        // 웹서비스로 보낼 httpRequest생성(각각의 Call이 HttpRequest이다)
        Call<String> call = registerService.registerUserInfo(userRegisterInfo);

        /**
         * 1) 비동기 방식으로 서버에 유저정보 전달
         * 2) 성공일 경우 ok반환, 실패일 경우 fail반환
         * */
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();
                // 통신 완료 시 viewModel의 결과데이터 업데이트

                Log.d("111", result);

                registerResult.postValue(result);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // 통신 실패 시 viewModel에 null 업데이트
                registerResult.postValue(null);

                Log.d("222", "1111");
            }
        });
    }

    // 프로필사진 저장
    public void transferImgToServer(Handler retrofitResultHandler,  RequestBody loginId, MultipartBody.Part profileImgFile){
        // 웹서비스 구현체 생성
        RegisterService service = retrofit.create(RegisterService.class);

        // 서버로 전송
        Call<String> call = service.transferImgToServer(loginId, profileImgFile);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    String result = response.body();
                    
                    Log.d("프로필 이미지파일 전송결과:", result);

                    Message msg = retrofitResultHandler.obtainMessage(0, result);
                    retrofitResultHandler.sendMessage(msg);
                } else{
                    Log.d("프로필 이미지파일 전송결과 실패:", "실패");

                    Message msg = retrofitResultHandler.obtainMessage(0, null);
                    retrofitResultHandler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("4444",t.getMessage());
                Message msg = retrofitResultHandler.obtainMessage(0, null);
                retrofitResultHandler.sendMessage(msg);
            }
        });
    }
}
