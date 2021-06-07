package com.teamnova.ptmanager.viewmodel.login;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.test.TestDTO;
import com.teamnova.ptmanager.repository.login.LoginRepository;

import java.io.File;

public class LoginViewModel extends ViewModel {
    // 로그인 결과
    private MutableLiveData<String> loginResult;

    // 로그인 처리를 담당할 저장소 객체
    private LoginRepository loginRepository;

    // 사용자 정보
    private MutableLiveData<UserInfoDto> loginUserInfo;

    private UserInfoDto loginUserInfo2;

    private String id;

    // 초기화
    public LoginViewModel() {
        loginRepository = new LoginRepository();
        loginResult = new MutableLiveData<>();
        loginUserInfo = new MutableLiveData<>();
    }

    // 로그인 결과 인스턴스 반환
    public MutableLiveData<String> getLoginResult() {
        return loginResult;
    }

    // 로그인
    public void login(String id, String pw){
        loginRepository.login(loginResult, id, pw);
    }

    // 아이디 가져오기
    public void getLoginId(Handler handler, String name, String phoneNum){
        loginRepository.getLoginId(handler, name, phoneNum);
    }

    // 유효한 아이디인지 확인
    public void checkIsValidId(Handler handler, String loginId){
        loginRepository.checkIsValidId(handler, loginId);
    }

    // 비밀번호 초기화(재설정)
    public void updatePw(Handler handler, String loginId, String pw){
        loginRepository.updatePw(handler, loginId, pw);
    }

    // 아이디에 해당하는 사용자 정보를 가져 옴
    public void getUserInfo(String id, int type){
        loginRepository.getUserInfo(loginUserInfo, id, type);
    }

    public MutableLiveData<UserInfoDto> getUserInfo() {
        return loginUserInfo;
    }

    public UserInfoDto getLoginUserInfo2() {
        return loginUserInfo2;
    }

    public void setLoginUserInfo2(UserInfoDto loginUserInfo2) {
        this.loginUserInfo2 = loginUserInfo2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
