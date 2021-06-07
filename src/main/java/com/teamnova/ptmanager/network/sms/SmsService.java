package com.teamnova.ptmanager.network.sms;

import com.teamnova.ptmanager.model.userInfo.UserInfoDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SmsService {
    /**
     *  1) 문자전송 요청을 서버에 보내는 메소드
     *  2) 사용자의 이름과 전화번호를 함꼐 전송한다.
     * */
    @GET("sms/smsSendReq.php")
    Call<String> smsSendReq(@Query("loginId") String loginId, @Query("name") String name, @Query("phoneNum") String phoneNum);


    /**
     *  1) 인증요청을 서버에 보내는 메소드
     *  2) 사용자의 전화번호와 인증번호를 함꼐 전송한다.
     * */
    @GET("sms/smsCertificationReq.php")
    Call<String> smsCertificationReq(@Query("phoneNum") String phoneNum, @Query("certificationNum") String certificationNum);
}