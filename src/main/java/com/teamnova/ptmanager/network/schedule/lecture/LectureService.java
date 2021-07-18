package com.teamnova.ptmanager.network.schedule.lecture;

import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.lecture.pass.PassInfoDto;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 강의 관련 웹서비스
 * */
public interface LectureService {
    /*@FormUrlEncoded
    @POST("friend/addToFriend.php")
    Call<String> addToFriend(@Field("ownerId") String ownerId, @Field("ownerType") String ownerType, @Field("friendId") String friendId, @Field("friendType") String friendType);
*/
    // 강의목록 가져오기
    @GET("lecture/getLectureList.php")
    Call<ArrayList<LectureInfoDto>> getLectureList(@Query("trainerLoginId") String trainerLoginId);

    // 강의에 등록된 회원정보 가져오기
    @GET("lecture/getLectureRegisteredMemberList.php")
    Call<ArrayList<FriendInfoDto>> getLectureRegisteredMemberList(@Query("lectureId") String lectureId);

    // 레슨을 수강하기로 한 회원정보 가져오기
    @GET("lecture/getLessonRegisteredMemberList.php")
    Call<ArrayList<FriendInfoDto>> getLessonRegisteredMemberList(@Query("lessonId") String lessonId);

    // 수강권 정보 저장
    @POST("lecture/pass/registerPassInfo.php")
    Call<String> registerPassInto(@Body PassInfoDto passInfo);

    // 수강권정보 가져오기
    @GET("lecture/pass/getLecturePassInfo.php")
    Call<PassInfoDto> getLecturePassInfo(@Query("memberId") String memberId);

    /*@GET("friend/getMemberInfo.php")
    Call<FriendInfoDto> getFriendInfo(@Query("friendId") String friendId);*/
}
