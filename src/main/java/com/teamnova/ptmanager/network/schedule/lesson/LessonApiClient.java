package com.teamnova.ptmanager.network.schedule.lesson;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.lecture.pass.PassInfoDto;
import com.teamnova.ptmanager.model.lesson.LessonInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.schedule.lecture.LectureService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LessonApiClient {
    // api 서버와 통신을 하기위한 통신객체
    private Retrofit retrofit;

    public LessonApiClient() {
        retrofit = RetrofitInstance.getRetroClient();
    }

    // 레슨 정보 저장하기
    public void registerLessonInfo(Handler handler, LessonInfo lessonInfo){
        // 웹서비스 구현체 생성
        LessonService service = retrofit.create(LessonService.class);

        // http request 객체 생성
        Call<String> call = service.registerLessonInfo(lessonInfo);

        // 수강권 정보 등록하기
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    // 레슨 정보 등록
                    String result = response.body();

                    Log.d("레슨 정보 등록 성공:", "성공!");
                    Log.d("레슨 정보 등록 정보:", result);

                    Message msg = handler.obtainMessage(0, result);
                    handler.sendMessage(msg);
                } else{
                    Log.d("레슨 정보 등록 실패:", response.message());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("레슨 정보 등록 통신 실패:", t.getMessage());

                Message msg = handler.obtainMessage(3, null);
                handler.sendMessage(msg);
            }
        });
    }

    /*// 강의목록 가져오기
    public void getLectureList(Handler handler, String trainerId){
        // 웹서비스 구현체 생성
        LectureService service = retrofit.create(LectureService.class);

        // http request 객체 생성
        Call<ArrayList<LectureInfoDto>> call = service.getLectureList(trainerId);

        // 강의정보 가져오기
        call.enqueue(new Callback<ArrayList<LectureInfoDto>>() {
            @Override
            public void onResponse(Call<ArrayList<LectureInfoDto>> call, Response<ArrayList<LectureInfoDto>> response) {
                if (response.isSuccessful()){
                    ArrayList<LectureInfoDto> lectureList = response.body();

                    if(lectureList != null){
                        Log.d("강의목록 가져오기 결과 USER_ID:", lectureList.get(0).getLectureName());
                    }

                    Message msg = handler.obtainMessage(0, lectureList);
                    handler.sendMessage(msg);
                } else{
                    Log.d("강의목록 가져오기 결과1:", "실패1111");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<LectureInfoDto>> call, Throwable t) {
                Log.d("강의목록 가져오기 결과2:", t.getMessage());

                Message msg = handler.obtainMessage(0, null);
                handler.sendMessage(msg);
            }
        });
    }

    // 강의를 수강할 수 있는 회원목록 가져오기
    public void getLectureRegisteredMemberList(Handler handler, String lectureId){
        // 웹서비스 구현체 생성
        LectureService service = retrofit.create(LectureService.class);

        // http request 객체 생성
        Call<ArrayList<FriendInfoDto>> call = service.getLectureRegisteredMemberList(lectureId);

        // 강의를 수강할 수 있는 회원목록 가져오기
        call.enqueue(new Callback<ArrayList<FriendInfoDto>>() {
            @Override
            public void onResponse(Call<ArrayList<FriendInfoDto>> call, Response<ArrayList<FriendInfoDto>> response) {
                if (response.isSuccessful()){
                    // 강의를 수강할 수 있는 회원정보 목록
                    ArrayList<FriendInfoDto> registeredMemberList = response.body();

                    if(registeredMemberList != null){
                        Log.d("수강가능한 회원목록 가져오기 결과 USER_ID:", registeredMemberList.get(0).getUserId());
                    }

                    Message msg = handler.obtainMessage(1, registeredMemberList);
                    handler.sendMessage(msg);
                } else{
                    Log.d("수강가능한 회원목록 결과1:", "실패1111");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<FriendInfoDto>> call, Throwable t) {
                Log.d("수강가능한 회원목록 결과2:", t.getMessage());

                Message msg = handler.obtainMessage(1, null);
                handler.sendMessage(msg);
            }
        });
    }

    // 레슨을 수강할 수 있는 회원목록 가져오기
    public void getLessonRegisteredMemberList(Handler handler, String lessonId){
        // 웹서비스 구현체 생성
        LectureService service = retrofit.create(LectureService.class);

        // http request 객체 생성
        Call<ArrayList<FriendInfoDto>> call = service.getLessonRegisteredMemberList(lessonId);

        // 레슨을 수강할 수 있는 회원목록 가져오기
        call.enqueue(new Callback<ArrayList<FriendInfoDto>>() {
            @Override
            public void onResponse(Call<ArrayList<FriendInfoDto>> call, Response<ArrayList<FriendInfoDto>> response) {
                if (response.isSuccessful()){
                    // 레슨을 수강할 수 있는 회원정보 목록
                    ArrayList<FriendInfoDto> registeredMemberList = response.body();

                    if(registeredMemberList != null){
                        Log.d("레슨가능한 회원목록 가져오기 결과 USER_ID:", registeredMemberList.get(0).getUserId());
                    }

                    Message msg = handler.obtainMessage(2, registeredMemberList);
                    handler.sendMessage(msg);
                } else{
                    Log.d("레슨가능한 회원목록 결과1:", "실패1111");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<FriendInfoDto>> call, Throwable t) {
                Log.d("레슨가능한 회원목록 결과2:", t.getMessage());

                Message msg = handler.obtainMessage(2, null);
                handler.sendMessage(msg);
            }
        });
    }*/

    
    /*// 친구목록에 추가
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
    }*/

    /*// 친구정보 가져오기
    public void getFriendInfo(MutableLiveData<LectureInfoDto> friendInfo, String friendId){
        // 웹서비스 구현체 생성
        FriendService service = retrofit.create(FriendService.class);

        // http request 객체 생성
        Call<LectureInfoDto> call = service.getFriendInfo(friendId);

        call.enqueue(new Callback<LectureInfoDto>() {
            @Override
            public void onResponse(Call<LectureInfoDto> call, Response<LectureInfoDto> response) {
                if (response.isSuccessful()){
                    friendInfo.postValue(response.body());

                } else{
                    Log.d("회원정보 가져오기 onResponse:", "실패");
                    friendInfo.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<LectureInfoDto> call, Throwable t) {
                Log.d("회원정보 가져오기 onFailure:", "실패");
                Log.d("회원정보 가져오기 onFailure:", t.getMessage());

            }
        });
    }*/
}
