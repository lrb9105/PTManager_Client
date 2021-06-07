package com.teamnova.ptmanager.viewmodel.sms;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.repository.register.RegisterRepository;
import com.teamnova.ptmanager.repository.sms.SmsRepository;

/**
 *  클래스 역할
 *      1) 문자 전송 및 인증 관련 기능을 제공하는 클래스
 * */
public class SmsViewModel extends ViewModel {
    // 문자 전송 완료를 처리할 변수
    private MutableLiveData<String> smsSendResult;

    // 인증 완료를 처리할 변수
    private MutableLiveData<String> smsCertificationResult;

    // 데이터 처리를 위한 Repo객체
    private SmsRepository smsRepository;

    // 변수, 저장소 초기화
    public SmsViewModel() {
        smsSendResult = new MutableLiveData<>();
        smsCertificationResult = new MutableLiveData<>();
        smsRepository = new SmsRepository();
    }

    // 서버에 문자 전송 요청
    public void smsSendReq(Handler handler, String loginId, String name, String phoneNum){
        smsRepository.smsSendReq(handler, loginId, name, phoneNum);
    }

    // 서버에 인증 요청
    public void smsCertificationReq(Handler handler, String phoneNum, String certificationNum){
        smsRepository.smsCertificationReq(handler, phoneNum, certificationNum);
    }

    // 문자 전송 결과 인스턴스 반환
    public LiveData<String> getSmsSendResult(){
        return smsSendResult;
    }

    // 인증 결과 인스턴스 반환
    public LiveData<String> getSmsCertificationResult(){
        return smsCertificationResult;
    }
}
