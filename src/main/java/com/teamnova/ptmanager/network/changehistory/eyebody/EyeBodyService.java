package com.teamnova.ptmanager.network.changehistory.eyebody;

import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyCompare;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * 눈바디관련 통신의 인터페이스
 * */
public interface EyeBodyService {
    // 눈바디 정보 저장
    @Multipart
    @POST("eyebody/registerEyeBodyInfo.php")
    Call<EyeBody> registerEyeBodyInfo(@Part("userId") RequestBody loginId, @Part MultipartBody.Part eyeBodyFile);

    // 눈바디 비교정보 저장
    @POST("eyebody/registerEyeBodyCompareInfo.php")
    Call<String> registerEyeBodyCompareInfo(@Body ArrayList<EyeBody> addedEyeBodyList);

    // 눈바디 변화정보 가져오기
    @GET("eyebody/getEyeBodyList.php")
    Call<ArrayList<EyeBody>> getEyeBodyList(@Query("userId") String userId);

    // 눈바디 비교정보 가져오기
    @GET("eyebody/getEyeBodyCompareList.php")
    Call<ArrayList<EyeBodyCompare>> getEyeBodyCompareList(@Query("userId") String userId);

    // 눈바디 변화정보 삭제하기
    @POST("eyebody/removeEyeBodyInfo.php")
    Call<String> deleteEyeBodyInfo(@Body String eyeBodyChangeId);

    // 눈바디 비교정보 삭제하기
    @POST("eyebody/deleteEyeBodyCompareInfo.php")
    Call<String> deleteEyeBodyCompareInfo(@Body String eyeBodyCompareId);

}
