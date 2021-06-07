package com.teamnova.ptmanager.test;

import androidx.lifecycle.MutableLiveData;

import com.teamnova.ptmanager.model.userInfo.UserInfoDto;

public class TestRepository {
    // API서버와 직접 통신하는 객체
    private TestApiClient testApiClient;

    public TestRepository() {
        testApiClient = new TestApiClient();
    }

    // 사용자 정보 가져 옴
    // 가져온 사용자 정보를 LiveData에 입력하기 위해 LiveData도 입력 받음
    public void getUserInfo(MutableLiveData<UserInfoDto> userInfoDto, String id){
        testApiClient.getUserInfo(userInfoDto, id);
    }
}
