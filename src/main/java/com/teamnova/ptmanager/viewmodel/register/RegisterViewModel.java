package com.teamnova.ptmanager.viewmodel.register;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.repository.login.LoginRepository;
import com.teamnova.ptmanager.repository.register.RegisterRepository;
import com.teamnova.ptmanager.test.TestDTO;

import java.io.File;

/**
 *  클래스 역할
 *      1) 회원가입 시 사용되는 viewModel
 *      2) 회원 데이터를 서버로 전송하는 역할
 * */
public class RegisterViewModel extends ViewModel {
    // 데이터 처리완료 여부를 저장하는 변수
    private MutableLiveData<String> registerResult;

    // 데이터 처리를 위한 Repo객체
    private RegisterRepository registerRepository;

    // 변수, 저장소 초기화
    public RegisterViewModel() {
        registerResult = new MutableLiveData<>();
        registerRepository = new RegisterRepository();
    }

    // 회원정보를 담을 객체 생성
    public UserInfoDto makeRegisterInfo(int userType, String loginId, String pw, String userName, String phoneNum, String branchOffice, String birth, int gender){
        // 트레이너인 경우
        if(userType == 0){
            return new UserInfoDto(userType, loginId, pw, userName, phoneNum, branchOffice, null, 9999);
        } else {// 일반회원인 경우
            return new UserInfoDto(userType, loginId, pw, userName, phoneNum, null, birth, gender);
        }
    }

    // 회원정보를 서버에 저장
    public void registerUserInfo(UserInfoDto userRegisterInfo){
        registerRepository.registerUserInfo(registerResult, userRegisterInfo);
    }

    // 회원정보 전달완료 여부를 저장하는 registerResult객체를 반환하는 메소드
    public LiveData<String> getRegisterResult(){
        return registerResult;
    }

    // 프로필사진 저장
    public void transferImgToServer(Handler retrofitResultHandler, File profileImgFile){
        registerRepository.transferImgToServer(retrofitResultHandler, profileImgFile);
    }
}
