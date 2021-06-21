package com.teamnova.ptmanager.network.schedule.lesson;

import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.lecture.pass.PassInfoDto;
import com.teamnova.ptmanager.model.lesson.LessonInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 레슨 관련 웹서비스
 * */
public interface LessonService {
    // 레슨 정보 저장
    @POST("lesson/registerLessonInfo.php")
    Call<String> registerLessonInfo(@Body LessonInfo lessonInfo);

    /*// 강의목록 가져오기
    @GET("lecture/getLectureList.php")
    Call<ArrayList<LectureInfoDto>> getLectureList(@Query("trainerLoginId") String trainerLoginId);

    // 강의에 등록된 회원정보 가져오기
    @GET("lecture/getLectureRegisteredMemberList.php")
    Call<ArrayList<FriendInfoDto>> getLectureRegisteredMemberList(@Query("lectureId") String lectureId);

    // 레슨을 수강하기로 한 회원정보 가져오기
    @GET("lecture/getLessonRegisteredMemberList.php")
    Call<ArrayList<FriendInfoDto>> getLessonRegisteredMemberList(@Query("lessonId") String lessonId);*/

}
