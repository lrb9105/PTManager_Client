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
        Log.e("트레이너에게 수강중인 회원 리스트 가져오기 ", "1. FriendService 구현체 생성");

        // http request 객체 생성
        Call<ArrayList<FriendInfoDto>> call = service.getFriendsList(ownerId);
        Log.e("트레이너에게 수강중인 회원 리스트 가져오기 ", "2. call 객체 생성");


        call.enqueue(new Callback<ArrayList<FriendInfoDto>>() {
            @Override
            public void onResponse(Call<ArrayList<FriendInfoDto>> call, Response<ArrayList<FriendInfoDto>> response) {
                if (response.isSuccessful()){
                    ArrayList<FriendInfoDto> friendsList = response.body();
                    Log.e("트레이너에게 수강중인 회원 리스트 가져오기 ", "3. 서버에서 가져온 수강중인 회원 목록의 사이즈 => " + friendsList.size());

                    Message msg = handler.obtainMessage(0, friendsList);

                    Log.e("트레이너에게 수강중인 회원 리스트 가져오기 ", "4. 핸들러에게 가져온 회원목록 넘겨줌");
                    handler.sendMessage(msg);
                } else{
                    Log.e("트레이너에게 수강중인 회원 리스트 가져오기 ", "3. 수강중인 회원 리스트 못 가져옴");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<FriendInfoDto>> call, Throwable t) {
                Log.e("트레이너에게 수강중인 회원 리스트 가져오기 ", "3. 수강중인 회원 리스트 가져오기 실패 => 이유: " + t.getMessage());

                Message msg = handler.obtainMessage(0, null);
                Log.e("트레이너에게 수강중인 회원 리스트 가져오기 ", "4. 수강중인 회원 리스트 가져오기 실패 후 핸들러에게 null 넘겨 줌!");

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
