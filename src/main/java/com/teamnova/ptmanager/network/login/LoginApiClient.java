package com.teamnova.ptmanager.network.login;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.IdPwDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.test.TestDTO;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.test.TestService;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginApiClient {
    // api 서버와 통신을 하기위한 통신객체
    private Retrofit retrofit;

    public LoginApiClient() {
        retrofit = RetrofitInstance.getRetroClient();
    }

    // 로그인
    public void login( MutableLiveData<String> loginResult, String id, String pw){
        // 웹서비스 구현체 생성
        LoginService service = retrofit.create(LoginService.class);

        // http request 생성 > 각각의 Call이 http Request이다.
        Call<String> call = service.loginUser(id, pw);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    String result = response.body();

                    Log.d("222", result);

                    loginResult.postValue(result);
                } else{
                    Log.d("222", "111");
                    loginResult.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("로그인실패: ", t.getMessage());
                Log.d("로그인실패: ", t.getCause().getMessage());
                loginResult.postValue(null);

            }
        });
    }

    // 아이디 가져오기
    public void getLoginId(Handler handler, String name, String phoneNum){
        // 웹서비스 구현체 생성
        LoginService service = retrofit.create(LoginService.class);

        // http request 생성 > 각각의 Call이 http Request이다.
        Call<String> call = service.getLoginId(name, phoneNum);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    String loginId = response.body();

                    Log.d("로그인아이디 가져오기 통신결과:", loginId);

                    Message msg = handler.obtainMessage(0, loginId);
                    handler.sendMessage(msg);
                } else{
                    Log.d("로그인아이디 가져오기 통신결과:", "없음");

                    Message msg = handler.obtainMessage(0, null);
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                Message msg = handler.obtainMessage(0, null);
                handler.sendMessage(msg);
            }
        });
    }

    // 유효한 아이디인지 확인
    public void checkIsValidId(Handler handler, String loginId){
        // 웹서비스 구현체 생성
        LoginService service = retrofit.create(LoginService.class);

        // http request 생성 > 각각의 Call이 http Request이다.
        Call<String> call = service.checkIsValidId(loginId);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // debug: db에 아이디가 없을 때 db를 조회해서 뭐라도 가져오는지 확인
                Log.d("db에 데이터 없을 때 뭐라도 가져옴: ", response.body());

                if (response.isSuccessful()){
                    String loginId = response.body();
                    // debug: db에 아이디가 없을 때 db를 조회해서 통신에 성공하는지 확인
                    Log.d("로그인아이디 가져오기 통신결과:", loginId);

                    Message msg = handler.obtainMessage(2, loginId);
                    handler.sendMessage(msg);
                } else{
                    // debug: db에 아이디가 없을 때 db를 조회해서 통신에 실패하는지
                    Log.d("로그인아이디 가져오기 통신결과:", "통신 실패");

                    Message msg = handler.obtainMessage(2, null);
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // debug: db에 아이디가 없을 때 db를 조회해서 통신에 실패하는지
                Log.d("로그인아이디 가져오기 통신결과:", "onFailure 통신 실패");
                Message msg = handler.obtainMessage(0, null);
                handler.sendMessage(msg);
            }
        });
    }

    // 비밀번호 초기화(재설정)
    public void updatePw(Handler handler, String loginId, String pw){
        // 웹서비스 구현체 생성
        LoginService service = retrofit.create(LoginService.class);

        // http request 생성 > 각각의 Call이 http Request이다.
        IdPwDto idPwDto = new IdPwDto(loginId, pw);

        Call<String> call = service.updatePw(idPwDto);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    String result = response.body();

                    Log.d("비밀번호 초기화 통신결과:", result);

                    Message msg = handler.obtainMessage(0, result);
                    handler.sendMessage(msg);
                } else{
                    Log.d("비밀번호 초기화 통신결과:", "실패");

                    Message msg = handler.obtainMessage(0, null);
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                Message msg = handler.obtainMessage(0, null);
                handler.sendMessage(msg);
            }
        });
    }

    // 사용자 정보 가져오기
    public void getUserInfo(MutableLiveData<UserInfoDto> userInfo, String id, int type) {
        Log.d("사용자정보 가져오기", "11");

        // API서버와 통신하기 위한 인터페이스
        LoginService service = retrofit.create(LoginService.class);

        // 결과객체 UserInfoDto - 로그인한 사용자 정보 가져오기
        if(type == 0){
            Call<UserInfoDto> call = service.getUserInfo(id);
            Log.d("사용자정보 가져오기", "22");
            // enqueue 메소드는 비동기방식으로 데이터를 가져온다.
            // 완료 시 콜백 객체가 생성되며 response를 처리할 수 있다.
            call.enqueue(new Callback<UserInfoDto>() {
                // 응답을 받은 경우
                @Override
                public void onResponse(Call<UserInfoDto> call, Response<UserInfoDto> response) {
                    // 응답 성공이라면
                    if (response.isSuccessful()){
                        // LiveData에 받아온 사용자 값을 입력한다.
                        Log.d("사용자정보 가져오기", "44");
                        userInfo.postValue(response.body());
                    }
                }

                // 통신 실패했을 때
                @Override
                public void onFailure(Call<UserInfoDto> call, Throwable t) {
                    Log.d("사용자정보 가져오기", "55");
                }
            });
        }else{
            Log.d("사용자정보 가져오기", "33");
        }
    }
}
