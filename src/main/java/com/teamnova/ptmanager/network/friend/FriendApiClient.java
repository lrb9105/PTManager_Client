package com.teamnova.ptmanager.network.friend;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.teamnova.ptmanager.model.userInfo.IdPwDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.login.LoginService;

import java.lang.reflect.Array;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendApiClient {
    // api 서버와 통신을 하기위한 통신객체
    private Retrofit retrofit;

    public FriendApiClient() {
        retrofit = RetrofitInstance.getRetroClient();
    }

    // 친구목록에 추가
    public void addToFriend(Handler handler, String ownerId, String ownerType, String friendId, String friendType){
        // 웹서비스 구현체 생성
        FriendService service = retrofit.create(FriendService.class);

        // http request 객체 생성
        Call<String> call = service.addToFriend(ownerId, ownerType, friendId, friendType);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    String result = response.body();

                    Log.d("검색한 회원 친구목록에 추가한 결과:", result);

                    Message msg = handler.obtainMessage(0, result);
                    handler.sendMessage(msg);
                } else{
                    Log.d("검색한 회원 친구목록에 추가한 결과:", "실패");

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

    // 친구목록 가져오기
    public void getFriendsList(Handler handler, String ownerId){
        // 웹서비스 구현체 생성
        FriendService service = retrofit.create(FriendService.class);

        // http request 객체 생성
        Call<ArrayList<FriendInfoDto>> call = service.getFriendsList(ownerId);

        call.enqueue(new Callback<ArrayList<FriendInfoDto>>() {
            @Override
            public void onResponse(Call<ArrayList<FriendInfoDto>> call, Response<ArrayList<FriendInfoDto>> response) {
                if (response.isSuccessful()){
                    ArrayList<FriendInfoDto> friendsList = response.body();

                    Log.d("친구목록", friendsList.toString());

                    if(friendsList.size() > 0){
                        Log.d("회원목록 가져오기 결과 USER_ID:", friendsList.get(0).getUserId());
                    }

                    Message msg = handler.obtainMessage(0, friendsList);
                    handler.sendMessage(msg);
                } else{
                    Log.d("회원목록 가져오기 결과1:", "실패");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<FriendInfoDto>> call, Throwable t) {
                Log.d("회원목록 가져오기 결과2:", "실패");
                Log.d("회원목록 가져오기 결과2:", t.getMessage());

                Message msg = handler.obtainMessage(0, null);
                handler.sendMessage(msg);
            }
        });
    }

    // 친구정보 가져오기
    public void getFriendInfo(MutableLiveData<FriendInfoDto> friendInfo, String friendId){
        // 웹서비스 구현체 생성
        FriendService service = retrofit.create(FriendService.class);

        // http request 객체 생성
        Call<FriendInfoDto> call = service.getFriendInfo(friendId);

        call.enqueue(new Callback<FriendInfoDto>() {
            @Override
            public void onResponse(Call<FriendInfoDto> call, Response<FriendInfoDto> response) {
                if (response.isSuccessful()){
                    friendInfo.postValue(response.body());

                } else{
                    Log.d("회원정보 가져오기 onResponse:", "실패");
                    friendInfo.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<FriendInfoDto> call, Throwable t) {
                Log.d("회원정보 가져오기 onFailure:", "실패");
                Log.d("회원정보 가져오기 onFailure:", t.getMessage());

            }
        });
    }

    // 트레이너-회원 연결 완료
    public void completeConnect(String memberId){
        // 웹서비스 구현체 생성
        FriendService service = retrofit.create(FriendService.class);

        // http request 객체 생성
        Call<String> call = service.completeConnect(memberId);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    Log.d("연결완료 onResponse:", "성공!" + response.body());
                } else{
                    Log.d("연결완료 onResponse:", "실패");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("연결완료 onFailure:", "실패");
                Log.d("연결완료 onFailure:", t.getMessage());
            }
        });
    }
}
