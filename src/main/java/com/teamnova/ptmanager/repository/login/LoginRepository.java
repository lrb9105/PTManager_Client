package com.teamnova.ptmanager.repository.login;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;

import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.test.TestDTO;
import com.teamnova.ptmanager.network.login.LoginApiClient;

import java.io.File;

public class LoginRepository {
    // 서버와 통신을 담당하는 객체
    private LoginApiClient loginApiClient;

    // 초기화
    public LoginRepository() {
        loginApiClient = new LoginApiClient();
    }

    // 로그인
    public void login( MutableLiveData<String> loginResult, String id, String pw){
        loginApiClient.login(loginResult, id, pw);
    }

    // 아이디 가져오기
    public void getLoginId(Handler handler, String name, String phoneNum){
        loginApiClient.getLoginId(handler, name, phoneNum);
    }

    // 유효한 아이디인지 확인
    public void checkIsValidId(Handler handler, String loginId){
        loginApiClient.checkIsValidId(handler, loginId);
    }

    // 비밀번호 초기화(재설정)
    public void updatePw(Handler handler, String loginId, String pw){
        loginApiClient.updatePw(handler, loginId, pw);
    }

    // 사용자 정보 가져 옴
    // 가져온 사용자 정보를 LiveData에 입력하기 위해 LiveData도 입력 받음
    public void getUserInfo(MutableLiveData<UserInfoDto> userInfoDto, String id, int type){
        loginApiClient.getUserInfo(userInfoDto, id, type);
    }
}
