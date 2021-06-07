package com.teamnova.ptmanager.viewmodel.login;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.teamnova.ptmanager.repository.login.LoginRepository;

public class ValidationCheckingViewModel extends ViewModel {
    // 로그인 처리를 담당할 저장소 객체
    private LoginRepository loginRepository;
    private boolean isValidName = false;
    private boolean isValidId = false;
    private boolean isValidPw = false;
    private boolean isValidPwConfirm = false;
    private boolean isValidBirthday = false;
    private boolean isValidPhoneNum = false;
    private boolean isAgree = false;


    // 초기화
    public ValidationCheckingViewModel() {
        loginRepository = new LoginRepository();
    }

    // 이름 유효성검사 체크
    public void checkIsValidName(){
        isValidName = true;
    }

    // 아이디 유효성검사 체크
    public void checkIsValidId(){
        isValidId = true;
    }

    // 비밀번호 유효성검사 체크
    public void checkIsValidPw(){
        isValidPw = true;
    }

    // 비밀번호 확인 유효성검사 체크
    public void checkIsValidPwConfirm(){
        isValidPwConfirm = true;
    }

    // 생년월일 유효성검사 체크
    public void checkIsValidBirthday(){
        isValidBirthday = true;
    }

    // 전화번호 유효성검사 체크
    public void checkIsValidPhoneNum(){
        isValidPhoneNum = true;
    }

    // 필수동의 입력 체크
    public void checkIsAgreeEnterd(){
        isAgree = true;
    }

    public void setValidBirthday(boolean validBirthday) {
        isValidBirthday = validBirthday;
    }

    public void setValidName(boolean validName) {
        isValidName = validName;
    }

    public void setValidId(boolean validId) {
        isValidId = validId;
    }

    public void setValidPw(boolean validPw) {
        isValidPw = validPw;
    }

    public void setValidPwConfirm(boolean validPwConfirm) {
        isValidPwConfirm = validPwConfirm;
    }

    public void setValidPhoneNum(boolean validPhoneNum) {
        isValidPhoneNum = validPhoneNum;
    }

    public void setAgree(boolean agree) {
        isAgree = agree;
    }

    // 유효성 검사가 모두 완료되었는지 여부를 반환하는 메소드
    public boolean isValidationCompleted(){
        boolean isAllCompleted = isValidName && isValidId && isValidPw && isValidPwConfirm && isValidBirthday && isValidPhoneNum&&isAgree;
        return isAllCompleted;
    }
}
