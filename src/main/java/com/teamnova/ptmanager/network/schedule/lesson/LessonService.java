package com.teamnova.ptmanager.network.schedule.lesson;

import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.lecture.pass.PassInfoDto;
import com.teamnova.ptmanager.model.lesson.AttendanceInfo;
import com.teamnova.ptmanager.model.lesson.LessonInfo;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.reservation.ReservationInfo;
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
 * 레슨 관련 웹서비스
 * */
public interface LessonService {
    // 레슨 정보 저장
    @POST("lesson/registerLessonInfo.php")
    Call<String> registerLessonInfo(@Body LessonInfo lessonInfo);

    // 레슨리스트 정보 가져오기
    @GET("lesson/getLessonSchList.php")
    Call<ArrayList<LessonSchInfo>> getLessonList(@Query("trainerId") String trainerLoginId, @Query("yearMonth") String yearMonth);

    // 회원 레슨리스트 정보 가져오기
    @GET("lesson/getLessonSchListByMember.php")
    Call<ArrayList<LessonSchInfo>> getLessonListByMember(@Query("memberId") String memberId);

    // 레슨정보 가져오기
    @GET("lesson/getLessonSchInfo.php")
    Call<LessonSchInfo> getLessonSchInfo(@Query("lessonSchId") String lessonSchId);

    // 레슨 정보 저장
    @POST("lesson/checkAttendance.php")
    Call<String> checkAttendance(@Body ArrayList<AttendanceInfo> attendanceInfo);

    // 예약 정보 저장
    @POST("lesson/registerReservationInfo.php")
    Call<LessonSchInfo> registerReservation(@Body LessonInfo lessonInfo);

    // 예약목록 정보 가져오기
    @GET("lesson/getReservedMemberList.php")
    Call<ArrayList<ReservationInfo>> getReservedMemberList(@Query("trainerId") String trainerId);

    // 레슨 취소 요청
    @FormUrlEncoded
    @POST("lesson/cancelLesson.php")
    Call<String> requestCancelLesson(@Field("lessonSchId") String lessonSchId, @Field("cancelReason") String cancelReason);

    // 레슨 예약 승인/거절
    @POST("lesson/approveReservation.php")
    Call<String> approveReservation(@Body ArrayList<ReservationInfo> reservationList);




    // 예약 정보 저장 테스트
    /*@POST("lesson/registerReservationInfo.php")
    Call<String> registerReservation(@Body LessonInfo lessonInfo);*/





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
