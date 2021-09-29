package com.teamnova.ptmanager.repository.changehistory.inbody;

import android.os.Handler;

import com.teamnova.ptmanager.network.changehistory.eyebody.EyeBodyApiClient;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 눈바디 저장소 객체
 * */
public class InBodyRepository {
    // 서버와 통신을 담당하는 객체
    private EyeBodyApiClient eyeBodyApiClient;

    // 초기화
    public InBodyRepository() {
        eyeBodyApiClient = new EyeBodyApiClient();
    }

    // 눈바디 사진 저장
    public void registerEyeBodyInfo(Handler retrofitResultHandler, RequestBody userId, MultipartBody.Part eyeBodyFile){
        eyeBodyApiClient.registerEyeBodyInfo(retrofitResultHandler, userId, eyeBodyFile);
    }
}
