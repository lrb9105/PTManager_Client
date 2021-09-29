package com.teamnova.ptmanager.repository.changehistory.eyebody;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;

import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.changehistory.eyebody.EyeBodyApiClient;
import com.teamnova.ptmanager.network.friend.FriendApiClient;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 눈바디 저장소 객체
 * */
public class EyeBodyRepository {
    // 서버와 통신을 담당하는 객체
    private EyeBodyApiClient eyeBodyApiClient;

    // 초기화
    public EyeBodyRepository() {
        eyeBodyApiClient = new EyeBodyApiClient();
    }

    // 눈바디 사진 저장
    public void registerEyeBodyInfo(Handler retrofitResultHandler, RequestBody userId, MultipartBody.Part eyeBodyFile){
        eyeBodyApiClient.registerEyeBodyInfo(retrofitResultHandler, userId, eyeBodyFile);
    }
}
