package com.teamnova.ptmanager.network.sms;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SmsApiClient {
    // api 서버와 통신을 하기위한 통신객체
    private Retrofit retrofit;

    // retrofit 인스턴스 생성
    public SmsApiClient() {
        retrofit = RetrofitInstance.getRetroClient();
    }

    // 서버에 문자 전송 요청
    public void smsSendReq(Handler handler, String loginId, String name, String phoneNum){
        Log.d("phoneNum", phoneNum);

        // 웹서비스 구현체 생성
        SmsService smsService = retrofit.create(SmsService.class);
        Call<String> call;

        /**
         * 웹서비스로 보낼 httpReq생성
         * 1. 회원가입: phoneNum만 있음
         * 2. 아이디 찾기: loginId 없음
         * 3. 비밀번호 찾기: name 없음
         * */
        if(name == null && loginId == null){ // 회원가입
            call = smsService.smsSendReq(null, null, phoneNum);
        } else if(loginId == null){ // 아이디 찾기
            call = smsService.smsSendReq(null, name, phoneNum);
        } else { // 비밀번호 찾기
            call = smsService.smsSendReq(loginId, null, phoneNum);
        }

        /**
         * 1) 비동기 방식으로 서버에 유저정보 전달
         * 2) 성공일 경우 ok반환, 실패일 경우 fail반환
         * */
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();
                Log.d("aaaaa",response.body());

                // 문자전송 완료 시 handler를 이용하여 결과 전송
                Message msg = handler.obtainMessage(0, result);
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("bbbbb","22222");
                // 통신 실패 시 viewModel에 null 업데이트
                Message msg = handler.obtainMessage(0, null);
                handler.sendMessage(msg);
            }
        });
    }

    // 서버에 인증 요청
    public void smsCertificationReq(Handler handler, String phoneNum, String certificationNum){
        // 웹서비스 구현체 생성
        SmsService smsService = retrofit.create(SmsService.class);

        // 웹서비스로 보낼 httpReq생성
        Call<String> call = smsService.smsCertificationReq(phoneNum, certificationNum);

        /**
         * 1) 비동기 방식으로 서버에 유저정보 전달
         * 2) 성공일 경우 ok반환, 실패일 경우 fail반환
         * */
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();

                Log.d("인증일치여부: ", result);
                // 문자전송 완료 시 handler를 이용하여 결과 전송
                Message msg = handler.obtainMessage(1, result);
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("인증일치여부: ", "null");

                // 통신 실패 시 핸들러를 통해 메시지 전달
                Message msg = handler.obtainMessage(1, null);
                handler.sendMessage(msg);
            }
        });
    }
}
