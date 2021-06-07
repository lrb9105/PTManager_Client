package com.teamnova.ptmanager.repository.sms;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;

import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.network.register.RegisterApiClient;
import com.teamnova.ptmanager.network.sms.SmsApiClient;

public class SmsRepository {
    // 서버와 통신하는 객체
    private SmsApiClient smsApiClient;

    // 통신 인스턴스 생성
    public SmsRepository() {
        smsApiClient = new SmsApiClient();
    }

    // 서버에 문자 전송 요청
    public void smsSendReq(Handler handler, String loginId, String name, String phoneNum){
        smsApiClient.smsSendReq(handler, loginId, name, phoneNum);
    }

    // 서버에 인증 요청
    public void smsCertificationReq(Handler handler, String phoneNum, String certificationNum){
        smsApiClient.smsCertificationReq(handler, phoneNum, certificationNum);
    }
}
