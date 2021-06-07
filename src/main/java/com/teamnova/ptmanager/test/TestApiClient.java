package com.teamnova.ptmanager.test;

import androidx.lifecycle.MutableLiveData;

import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TestApiClient {
    // api 서버와 통신을 하기위한 통신객체
    private Retrofit retrofit;


    public TestApiClient() {
        retrofit = RetrofitInstance.getRetroClient();
    }

    public void getUserInfo(MutableLiveData<UserInfoDto> userInfo, String id) {
        // API서버와 통신하기 위한 인터페이스
        TestService service = retrofit.create(TestService.class);

        Call<UserInfoDto> call = service.getUserInfo(id);

        // enqueue 메소드는 비동기방식으로 데이터를 가져온다.
        // 완료 시 콜백 객체가 생성되며 response를 처리할 수 있다.
        call.enqueue(new Callback<UserInfoDto>() {
            // 응답을 받은 경우
            @Override
            public void onResponse(Call<UserInfoDto> call, Response<UserInfoDto> response) {
                // 응답 성공이라면
                if (response.isSuccessful()){
                    // LiveData에 받아온 사용자 값을 입력한다.
                    userInfo.postValue(response.body());
                }
            }

            // 통신 실패했을 때
            @Override
            public void onFailure(Call<UserInfoDto> call, Throwable t) {

            }
        });
    }
}
