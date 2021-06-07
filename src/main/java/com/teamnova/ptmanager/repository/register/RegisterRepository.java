package com.teamnova.ptmanager.repository.register;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;

import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.network.login.LoginApiClient;
import com.teamnova.ptmanager.network.register.RegisterApiClient;
import com.teamnova.ptmanager.test.TestDTO;

import java.io.File;

public class RegisterRepository {
    // 서버와 통신하는 객체
    private RegisterApiClient registerApiClient;

    // 통신 인스턴스 생성
    public RegisterRepository() {
        registerApiClient = new RegisterApiClient();
    }

    // 회원정보를 서버에 전달
    public void registerUserInfo(MutableLiveData<String> registerResult, UserInfoDto userRegisterInfo){
        registerApiClient.registerUserInfo(registerResult, userRegisterInfo);
    }

    // 프로필사진 저장
    public void transferImgToServer(Handler retrofitResultHandler, File profileImgFile){
        registerApiClient.transferImgToServer(retrofitResultHandler, profileImgFile);
    }
}
