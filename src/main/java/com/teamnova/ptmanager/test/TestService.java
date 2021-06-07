package com.teamnova.ptmanager.test;

import com.teamnova.ptmanager.model.userInfo.UserInfoDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TestService {
    @GET("test/getUserInfo.php")
    Call<UserInfoDto> getUserInfo(@Query("id") String id);
}
