package com.teamnova.ptmanager.network.record.fitness;

import com.teamnova.ptmanager.model.changehistory.inbody.InBody;
import com.teamnova.ptmanager.model.changehistory.inbody.InBodyForGraph;
import com.teamnova.ptmanager.model.record.fitness.FavoriteReq;
import com.teamnova.ptmanager.model.record.fitness.FitnessKinds;
import com.teamnova.ptmanager.model.record.fitness.FitnessRecord;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 운동기록 관련 통신의 인터페이스
 * */
public interface FitnessService {
    // 운동기록 정보 저장
    @POST("fitness/registerFitnessInfo.php")
    Call<String> registerFitnessInfo(@Body FitnessRecord fitnessInfo);

    // 운동기록 정보 수정
    @POST("fitness/modifyFitnessInfo.php")
    //Call<String> modifyFitnessInfo(@Body FitnessRecord FitnessRecord);
    Call<String> modifyFitnessInfo(@Body FitnessRecord FitnessRecord);

    // 운동기록 정보 삭제
    @POST("fitness/deleteFitnessInfo.php")
    Call<String> deleteFitnessInfo(@Body String inBodyId);

    // 날짜별 운동기록 정보 가져오기
    @GET("fitness/getFitnessInfoByDay.php")
    Call<ArrayList<FitnessRecord>> getFitnessInfoByDay(@Query("userId") String userId, @Query("fitnessDate") String fitnessDate);

    // 운동기록id별 운동기록 정보 가져오기
    @GET("fitness/getFitnessInfoById.php")
    Call<FitnessRecord> getFitnessInfoById(@Query("fitnessRecordId") String fitnessRecordId);

    // 운동종류 리스트 가져오기
    @GET("fitness/getFitnessKindsList.php")
    Call<ArrayList<FitnessKinds>> getFitnessKindsList(@Query("userId") String userId);

    // 운동종류 리스트 가져오기
    @GET("fitness/getFitnessKindsList.php")
    Call<String> getFitnessKindsList2();

    // 운동기록 데이터 가지고 있는 날짜리스트 가져오기
    @GET("fitness/getFitnessDateList.php")
    Call<ArrayList<String>> getFitnessDateList(@Query("userId") String userId);

    // 커스텀 운동정보 저장
    @POST("fitness/registerCustomFitness.php")
    Call<String> registerCustomFitness(@Body FitnessKinds customFitnessInfo);

    // 즐겨찾기 정보 수정
    @POST("fitness/modifyFavorite.php")
    Call<String> modifyFavorite(@Body FavoriteReq favoriteReq);

    // 운동 기록 저장
    @POST("fitness/registerFitnessRecordList.php")
    Call<String> registerFitnessRecordList(@Body ArrayList<FitnessRecord> fitnessRecordList);

    // 운동 기록 수정
    @POST("fitness/modifyFitnessRecordList.php")
    Call<String> modifyFitnessRecordList(@Body ArrayList<FitnessRecord> fitnessRecordList);

    // 운동 기록 삭제
    @POST("fitness/deleteFitnessRecordList.php")
    Call<String> deleteFitnessRecordList(@Body ArrayList<FitnessRecord> fitnessRecordList);

    // 커스텀 운동 기록 수정
    @POST("fitness/modifyCustomFitnessInfo.php")
    Call<String> modifyCustomFitness(@Body FitnessKinds fitnessInfo);

    // 커스텀 운동 기록 삭제
    @POST("fitness/deleteCustomFitnessInfo.php")
    Call<String> deleteCustomFitness(@Body String fitnessKindsId);

    // 커스텀 운동기록 정보 가져오기
    @GET("fitness/getCustomFitness.php")
    //Call<String> getCustomFitness(@Query("fitnessKindsId") String fitnessKindsId);
    Call<FitnessKinds> getCustomFitness(@Query("fitnessKindsId") String fitnessKindsId);

}
