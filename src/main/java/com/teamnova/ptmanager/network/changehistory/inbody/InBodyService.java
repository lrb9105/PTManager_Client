package com.teamnova.ptmanager.network.changehistory.inbody;

import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyCompare;
import com.teamnova.ptmanager.model.changehistory.inbody.InBody;
import com.teamnova.ptmanager.model.changehistory.inbody.InBodyForGraph;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * 눈바디관련 통신의 인터페이스
 * */
public interface InBodyService {
    // 인바디 정보 저장
    @POST("inbody/registerInbodyInfo.php")
    Call<String> registerInBodyInfo(@Body InBody inBodyInfo);

    // 인바디 정보 저장
    @POST("inbody/modifyInBodyInfo.php")
    //Call<String> modifyInBodyInfo(@Body InBody inBodyInfo);
    Call<InBody> modifyInBodyInfo(@Body InBody inBodyInfo);

    // 인바디 정보 삭제
    @POST("inbody/deleteInBodyInfo.php")
    Call<String> deleteInBodyInfo(@Body String inBodyId);

    // 인바디 정보 가져오기
    @GET("inbody/getInbodyInfo.php")
    Call<ArrayList<InBody>> getInBodyInfo(@Query("userId") String userId, @Query("date") String date);

    // 인바디 리스트 가져오기
    @GET("inbody/getInbodyList.php")
    Call<InBodyForGraph> getInBodyList(@Query("userId") String userId, @Query("srtDate") String srtDate, @Query("endDate") String endDate);

    // 인바디 데이터 가지고 있는 날짜리스트 가져오기
    @GET("inbody/getInbodyDateList.php")
    Call<ArrayList<String>> getInBodyDateList(@Query("userId") String userId);
}
