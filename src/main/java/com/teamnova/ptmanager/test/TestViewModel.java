package com.teamnova.ptmanager.test;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.teamnova.ptmanager.model.userInfo.UserInfoDto;

public class TestViewModel extends ViewModel {
    // view가 관찰하기위한 데이터
    private MutableLiveData<UserInfoDto> data;

    // 데이터 처리를 담당 하는 객체
    private TestRepository repository;

    public TestViewModel() {
        repository = new TestRepository();
        data = new MutableLiveData<>();
    }

    // 아이디에 해당하는 사용자 정보를 가져 옴
    public void getUserInfo(String id){
        repository.getUserInfo(data, id);
    }

    // LiveData 반환
    public MutableLiveData<UserInfoDto> getData() {
        return data;
    }
}
