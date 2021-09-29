package com.teamnova.ptmanager.network.record.meal;

import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.changehistory.inbody.InBody;
import com.teamnova.ptmanager.model.changehistory.inbody.InBodyForGraph;
import com.teamnova.ptmanager.model.record.meal.Meal;
import com.teamnova.ptmanager.model.record.meal.MealDateWithCount;
import com.teamnova.ptmanager.model.record.meal.MealHistoryInfo;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * 식사관련 통신의 인터페이스
 * */
public interface MealService {
    // 식사 정보 저장
    @Multipart
    @POST("meal/registerMealInfo.php")
    /*Call<String> registerMealInfo(@Part("userId") RequestBody userId,
                                  @Part("mealDate") RequestBody mealDate,
                                  @Part("mealType") RequestBody mealType,
                                  @Part("mealContents") RequestBody mealContents,
                                  @Part ArrayList<MultipartBody.Part> mealFile);*/

   Call<Meal> registerMealInfo(@Part("userId") RequestBody userId,
                                  @Part("mealDate") RequestBody mealDate,
                                  @Part("mealType") RequestBody mealType,
                                  @Part("mealContents") RequestBody mealContents,
                                  @Part ArrayList<MultipartBody.Part> mealFile);

   // 식사정보 수정
    @Multipart
    @POST("meal/modifyMealInfo.php")
    Call<Meal> modifyMealInfo(@Part("mealId") RequestBody mealId,
                                @Part("mealContents") RequestBody mealContents,
                                @Part("isImgChanged") RequestBody isImgChanged,
                                @Part ArrayList<MultipartBody.Part> mealFile);
    /*Call<String> modifyMealInfo(@Part("mealId") RequestBody mealId,
                              @Part("mealContents") RequestBody mealContents,
                              @Part("isImgChanged") RequestBody isImgChanged,
                              @Part ArrayList<MultipartBody.Part> mealFile);*/


    // 특정일의 식사 정보 가져오기
    @GET("meal/getMealInfoById.php")
    Call<Meal> getMealInfoById(@Query("mealId") String mealId);

    // 식사정보 삭제
    @POST("meal/deleteMealInfo.php")
    Call<String> deleteMealInfo(@Body String mealId);


    // 식사 데이터 가지고 있는 날짜리스트 가져오기
    @GET("meal/getMealDateList.php")
    Call<ArrayList<MealDateWithCount>> getMealDateList(@Query("userId") String userId);
    //Call<String> getMealDateList(@Query("userId") String userId);

    // 홈화면에서 사용할 식사 리스트 가져오기
    @GET("meal/getMealHistoryInfo.php")
    Call<MealHistoryInfo> getMealList(@Query("userId") String userId, @Query("date") String date);

    // 모아보기 화면에서 사용할 전체 식사기록 가져오기
    @GET("meal/getMealList.php")
    Call<ArrayList<Meal>> getMealListAll(@Query("userId") String userId);
}
