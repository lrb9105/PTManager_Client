package com.teamnova.ptmanager.network.login;

import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.IdPwDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.test.TestDTO;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface LoginService {
    @FormUrlEncoded
    @POST("login/Login.php")
    Call<String> loginUser(@Field("id") String id, @Field("pw") String pw);

    @GET("login/getLoginId.php")
    Call<String> getLoginId(@Query("name") String name, @Query("phoneNum") String phoneNum);

    @GET("login/checkIsValidId.php")
    Call<String> checkIsValidId(@Query("loginId") String loginId);

    @PUT("login/updatePw.php")
    Call<String> updatePw(@Body IdPwDto idPwDto);

    @GET("login/getLoginUserInfo.php")
    Call<UserInfoDto> getUserInfo(@Query("id") String id);

    @GET("login/getMemberInfo.php")
    Call<UserInfoDto> getMemberInfoFromServer(@Query("id") String id);

}
